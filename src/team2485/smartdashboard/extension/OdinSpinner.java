package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class OdinSpinner extends Widget {
    public static final String NAME = "Display > Odin Spinner";
    public static final DataType[] TYPES = { DataType.BOOLEAN };

    private static final int PAUSE_FRAMES = 30;

    private boolean disabled = true;

    private BufferedImage hornsImage;
    private int frameHeight, numFrames, frame = 0;
    private Timer disabledTimer;

    @Override
    public void init() {
        try {
            hornsImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/horns154.png"));
        } catch (IOException e) {
            System.err.println("Error loading horns images.");
            e.printStackTrace();
        }

        frameHeight = hornsImage.getHeight();
        numFrames = hornsImage.getWidth() / frameHeight;

        final Dimension size = new Dimension(frameHeight, frameHeight);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        disabledTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                if (frame >= numFrames + PAUSE_FRAMES) frame = 0;
                if (frame < numFrames) repaint();
            }
        });
        disabledTimer.start();
    }

    @Override
    public void setValue(Object value) {
        disabled = (Boolean)value;

        if (disabled) disabledTimer.start();
        else disabledTimer.stop();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (disabled) {
            int minFrame = Math.min(frame, 27);
            g.drawImage(hornsImage, 0, 0, frameHeight, frameHeight, minFrame * frameHeight, 0, (minFrame + 1) * frameHeight, frameHeight, null);
        }
    }

    @Override
    public void propertyChanged(Property property) {
    }

}
