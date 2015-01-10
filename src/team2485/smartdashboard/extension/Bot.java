package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.IntegerListProperty;
import edu.wpi.first.smartdashboard.properties.MultiProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Bot extends StaticWidget {
    public static final String NAME = "MiniBot";

    private BufferedImage image;
    private int X;
    public int [] styles = new int [] {0,1,2};
    Property style = new IntegerListProperty(this, "Style", styles);


    @Override
    public void init() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/minibot2.png"));
        } catch (IOException e) { }

        final Dimension size = new Dimension(210, 210);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(new Dimension(105, 105));
        this.setMaximumSize(new Dimension(500, 500));
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        X = Math.min(getWidth(), getHeight());
        g.drawImage(image, 0, 0, X, X, null);
    }
}
