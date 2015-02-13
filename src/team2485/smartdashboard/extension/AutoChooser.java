package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Autonomous mode chooser
 * @author Bryce Matsumori
 */
public class AutoChooser extends StaticWidget implements IRemoteConnectionListener {
    public static final String NAME = "Autonomous Chooser";
    public static final DataType[] TYPES = { DataType.NUMBER };
    private static final String FIELD_NAME = "autoMode";

    private static final String[] MODES = new String[8];
    private static final int[] CODES = new int[8];
    static {
        MODES[0] = "Forward";           CODES[0] = 0;
        MODES[1] = "One Ball Left";     CODES[1] = 3;
        MODES[2] = "One Ball Right";    CODES[2] = 4;
        MODES[3] = "Two Ball No Hot";   CODES[3] = 14;
        MODES[4] = "Two Ball Hot";      CODES[4] = 12;
        MODES[7] = "Two Ball Goalie";   CODES[7] = 16;
        MODES[5] = "Three Ball No Hot"; CODES[5] = 13;
        MODES[6] = "Three Ball Hot";    CODES[6] = 15;
    }
    private int selectedIndex = 0;

    private final JPopupMenu popup;
    private boolean buttonPressed = false;
    private final Icon buttonPressedIcon;
    private Icon buttonIcon = null;

    private NetworkTable table;

    public AutoChooser() {
        initComponents();

        popup = new JPopupMenu();
        for (int i = 0; i < MODES.length; i++) {
            final int index = i;
            popup.add(new JMenuItem(new AbstractAction(MODES[i]) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setIndex(CODES[index]);
                }
            }));
        }

    	// set the default to two ball hot
        setIndex(CODES[4]);

        buttonPressedIcon = new ImageIcon(getClass().getResource("/team2485/smartdashboard/extension/res/arrow-hover.png"));
    }

    @Override
    public void init() {
        table = NetworkTable.getTable("SmartDashboard");
        table.addTableListener(FIELD_NAME, new ITableListener() {
            @Override
            public void valueChanged(ITable source, String key, Object value, boolean isNew) {
                setIndex(((Number)value).intValue());
            }
        }, true);

        Robot.addConnectionListener(this, true);
    }

    private void setIndex(int index) {
        for (int i = 0; i < CODES.length; i++) {
            if (CODES[i] == index) {
                auto.setText(MODES[i]);
                selectedIndex = index;
                return;
            }
        }
        System.err.println("Index " + index + " not found");
    }

    @Override
    public void propertyChanged(Property property) {
    }

    @Override
    public void connected(IRemote remote) {
        if (table.getNumber(FIELD_NAME, -1) == -1) {
            // update the robot with this value when we first connect if it doesn't have anything
            sendButtonMousePressed(null);
        }
    }

    @Override
    public void disconnected(IRemote remote) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoLabel = new javax.swing.JLabel();
        sendButton = new javax.swing.JLabel();
        vSeparator = new javax.swing.JSeparator();
        auto = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setBorder(new team2485.smartdashboard.extension.util.NiceBorder(java.awt.Color.white, 1, 8, 0));
        setMaximumSize(new java.awt.Dimension(500, 55));
        setMinimumSize(new java.awt.Dimension(200, 55));

        autoLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        autoLabel.setForeground(new java.awt.Color(255, 255, 255));
        autoLabel.setText("Autonomous");

        sendButton.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        sendButton.setForeground(new java.awt.Color(255, 255, 255));
        sendButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/team2485/smartdashboard/extension/res/arrow.png"))); // NOI18N
        sendButton.setPreferredSize(new java.awt.Dimension(40, 40));
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sendButtonMousePressed(evt);
            }
        });

        vSeparator.setForeground(new java.awt.Color(255, 255, 255));
        vSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        vSeparator.setPreferredSize(new java.awt.Dimension(50, 1));

        auto.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        auto.setForeground(new java.awt.Color(255, 255, 255));
        auto.setText("None");
        auto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                autoMousePressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/team2485/smartdashboard/extension/res/chevron.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                autoMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(auto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(vSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(autoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(auto, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
            .addComponent(sendButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendButtonMousePressed
        table.putNumber(FIELD_NAME, selectedIndex);
        if (!buttonPressed) {
            if (buttonIcon == null) buttonIcon = sendButton.getIcon();
            sendButton.setIcon(buttonPressedIcon);
            buttonPressed = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) { }

                    buttonPressed = false;
                    sendButton.setIcon(buttonIcon);
                }
            }).start();
        }
    }//GEN-LAST:event_sendButtonMousePressed

    private void autoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_autoMousePressed
        popup.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_autoMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel auto;
    private javax.swing.JLabel autoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel sendButton;
    private javax.swing.JSeparator vSeparator;
    // End of variables declaration//GEN-END:variables
}