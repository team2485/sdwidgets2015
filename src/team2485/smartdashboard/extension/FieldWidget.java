package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FieldWidget extends Widget {
    public static final String NAME = "Field 2014";
    public static final DataType[] TYPES = { DataType.STRING };

    private NetworkTable table;

    // 24ft 8in x 54ft = 296in x 648in
    private static final int
            REAL_WIDTH  = 296,
            REAL_HEIGHT = 648,
            REAL_HALF_WIDTH  = REAL_WIDTH  / 2,
            REAL_HALF_HEIGHT = REAL_HEIGHT / 2;

    private static final double
            FIELD_CENTERX = 211.0,
            FIELD_CENTERY = 369.0,
            FIELD_WIDTH   = 308.0,
            FIELD_HEIGHT  = 607.0,
            FIELD_HALF_WIDTH  = FIELD_WIDTH  * 0.5,
            FIELD_HALF_HEIGHT = FIELD_HEIGHT * 0.5;

    private static final int
            ROBOT_WIDTH  = 25, // = 26 * REAL_WIDTH / FIELD_WIDTH
            ROBOT_HEIGHT = 29; // = 28 * REAL_HEIGHT / FIELD_HEIGHT

    private BufferedImage fieldImage, greenImage;
    private double
            positionX = 0.0, positionY = 0.0, rotation = 0.0,
            distance = 27.0, // distance is in feet
            destX, destY;
    private boolean inRange = false;

    @Override
    public void init() {
        // Load images
        try {
            this.fieldImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/field.png"));
            this.greenImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/field-green.png"));
        } catch (IOException e) {
            System.err.println("Error loading field images.");
            e.printStackTrace();
        }

        table = NetworkTable.getTable("SmartDashboard");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                destX = (int)((e.getX() - FIELD_CENTERX) / FIELD_HALF_WIDTH  * REAL_HALF_WIDTH);
                destY = (int)((e.getY() - FIELD_CENTERY) / FIELD_HALF_HEIGHT * REAL_HALF_HEIGHT);
                table.putNumber("destinationX", destX);
                table.putNumber("destinationY", destY);
            }
        });

        final Dimension size = new Dimension(522, 740);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    public void setPosition(String position) {
        final String[] split = position.split(",");
        this.positionX = Double.parseDouble(split[0]);
        this.positionY = Double.parseDouble(split[1]);
        this.rotation  = Double.parseDouble(split[2]);
        this.inRange   = Boolean.parseBoolean(split[3]);

        // distance from the target in feet
        this.distance = (REAL_HALF_HEIGHT - this.positionY) / 12;

        repaint();
    }

    @Override
    protected void paintComponent(final Graphics gg) { // too easy
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.setFont(new Font("BoomBox 2", Font.PLAIN, 14));
        g.drawString("Distance", 130 - g.getFontMetrics().stringWidth("Distance"), 300);
        g.setFont(new Font("BoomBox 2", Font.PLAIN, 30));
        final String distStr = String.format("%.1f", this.distance);
        g.drawString(distStr, 130 - g.getFontMetrics().stringWidth(distStr), 300 + 35);

        if (inRange) {
            g.setColor(Color.green);
            g.setFont(new Font("BoomBox 2", Font.PLAIN, 20));
            g.drawString("IN RANGE", 130 - g.getFontMetrics().stringWidth("IN RANGE"), 420);
        }

        g.translate(100, 0);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(inRange ? this.greenImage : this.fieldImage, 0, 0, null);

        // draw position
        double x, y;
        if (this.positionX > -REAL_HALF_WIDTH && this.positionX < REAL_HALF_WIDTH &&
                this.positionY > -REAL_HALF_HEIGHT && this.positionY < REAL_HALF_HEIGHT) {
            x = FIELD_CENTERX + (this.positionX / REAL_HALF_WIDTH) * FIELD_HALF_WIDTH;
            y = FIELD_CENTERY - (this.positionY / REAL_HALF_HEIGHT) * FIELD_HALF_HEIGHT;

            g.setColor(inRange ? Color.green : Color.red);
        }
        // or out of bounds
        else {
            if (this.positionX > REAL_HALF_WIDTH)
                x = FIELD_CENTERX + FIELD_HALF_WIDTH;
            else if (this.positionX < -REAL_HALF_WIDTH)
                x = FIELD_CENTERX - FIELD_HALF_WIDTH;
            else
                x = FIELD_CENTERX + (this.positionX / REAL_HALF_WIDTH) * FIELD_HALF_WIDTH;

            if (this.positionY > REAL_HALF_HEIGHT)
                y = FIELD_CENTERY - FIELD_HALF_HEIGHT;
            else if (this.positionY < -REAL_HALF_HEIGHT)
                y = FIELD_CENTERY + FIELD_HALF_HEIGHT;
            else
                y = FIELD_CENTERY - (this.positionY / REAL_HALF_HEIGHT) * FIELD_HALF_HEIGHT;

            g.setColor(Color.orange);
        }

        g.rotate((rotation) * Math.PI / 180, x, y);

        g.setStroke(new BasicStroke(2));
        g.draw(new Rectangle2D.Double(x - ROBOT_WIDTH / 2, y - ROBOT_WIDTH / 2, ROBOT_WIDTH, ROBOT_HEIGHT));
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(255, 83, 13, 255 - i * 30));
            // TODO: convert to Path2D.Double
            g.fillPolygon(
                    new int[] { (int)x - 5, (int)x, (int)x + 5 },
                    new int[] { (int)y - 25 - i * 12, (int)y - 30 - i * 12, (int)y - 25 - i * 12 }, 3);
        }
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    public void setValue(Object o) {
        setPosition((String)o);
    }
}
