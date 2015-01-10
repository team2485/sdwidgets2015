package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LightSwitcher extends Widget {
    public static final String NAME = "Light Switcher";
    public static final DataType[] TYPES = { DataType.NUMBER };

    private static final String[] LABELS = new String[] {
        "(S) Rainbow cycle", "(S) Black", "(S) Green", "(S) Gold", "(S) Red", "(S) Blue",
        "(S) Gold alternating", "(S) White alternating", "(S) Gold chase", "(S) Gold blink", "(S) Gold ping pong",
        "(S) Happy rainbow", "(S) Rainbow chase", "(S) Rainbow cycle fast", "(M) Intake"
    };
    private static final Color[] COLORS = new Color[] {
        // S_RAINBOW_CYCLE, S_BLACK, S_GREEN, S_GOLD, S_RED, S_BLUE
        new Color(0x3388E8), new Color(0x666666), new Color(0x4CFF26), new Color(0xe4c025), new Color(0xFF3C28), new Color(0x2A95FF),
        // S_GOLD_ALT, S_WHITE_ALT, S_GOLD_CHASE, S_GOLD_BLINK, S_GOLD_PING_PONG
        new Color(0xe4c025), new Color(0xffffff), new Color(0xe4c025), new Color(0xe4c025), new Color(0xe4c025),
        // S_HAPPY_RAINBOW, S_RAINBOW_CHASE, S_RAINBOW_CYCLE_FAST, M_INTAKE
        new Color(0xFF7B16), new Color(0xFF4564), new Color(0xFF7C64), new Color(0xE8D126)
    };
    private static final int COUNT = COLORS.length, BTN_WIDTH = 30;

    private int mode = 0;
    private NetworkTable table;

    public LightSwitcher() {
    }

    @Override
    public void init() {
        table = NetworkTable.getTable("SmartDashboard");

        final Dimension size = new Dimension(BTN_WIDTH * COUNT, BTN_WIDTH + 30);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mode = e.getX() / BTN_WIDTH;
                table.putNumber(getFieldName(), mode);
                repaint();
            }
        });
    }

    @Override
    public void setValue(Object value) {
        mode = ((Number)value).intValue();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font("Ubuntu", Font.BOLD, 16));
        for (int i = 0; i < COUNT; i++) {
            if (mode == i) {
                g.setColor(COLORS[i]);
                g.fillOval(i * BTN_WIDTH + BTN_WIDTH / 2 - 12, BTN_WIDTH / 2 - 12, 24, 24);
                g.fillRect(i * BTN_WIDTH + BTN_WIDTH / 2 - 12, BTN_WIDTH / 2, 24, 24);
                g.setColor(new Color(0x212121));
            }
            else {
                g.setColor(Color.white);
            }

            final String n = Integer.toString(i);
            g.drawString(n, i * BTN_WIDTH + BTN_WIDTH / 2 - g.getFontMetrics().stringWidth(n) / 2, BTN_WIDTH / 2 + 6);
        }

        g.setColor(COLORS[mode]);
        final String label = LABELS[mode];
        final int y = BTN_WIDTH / 2 - 12 + 24;
        g.fillOval(BTN_WIDTH / 2 - 12, y, 24, 24);
        g.fillOval(BTN_WIDTH / 2 - 12 + COUNT * BTN_WIDTH - BTN_WIDTH, y, 24, 24);
        g.fillRect(BTN_WIDTH / 2, y, COUNT * BTN_WIDTH - BTN_WIDTH / 2 - 12, 24);

        g.setColor(new Color(0x212121));
        g.setFont(new Font("Ubuntu", Font.PLAIN, 14));
        g.drawString(label, BTN_WIDTH / 2 + (COUNT * BTN_WIDTH - BTN_WIDTH / 2 - 12) / 2 - g.getFontMetrics().stringWidth(label) / 2, y + 24 - 6);
    }

    @Override
    public void propertyChanged(Property property) {
    }
}
