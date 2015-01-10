package team2485.smartdashboard.extension;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import com.googlecode.javacv.cpp.opencv_imgproc;
import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.wpijavacv.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import team2485.smartdashboard.extension.util.*;

/**
 * Axis Camera Image Processing Widget
 * @author Bryce Matsumori, jrussell (Team 341: Miss Daisy)
 */
public class AxisCameraProcessor2 extends StaticWidget {
    public static final String NAME = "Axis Camera Processor";

    private enum TrackState {
        NONE(0), LEFT(1), RIGHT(2), BOTH(3);

        public final int id;
        TrackState(int id) {
            this.id = id;
        }
    }
    public enum ExposureMode { VISION, DRIVER }

    // State
    private boolean connected = false, process = true, showBinaryImage = false, showHsvTuner = false, holdExposure = true;
    private int
            hueMin = 39,  hueMax = 108,
            satMin = 111, satMax = 255,
            vibMin = 98,  vibMax = 255;
    private TrackState trackingState = TrackState.NONE;

    // SmartDashboard Properties
    public final IPAddressProperty ipProperty = new IPAddressProperty(this, "Camera IP Address",
        new int[] { 10, (DashboardPrefs.getInstance().team.getValue() / 100), (DashboardPrefs.getInstance().team.getValue() % 100), 11 });
    public final StringProperty userProperty = new StringProperty(this, "Axis Camera Username", "root"),
                                passProperty = new StringProperty(this, "Axis Camera Password", "root");
    public final BooleanProperty processProperty = new BooleanProperty(this, "Process Images", process),
                                 binaryImageProperty = new BooleanProperty(this, "Show Binary Image", showBinaryImage),
                                 hsvTunerProperty = new BooleanProperty(this, "Show HSV Tuner", showHsvTuner),
                                 holdExposureProperty = new BooleanProperty(this, "Hold Exposure", holdExposure);
    public final IntegerProperty
            hMinProp = new IntegerProperty(this, "Hue Min", hueMin),
            hMaxProp = new IntegerProperty(this, "Hue Max", hueMax),
            sMinProp = new IntegerProperty(this, "Saturation Min", satMin),
            sMaxProp = new IntegerProperty(this, "Saturation Max", satMax),
            vMinProp = new IntegerProperty(this, "Vibrance Min", vibMin),
            vMaxProp = new IntegerProperty(this, "Vibrance Max", vibMax);

    private NetworkTable table;

    // Image Processing
    private WPICamera cam;
    private BufferedImage drawnImage;
    private opencv_core.CvSize size = null;
    private WPIContour[] contours;
    private opencv_core.IplImage thresh, hsv, dilated;
    private static opencv_core.CvScalar min, max;
    private opencv_imgproc.IplConvKernel dilationElement;

    // UI
    private final JRadioButtonMenuItem holdExpMenuItem = new JRadioButtonMenuItem("Hold Exposure", true), autoExpMenuItem = new JRadioButtonMenuItem("Auto Exposure", false);
    private final JCheckBoxMenuItem binaryImageMenuItem = new JCheckBoxMenuItem("Show Binary Image", false), hsvTunerMenuItem = new JCheckBoxMenuItem("Show HSV Tuner", false);
    private final JMenu configMenu = new JMenu("Config");
    private final JMenuItem configSaveItem = new JMenuItem("Save Current");
    private CanvasFrame binaryPreview;
    private HSVTuner hsvTuner;
    private BufferedImage noneImage, leftImage, rightImage, bothImage, placeholderImage;
    private String errorMessage = "Connecting...";

    // Configuration
    private static final File
            SD_HOME          = new File(new File(System.getProperty("user.home")), "SmartDashboard"),
            AXIS_CONFIG_FILE = new File(SD_HOME, "axisconfig.properties");
    private final Properties props = new Properties();
    private final HashMap<String, HSVConfig> configs = new HashMap<>();

    private class GCThread extends Thread {
        boolean destroyed = false;

        public GCThread() {
            super("Camera GC");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!destroyed) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) { }
                System.gc();
            }
        }

        @Override
        public void destroy() {
            destroyed = true;
            interrupt();
        }
    }
    private final GCThread gcThread = new GCThread();

    private class BGThread extends Thread {
        boolean destroyed = false;

        public BGThread() {
            super("Camera Background");
            setDaemon(true);
        }

        @Override
        public void run() {
            WPIImage image;
            while (!destroyed) {
                if (cam == null) cam = new WPICamera(ipProperty.getSaveValue());

                try {
                    image = cam.getNewImage(5.0);
                    connected = true;

                    if (process) {
                        drawnImage = processImage((WPIColorImage) image).getBufferedImage();
                        table.putNumber("targets", trackingState.id);
                    }
                    else drawnImage = image.getBufferedImage();

                    repaint();
                } catch (final WPIFFmpegVideo.BadConnectionException e) {
                    connected = false;
                    errorMessage = "Could not connect to camera.";
                    System.err.println("* Could not getNewImage: WPIFFmpegVideo.BadConnectionException");
                    if (cam != null) cam.dispose();
                    cam = null;
                    drawnImage = null;
                    repaint();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) { }
                }

                table.putBoolean("connected", connected);
            }
        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
    private final BGThread bgThread = new BGThread();

    public AxisCameraProcessor2() {
        WPIExtensions.init();

        // Try loading the HSV configurations
        try {
            props.load(new FileInputStream(AXIS_CONFIG_FILE));
            for (String prop : props.stringPropertyNames()) {
                configs.put(prop, HSVConfig.parse(props.getProperty(prop)));
            }
        } catch (IOException ex) {
            System.err.println("Could not load Axis camera properties.");
        }
    }

    @Override
    public void init() {
        table = NetworkTable.getTable("vision");

        process         = processProperty.getValue();
        showBinaryImage = binaryImageProperty.getValue();
        showHsvTuner    = hsvTunerProperty.getValue();
        holdExposure    = holdExposureProperty.getValue();
        showHideBinaryImage();
        showHideHsvTuner();
        autoExpMenuItem.setSelected(!holdExposure);
        holdExpMenuItem.setSelected(holdExposure);

        hueMin = hMinProp.getValue();
        hueMax = hMaxProp.getValue();
        satMin = sMinProp.getValue();
        satMax = sMaxProp.getValue();
        vibMin = vMinProp.getValue();
        vibMax = vMaxProp.getValue();

        try {
            noneImage  = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/hot-none.png"));
            leftImage  = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/hot-left.png"));
            rightImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/hot-right.png"));
            bothImage  = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/hot-both.png"));
            placeholderImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/axis.png"));
        } catch (IOException e) { }

        final Dimension minSize = new Dimension(320, 240);
        setSize(minSize);
        setPreferredSize(minSize);
        setMinimumSize(minSize);
        setMaximumSize(new Dimension(800, 600));

        // <editor-fold defaultstate="collapsed" desc="Setup Menu Bar">

        final AxisCameraProcessor2 self = this;
        final JMenuBar bar = new JMenuBar();
        bar.setVisible(false);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bar.setVisible(false);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getY() < 20) {
                    bar.setVisible(true);
                }
            }
        });
        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (self.findComponentAt(e.getX(), e.getY()) != self)
                    bar.setVisible(false);
            }
        });
        final JMenu cameraMenu = new JMenu("Camera"), debugMenu = new JMenu("Debug");
        final JMenuItem refreshCameraItem = new JMenuItem("Refresh"), axisSettingsItem = new JMenuItem("Open Axis Settings");

        refreshCameraItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refreshing camera...");
                connected = false;
                if (cam != null) cam.dispose();
                cam = null;
                drawnImage = null;
            }
        });
        axisSettingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://" + ipProperty.getSaveValue() + "/operator/advanced.shtml"));
                } catch (URISyntaxException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        holdExpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExposureSettings(ExposureMode.VISION);
                holdExposure = true;
                holdExposureProperty.setValue(true);

                autoExpMenuItem.setSelected(false);
            }
        });
        autoExpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExposureSettings(ExposureMode.DRIVER);
                holdExposure = false;
                holdExposureProperty.setValue(false);

                holdExpMenuItem.setSelected(false);
            }
        });
        cameraMenu.add(refreshCameraItem);
        cameraMenu.add(axisSettingsItem);
        cameraMenu.add(new JPopupMenu.Separator());
        cameraMenu.add(holdExpMenuItem);
        cameraMenu.add(autoExpMenuItem);

        binaryImageMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBinaryImage = binaryImageMenuItem.isSelected();
                showHideBinaryImage();
                binaryImageProperty.setValue(showBinaryImage);
            }
        });
        hsvTunerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHsvTuner = hsvTunerMenuItem.isSelected();
                showHideHsvTuner();
                hsvTunerProperty.setValue(showHsvTuner);
            }
        });
        debugMenu.add(binaryImageMenuItem);
        debugMenu.add(hsvTunerMenuItem);

        configSaveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(null, "Enter configuration name.", "Axis Camera Processor", JOptionPane.PLAIN_MESSAGE);
                if (name == null) return;
                if (name.indexOf('=') != -1) {
                    JOptionPane.showMessageDialog(null, "Name cannot contain \"=\"!", "Axis Camera Processor", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                final HSVConfig config = new HSVConfig(hueMin, hueMax, satMin, satMax, vibMin, vibMax);
                configs.put(name, config);
                constructConfigMenu();

                props.setProperty(name, config.toString());
                saveProperties();
            }
        });
        constructConfigMenu();

        bar.add(cameraMenu);
        bar.add(debugMenu);
        bar.add(configMenu);
        setLayout(new BorderLayout());
        add(bar, BorderLayout.NORTH);

        // </editor-fold>

        bgThread.start();
        gcThread.start();
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == ipProperty) {
            if (cam != null) {
                cam.dispose();
            }
            try {
                cam = new WPICamera(ipProperty.getSaveValue());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not connect to camera with IP " + ipProperty.getSaveValue(), "Axis Camera Processor", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                connected = false;
                drawnImage = null;
                revalidate();
                repaint();
            }
        }
        else if (property == processProperty) {
            process = !property.hasValue() || (property.hasValue() && (Boolean)property.getValue()); // default or the value
        }
        else if (property == binaryImageProperty) {
            showBinaryImage = !property.hasValue() || (property.hasValue() && (Boolean)property.getValue()); // default or the value
            showHideBinaryImage();
        }
        else if (property == hsvTunerProperty) {
            showHsvTuner = !property.hasValue() || (property.hasValue() && (Boolean)property.getValue()); // default or the value
            showHideHsvTuner();
        }
        else if (property == holdExposureProperty) {
            holdExposure = !property.hasValue() || (property.hasValue() && (Boolean)property.getValue()); // default or the value
            updateExposureSettings(holdExposure ? ExposureMode.VISION : ExposureMode.DRIVER);

            autoExpMenuItem.setSelected(!holdExposure);
            holdExpMenuItem.setSelected(holdExposure);
        }
        else if (property == hMinProp && hMinProp.hasValue()) {
            hueMin = hMinProp.getValue();
            if (hsvTuner != null) hsvTuner.setHMin(hueMin);
        }
        else if (property == hMaxProp && hMaxProp.hasValue()) {
            hueMax = hMaxProp.getValue();
            if (hsvTuner != null) hsvTuner.setHMax(hueMax);
        }
        else if (property == sMinProp && sMinProp.hasValue()) {
            satMin = sMinProp.getValue();
            if (hsvTuner != null) hsvTuner.setSMin(satMin);
        }
        else if (property == sMaxProp && sMaxProp.hasValue()) {
            satMax = sMaxProp.getValue();
            if (hsvTuner != null) hsvTuner.setSMax(satMax);
        }
        else if (property == vMinProp && vMinProp.hasValue()) {
            vibMin = vMinProp.getValue();
            if (hsvTuner != null) hsvTuner.setVMin(vibMin);
        }
        else if (property == vMaxProp && vMaxProp.hasValue()) {
            vibMax = vMaxProp.getValue();
            if (hsvTuner != null) hsvTuner.setVMax(vibMax);
        }
    }

    @Override
    public void disconnect() {
        bgThread.destroy();
        gcThread.destroy();
        if (cam != null) cam.dispose();

        super.disconnect();
    }

    @Override
    protected void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int width  = getBounds().width,
                  height = getBounds().height;

        if (connected && drawnImage != null) {
            // Calculate scaling
            final int imageWidth  = drawnImage.getWidth(),
                      imageHeight = drawnImage.getHeight();
            final double scale = Math.min((double)width / (double)imageWidth, (double)height / (double)imageHeight);
            final int imageX = (int)(width  - (scale * imageWidth))  / 2,
                      imageY = (int)(height - (scale * imageHeight)) / 2;

            // Draw camera image
            g.drawImage(drawnImage, imageX, imageY,
                    (int) ((width + scale * imageWidth) / 2), (int) (height + scale * imageHeight) / 2,
                    0, 0, imageWidth, imageHeight, null);

            // Draw overlays
            final int centerX = imageX + (int)((scale * imageWidth) * 0.5),
                      scaledImageHeight = (int)(scale * imageHeight);

            g.setColor(new Color(255, 0, 0, 128));
            g.drawLine(centerX + 2, imageY, centerX + 2, imageY + scaledImageHeight);
            g.drawLine(centerX - 3, imageY, centerX - 3, imageY + scaledImageHeight);

            g.setColor(new Color(0, 255, 0, 128));
            g.drawLine(centerX + 60, imageY, centerX + 60, imageY + scaledImageHeight);
            g.drawLine(centerX - 61, imageY, centerX - 61, imageY + scaledImageHeight);

            g.setColor(new Color(0, 0, 255, 128));
            g.drawLine(centerX + 110, imageY, centerX + 110, imageY + scaledImageHeight);
            g.drawLine(centerX - 111, imageY, centerX - 111, imageY + scaledImageHeight);

            if (process) {
                // Draw hot image
                BufferedImage hotImg = null;
                switch (trackingState) {
                    case NONE:  hotImg = noneImage;  break;
                    case LEFT:  hotImg = leftImage;  break;
                    case RIGHT: hotImg = rightImage; break;
                    case BOTH:  hotImg = bothImage;  break;
                }
                g.drawImage(hotImg, getWidth() / 2 - noneImage.getWidth() / 2, 0, null);
            }
        }
        else {
            // Calculate scaling
            final double scale = Math.min(width / 320.0, height / 240.0);
            final int
                imageX = (int)(width  - (scale * 320)) / 2,
                imageY = (int)(height - (scale * 240)) / 2,
                scaledImageWidth  = (int)(scale * 320),
                scaledImageHeight = (int)(scale * 240);

            // Draw placeholder
            g.setColor(new Color(255, 255, 255, 25));
            g.fillRoundRect(imageX, imageY, scaledImageWidth - 1, scaledImageHeight - 1, 10, 10);
            g.setColor(Color.white);
            g.drawRoundRect(imageX, imageY, scaledImageWidth - 1, scaledImageHeight - 1, 10, 10);
            g.drawImage(placeholderImage, imageX + scaledImageWidth / 2 - 36, imageY + scaledImageHeight / 2 - 54, null);

            if (errorMessage != null) {
                g.setFont(new Font("Ubuntu", Font.PLAIN, 14));
                g.drawString(errorMessage,
                        imageX + scaledImageWidth / 2 - g.getFontMetrics().stringWidth(errorMessage) / 2,
                        imageY + scaledImageHeight - 10);
            }
        }

        super.paintComponent(gg);
    }

    public WPIImage processImage(WPIColorImage rawImage) {
        // Refresh IplImage sizes if necessary
        if (size == null || size.width() != rawImage.getWidth() || size.height() != rawImage.getHeight()) {
            size = opencv_core.cvSize(rawImage.getWidth(), rawImage.getHeight());
            thresh = opencv_core.IplImage.create(size, 8, 1);
            hsv = opencv_core.IplImage.create(size, 8, 3);
            dilated = opencv_core.IplImage.create(size, 8, 1);
            dilationElement = opencv_imgproc.cvCreateStructuringElementEx(
                    8, 8, 0, 0,
                    opencv_imgproc.CV_SHAPE_ELLIPSE, null);
        }
        min = cvScalar(hueMin, satMin, vibMin, 0);
        max = cvScalar(hueMax, satMax, vibMax, 255);

        // Get the raw IplImage
        opencv_core.IplImage input = rawImage.getIplImage();

        // Set ROI
        // opencv_core.cvSetImageROI(input, opencv_core.cvRect(0, 0, size.width(), size.height()));

        // Convert to HSV color space
        opencv_imgproc.cvCvtColor(input, hsv, opencv_imgproc.CV_RGB2HSV);

        // Find the green areas
        opencv_core.cvInRangeS(hsv, min, max, thresh);

        // Expand the edges for accuracy
        opencv_imgproc.cvDilate(thresh, dilated, dilationElement, 2);

        if (showBinaryImage && binaryPreview != null)
            binaryPreview.showImage(dilated.getBufferedImage());

        // Find the contours!
        WPIBinaryImage binWpi = WPIExtensions.makeWPIBinaryImage(dilated);
        contours = WPIExtensions.findConvexContours(binWpi);

        boolean foundLeft = false, foundRight = false;

        for (WPIContour c : contours) {
            double area = opencv_imgproc.cvContourArea(c.getCVSeq(), opencv_core.CV_WHOLE_SEQ, 0);

            if (area < 200 || c.getWidth() < dilated.width() / 4) continue;

            final int rectCenterX = c.getX() + c.getWidth() / 2;

            if (rectCenterX < dilated.width() / 2)
                foundLeft = true;
            else
                foundRight = true;
        }

        if (foundLeft && foundRight) trackingState = TrackState.BOTH;
        else if (foundLeft) trackingState = TrackState.LEFT;
        else if (foundRight) trackingState = TrackState.RIGHT;
        else trackingState = TrackState.NONE;

        WPIExtensions.releaseMemory();

        return rawImage;
    }

    private void constructConfigMenu() {
        configMenu.removeAll();
        configMenu.add(configSaveItem);
        configMenu.add(new JPopupMenu.Separator());
        if (configs.isEmpty()) {
            final JMenuItem noConfigItem = new JMenuItem("(no configs)");
            noConfigItem.setEnabled(false);
            configMenu.add(noConfigItem);
        }
        else {
            Iterator<Map.Entry<String, HSVConfig>> iterator = configs.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, HSVConfig> next = iterator.next();
                final HSVConfig config = next.getValue();
                final JMenuItem configItem = new JMenuItem(next.getKey());
                configItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hMinProp.setValue(config.hMin);
                        hMaxProp.setValue(config.hMax);
                        sMinProp.setValue(config.sMin);
                        sMaxProp.setValue(config.sMax);
                        vMinProp.setValue(config.vMin);
                        vMaxProp.setValue(config.vMax);
                        JOptionPane.showMessageDialog(null,
                                String.format("%s\nH: %d - %d\nS: %d - %d\nV: %d - %d", next.getKey(), config.hMin, config.hMax, config.sMin, config.sMax, config.vMin, config.vMax),
                                "Axis Camera Processor", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                configMenu.add(configItem);
                configMenu.revalidate();
            }
        }
    }

    private void saveProperties() {
        try {
            AXIS_CONFIG_FILE.createNewFile();
            props.store(new FileOutputStream(AXIS_CONFIG_FILE), "Axis Camera Config");
        } catch (IOException ex) {
            System.err.println("Error saving Axis config file.");
            ex.printStackTrace();
        }
    }

    private void showHideBinaryImage() {
        if (showBinaryImage) {
            if (binaryPreview == null) {
                binaryPreview = new CanvasFrame("Binary Preview");
                binaryPreview.setAlwaysOnTop(true);
                binaryPreview.setSize(320, 240);
            }
            binaryPreview.setVisible(true);
        }
        else if (binaryPreview != null) {
            binaryPreview.setVisible(false);
        }

        binaryImageMenuItem.setSelected(showBinaryImage);
    }

    private void showHideHsvTuner() {
        if (showHsvTuner) {
            if (hsvTuner == null) {
                hsvTuner = new HSVTuner(new HSVTuner.HSVTunerChangeListener() {
                    @Override
                    public void hMinChanged(int newValue) {
                        hueMin = newValue;
                        hMinProp.setValue(newValue);
                    }
                    @Override
                    public void hMaxChanged(int newValue) {
                        hueMax = newValue;
                        hMaxProp.setValue(newValue);
                    }
                    @Override
                    public void sMinChanged(int newValue) {
                        satMin = newValue;
                        sMinProp.setValue(newValue);
                    }
                    @Override
                    public void sMaxChanged(int newValue) {
                        satMax = newValue;
                        sMaxProp.setValue(newValue);
                    }
                    @Override
                    public void vMinChanged(int newValue) {
                        vibMin = newValue;
                        vMinProp.setValue(newValue);
                    }
                    @Override
                    public void vMaxChanged(int newValue) {
                        vibMax = newValue;
                        vMaxProp.setValue(newValue);
                    }
                }, hueMin, hueMax, satMin, satMax, vibMin, vibMax);
            }
            hsvTuner.setVisible(true);
        }
        else if (hsvTuner != null) {
            hsvTuner.setVisible(false);
        }

        hsvTunerMenuItem.setSelected(showHsvTuner);
    }

    /**
     * Updates the exposure and white balance settings using the Axis VAPIX API.
     * @param mode The exposure mode.
     */
    public void updateExposureSettings(ExposureMode mode) {
        final String modeStr = mode == ExposureMode.VISION ? "hold" : "auto";

        new Thread(new Runnable() {
            @Override
            public void run() {
                // set exposure
                if (!doVapixParamUpdate("ImageSource.I0.Sensor.Exposure", modeStr))
                    JOptionPane.showMessageDialog(null, "Error setting exposure. See console for details.", "Axis Camera Processor", JOptionPane.ERROR_MESSAGE);
                // set white balance
                else if (!doVapixParamUpdate("ImageSource.I0.Sensor.WhiteBalance", modeStr))
                    JOptionPane.showMessageDialog(null, "Error setting white balance. See console for details.", "Axis Camera Processor", JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "Exposure and white balance set to \"" + modeStr + "\".", "Axis Camera Processor", JOptionPane.INFORMATION_MESSAGE);
            }
        }).start();
    }

    /**
     * Makes a standard VAPIX parameter update request.
     *
     * A VAPIX HTTP request looks like this:
     * http://root:root@10.24.85.11/axis-cgi/param.cgi?action=update&ImageSource.I0.Sensor.Exposure=hold
     *
     * For more info, see:
     * http://www.axis.com/files/manuals/vapix_capture_mode_1_2_en_1307.pdf
     * http://www.axis.com/techsup/cam_servers/dev/cam_param_2.php#parameter_blocks_jpeg_mjpeg_imagesource_i#
     *
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return Success?
     */
    private boolean doVapixParamUpdate(String property, String value) {
        try {
            URL url = new URL("http://" + ipProperty.getSaveValue() + "/axis-cgi/param.cgi?action=update&" + property + "=" + value);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + Base64Encoder.encode(userProperty.getValue() + ":" + passProperty.getValue()));
            conn.connect();
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200; // success
        } catch (IOException e) {
            e.printStackTrace();
           return false;
        }
    }
}
