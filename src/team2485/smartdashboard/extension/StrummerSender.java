package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.java.games.input.*;

/**
 * Guitar Hero X-plorer strummer axis sender
 * @author Bryce Matsumori
 */
public class StrummerSender extends StaticWidget implements Runnable {
    public static final String NAME = "X-plorer Strummer Sender";

    private ControllerEnvironment env;
    private Controller xplorer;
    private Component component;
    private NetworkTable table;
    private BufferedImage image;

    public StrummerSender() {
        final Dimension size = new Dimension(84, 38);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        try {
            image = ImageIO.read(getClass().getResource("/team2485/smartdashboard/extension/res/guitar.png"));
        } catch (IOException e) { }
    }

    @Override
    protected void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, null);

        if (component == null) {
            g.setColor(Color.red);
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
            g.drawLine(10, 10, 74, 28);
            g.drawLine(10, 28, 74, 10);
        }
    }

    @Override
    public void init() {
        table = NetworkTable.getTable("SmartDashboard");

        env = ControllerEnvironment.getDefaultEnvironment();
        for (Controller c : env.getControllers()) {
            if (c.getName().contains("X-plorer")) {
                System.out.println("Found X-plorer");
                xplorer = c;
                component = xplorer.getComponent(Component.Identifier.Axis.POV);

                Thread t = new Thread(this, "Strummer Sender");
                t.setDaemon(true);
                t.start();
            }
        }

        if (xplorer == null) {
            final Runnable self = this;
            env.addControllerListener(new ControllerListener() {
                @Override
                public void controllerAdded(ControllerEvent ce) {
                    if (ce.getController().getName().contains("X-plorer")) {
                        System.out.println("Found X-plorer");
                        xplorer = ce.getController();
                        component = xplorer.getComponent(Component.Identifier.Axis.POV);

                        final Thread t = new Thread(self, "Strummer Sender");
                        t.setDaemon(true);
                        t.start();
                    }
                }

                @Override
                public void controllerRemoved(ControllerEvent ce) {
                }
            });
        }
    }

    @Override
    public void propertyChanged(Property property) {
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (xplorer != null) {
                    xplorer.poll();
                    table.putNumber("xplorer-strummer", component.getPollData());
                }

                Thread.sleep(20);
            }
        } catch (InterruptedException e) { }
    }
}
