package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Background extends StaticWidget implements IRemoteConnectionListener {
    public static final String NAME = "Display > Background";

    private static final String DS_FOCUS_EXE = "C:\\Program Files\\SmartDashboard\\DSFocusAssistant.exe";

    private boolean connected = false;

    private BufferedImage leftImage, rightImage, connectedImage, disconnectedImage, logoBuffer;
    private GradientPaint redTopGrad, greenTopGrad, redBottomGrad, greenBottomGrad, redLineGrad, greenLineGrad;
    private Path2D shape, bottom;

    @Override
    public void init() {
        try {
            leftImage         = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/bg-left.png"));
            rightImage        = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/bg-right.png"));
            connectedImage    = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/logo-connected.png"));
            disconnectedImage = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/logo-disconnected.png"));
        } catch (IOException e) {
            System.err.println("Error loading background images.");
            e.printStackTrace();
        }

        Robot.addConnectionListener(this, true);

        final Background self = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // set dashboard background
                self.getParent().setBackground(new Color(0x111111));

                // maximize it
                ((JFrame)SwingUtilities.getWindowAncestor(self)).setExtendedState(JFrame.MAXIMIZED_BOTH);

                final Dimension size = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, self.getParent().getHeight());
                self.setSize(size);
                self.setPreferredSize(size);
                self.setMinimumSize(size);
                self.setMaximumSize(size);

                final float boxHeight = 120f, dipHeight = 100f, maxHeight = 200f;

                shape = new Path2D.Float();
                shape.moveTo(0f, 0f);
                shape.lineTo(getWidth(), 0f);
                shape.lineTo(getWidth(), boxHeight);
                shape.curveTo(getWidth() - 300f, boxHeight + dipHeight, 300f, boxHeight + dipHeight, 0f, boxHeight);
                shape.lineTo(0f, 0f);

                bottom = new Path2D.Float();
                bottom.moveTo(0f, boxHeight);
                bottom.curveTo(300f, boxHeight + dipHeight, getWidth() - 300f, boxHeight + dipHeight, getWidth(), boxHeight);

                redTopGrad      = new GradientPaint(0, 0, new Color(255, 0, 0, 100), 0, 60, new Color(255, 0, 0, 0));
                redBottomGrad   = new GradientPaint(0, maxHeight, new Color(255, 0, 0, 30), 0, maxHeight - 50, new Color(255, 0, 0, 0));
                redLineGrad     = new GradientPaint(0, 0, new Color(255, 0, 0, 0), getWidth() / 2, 0, new Color(255, 0, 0, 200), true);
                greenTopGrad    = new GradientPaint(0, 0, new Color(0, 255, 0, 100), 0, 60, new Color(0, 255, 0, 0));
                greenBottomGrad = new GradientPaint(0, maxHeight, new Color(0, 255, 0, 30), 0, maxHeight - 50, new Color(0, 255, 0, 0));
                greenLineGrad   = new GradientPaint(0, 0, new Color(0, 255, 0, 0), getWidth() / 2, 0, new Color(0, 255, 0, 200), true);

                renderLogoBuffer();
            }
        });

        focusDS();
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(leftImage, 0, 0, null);
        g.drawImage(rightImage, getWidth() - rightImage.getWidth(), 0, null);

        if (logoBuffer != null)
            g.drawImage(logoBuffer, 0, 0, null);
    }

    private void renderLogoBuffer() {
        if (shape == null) return;

        final int maxHeight = 200;

        logoBuffer = new BufferedImage(getWidth(), maxHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D lg = logoBuffer.createGraphics();
        lg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        lg.setPaint(connected ? greenTopGrad : redTopGrad);
        lg.fillRect(0, 0, getWidth(), 100);

        lg.setClip(shape);

        lg.setPaint(connected ? greenBottomGrad : redBottomGrad);
        lg.fillRect(0, maxHeight - 100, getWidth(), 100);

        lg.setPaint(connected ? greenBottomGrad : redBottomGrad);
        lg.fillRect(0, maxHeight - 50, getWidth(), 50);

        final int imgX = (int)(getWidth() * 0.5f - connectedImage.getWidth() * 0.5f) + 22, imgY = -20;
        lg.drawImage(connected ? connectedImage : disconnectedImage, imgX, imgY, null);

        lg.setClip(null);

        if (bottom != null) {
            lg.setPaint(connected ? greenLineGrad : redLineGrad);
            lg.setStroke(new BasicStroke(2));
            lg.draw(bottom);
        }
    }

    @Override
    public void connected(IRemote remote) {
        if (!connected) {
            connected = true;
            renderLogoBuffer();
            repaint();
        }
    }

    @Override
    public void disconnected(IRemote remote) {
        if (connected) {
            connected = false;
            renderLogoBuffer();
            repaint();
        }
    }

    private static void focusDS() {
        try {
            Runtime.getRuntime().exec(DS_FOCUS_EXE);
        } catch (IOException ex) {
            System.err.println("Could not focus driver station.");
        }
    }
}
