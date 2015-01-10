package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.gui.DashboardPanel;
import edu.wpi.first.smartdashboard.gui.DashboardPrefs;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class LayoutSwitcher extends StaticWidget {
    public static final String NAME = "Display > Layout Switcher";

    private DashboardFrame dash;
    private JMenuBar menuBar;
    private DashboardPanel dashPanel;
    private DashboardPrefs dashPrefs;
    private JMenu layoutMenu;

    private final Properties props = new Properties();

    private static final File
            SD_HOME     = new File(new File(System.getProperty("user.home")), "SmartDashboard"),
            LAYOUT_FILE = new File(SD_HOME, "layouts.properties");
    private NetworkTable table;

    private static LayoutSwitcher instance;
    public static LayoutSwitcher getInstance() {
        return instance;
    }

    public LayoutSwitcher() {
        // load layouts from file
        try {
            props.load(new FileInputStream(LAYOUT_FILE));
        } catch (IOException ex) {
            System.err.println("Could not load layouts from file.");
            ex.printStackTrace();
        }

        if (instance != null) throw new IllegalStateException("Layout Switcher widget already exists!");
        instance = this;
    }

    @Override
    protected void finalize() throws Throwable {
        instance = null;
    }

    @Override
    public void init() {
        // save load
        final LayoutSwitcher self = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dash = (DashboardFrame)SwingUtilities.getWindowAncestor(self);

                // lots of hacks here, oh well
                try {
                    Field menuField = dash.getClass().getDeclaredField("menuBar");
                    menuField.setAccessible(true); // muahahaha
                    menuBar = (JMenuBar)menuField.get(dash);

                    Field panelField = dash.getClass().getDeclaredField("smartDashboardPanel");
                    panelField.setAccessible(true);
                    dashPanel = (DashboardPanel)panelField.get(dash);

                    Field prefsField = dash.getClass().getDeclaredField("prefs");
                    prefsField.setAccessible(true);
                    dashPrefs = (DashboardPrefs)prefsField.get(dash);

                    layoutMenu = new JMenu("Layout");

                    layoutMenu.add(new JMenuItem(new AbstractAction("Add Layout") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final String name = JOptionPane.showInputDialog(null, "Enter layout name.", "Layout Switcher", JOptionPane.PLAIN_MESSAGE);
                            if (name == null) return;

                            final JFileChooser fileChooser = new JFileChooser(SD_HOME);
                            fileChooser.setMultiSelectionEnabled(false);
                            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

                            // add property
                            props.setProperty(name, fileChooser.getSelectedFile().getAbsolutePath());
                            layoutMenu.add(new JMenuItem(new AbstractAction(name) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setDashLayout(name);
                                }
                            }));

                            // save and update
                            try {
                                props.store(new FileOutputStream(LAYOUT_FILE), "Layout Switcher");
                            } catch (IOException ex) {
                                System.err.println("Error saving layouts.");
                                ex.printStackTrace();
                            }
                            setDashLayout(name);
                        }
                    }));
                    layoutMenu.add(new JPopupMenu.Separator());

                    // populate menu
                    for (final Object name : props.keySet()) {
                        layoutMenu.add(new JMenuItem(new AbstractAction((String)name) {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setDashLayout((String)name);
                            }
                        }));
                    }

                    menuBar.add(layoutMenu);
                    menuBar.validate();
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    System.err.println("Cannot access DashboardFrame menu.");
                    e.printStackTrace();
                }
            }
        });

        table = NetworkTable.getTable("SmartDashboard");
        table.addTableListener("sdTable", new ITableListener() {
            @Override
            public void valueChanged(ITable source, String key, Object value, boolean isNew) {
                setDashLayout((String)value);
            }
        }, true);
    }

    public void setDashLayout(String name) {
//        if (JOptionPane.showConfirmDialog(null, "Save the current layout?", "Layout Switcher", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            dash.save(props.getProperty(currentName));
//        }

        final String path = props.getProperty(name);
        if (path == null) return;

        dashPanel.clear();
        dash.load(path);
        dashPrefs.saveFile.setValue(path);
    }

    @Override
    public void propertyChanged(Property property) {
    }
}
