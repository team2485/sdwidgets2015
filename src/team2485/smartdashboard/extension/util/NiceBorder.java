package team2485.smartdashboard.extension.util;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;

/**
 * Border with antialiased lines and custom radius.
 * Modified from http://stackoverflow.com/q/15025092/1248884
 */
public class NiceBorder extends AbstractBorder {
    private Color color;
    private int thickness = 4;
    private int radius = 8;
    private Insets insets = null;
    private BasicStroke stroke = null;
    private int strokePad;

    private final RenderingHints hints;

    public NiceBorder(Color color, int thickness, int radius) {
        this(color, thickness, radius, radius / 2 + thickness / 2);
    }

    public NiceBorder(Color color, int thickness, int radius, int padding) {
        this.thickness = thickness;
        this.radius = radius;
        this.color = color;

        stroke = new BasicStroke(thickness);
        strokePad = thickness / 2;

        hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        insets = new Insets(padding, padding, padding, padding);
    }


    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return getBorderInsets(c);
    }

    @Override
    public void paintBorder(Component c, Graphics gg, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHints(hints);

        int bottomLineY = height - thickness;

        RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
                strokePad, strokePad, width - thickness, bottomLineY,
                radius, radius);

        Area area = new Area(bubble);

        // Paint the BG color of the parent, everywhere outside the clip of the text bubble.
        Component parent = c.getParent();
        if (parent != null) {
            Color bg = parent.getBackground();
            Rectangle rect = new Rectangle(0, 0, width, height);
            Area borderRegion = new Area(rect);
            borderRegion.subtract(area);
            g.setClip(borderRegion);
            g.setColor(bg);
            g.fillRect(0, 0, width, height);
            g.setClip(null);
        }

        g.setColor(color);
        g.setStroke(stroke);
        g.draw(area);
    }
}
