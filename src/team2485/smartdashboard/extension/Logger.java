package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import team2485.smartdashboard.extension.util.NiceBorder;

/**
 * Data logger.
 * @author Bryce Matsumori
 */
public class Logger extends Widget {
    public static final String NAME = "Logger";
    public static final DataType[] TYPES = { DataType.STRING };

    static Object getLogger(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private JLabel label;
    private final ImageIcon expandIcon;
    private JCheckBoxMenuItem enabledMenuItem;

    private static final int MAX_LINES = 1000;
    private boolean enabled = true;
    private int count = 0, bufferCount = 0, lastCountUpdate = 0, markerCount = 0;
    private final StringBuffer buffer;

    private final DateFormat logDateFormat, fileDateFormat;
    private static final File LOG_HOME =
            new File(new File(System.getProperty("user.home")), "SmartDashboard" + File.separatorChar + "logs");

    public Logger() {
        buffer = new StringBuffer();
        logDateFormat  = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        fileDateFormat = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");

        expandIcon = new ImageIcon(getClass().getResource("/team2485/smartdashboard/extension/res/chevron.png"));

        LOG_HOME.mkdirs();
    }

    @Override
    public void init() {
        label = new JLabel("Logger");
        label.setForeground(Color.white);
        label.setBackground(Color.red);
        label.setFont(new Font("Ubuntu", Font.PLAIN, 14));
        label.setHorizontalAlignment(JLabel.CENTER);

        final JPopupMenu popup = new JPopupMenu();

        enabledMenuItem = new JCheckBoxMenuItem(new AbstractAction("Enabled") {
            @Override
            public void actionPerformed(ActionEvent e) {
                enabled = !enabled;
                label.setForeground(enabled ? Color.white : Color.red);
            }
        });
        enabledMenuItem.setSelected(true);
        popup.add(enabledMenuItem);
        popup.add(new JPopupMenu.Separator());

        popup.add(new JMenuItem(new AbstractAction("View Logs") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(LOG_HOME);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Save Logs Now") {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Add Marker") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = JOptionPane.showInputDialog(null, "Enter Marker Name:", "Logger", JOptionPane.PLAIN_MESSAGE);
                if (message == null) return;
                buffer.append(logDateFormat.format(new Date())).append('\t').append("MARKER\t").append(message).append('\n');
                markerCount++;
            }
        }));

        final JLabel expand = new JLabel(expandIcon);
        expand.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popup.show(expand, e.getX(), e.getY());
            }
        });

        final Dimension size = new Dimension(100, 28);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(new Dimension(300, 28));

        setBorder(new NiceBorder(Color.white, 1, 8));

        setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        add(label);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(expand);
    }

    @Override
    public void setValue(Object value) {
        if (!enabled) return;

        String str = (String)value;
        if (str.isEmpty()) return;

        final String timestamp = logDateFormat.format(new Date());

        // put date in before each entry
        int prev = 0, next = 0;
        while ((next = str.indexOf('\n', prev)) != -1) {
            buffer.append(timestamp).append('\t').append(str.substring(prev, next + 1));
            count++;
            bufferCount++;
            prev = next + 1; // after the \n
        }

        if (bufferCount > MAX_LINES) saveFile();

        if (count - lastCountUpdate >= 20) {
            label.setText(Integer.toString(count));
            lastCountUpdate = count;
        }
    }

    private void saveFile() {
        final File logFile = new File(LOG_HOME, fileDateFormat.format(new Date()) + (markerCount > 0 ? " " + markerCount + "M" : "") + ".log");

        try {
            final FileOutputStream stream   = new FileOutputStream(logFile);
            final OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(buffer.toString());
            writer.close();
            stream.close();
            System.out.println("Saved log file " + logFile.getName());
        } catch (IOException e) {
            System.err.println("Error saving log file " + logFile.getName() + "!");
            e.printStackTrace();
        }

        empty();
    }

    private void empty() {
        buffer.setLength(0);
        bufferCount = 0;
        markerCount = 0;
        System.gc();
    }

    @Override
    public void propertyChanged(Property property) {
    }
}
