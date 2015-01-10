package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class NyanCat extends Widget {
    public static final String NAME = "Nyan Cat (data)";
    public static final DataType[] TYPES = { DataType.NUMBER };

    private Thread renderThread;
    private boolean shutdown = false;
    private long delayVal = 250;
    private int index = 0;
    private BufferedImage image;

    @Override
    public void init() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/poptart.png"));
        } catch (IOException e) { }

        final Dimension size = new Dimension(400, 400);
        this.setSize(size);
        this.setPreferredSize(size);

        renderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!shutdown) {
                    repaint();
                    index++;
                    if (index > 11) index = 0;
                    try {
                        Thread.sleep(delayVal);
                    } catch (InterruptedException e) { }
                }
            }
        }, "Nyan Render");
        renderThread.start();
    }

    @Override
    public void setValue(Object o) {
       delayVal = 20 + ((Number)o).intValue() * 3;
    }

    @Override
    public void propertyChanged(Property prprt) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        // draw centered, max bounds or 400px
        final int width = Math.min(400, Math.min(this.getWidth(), this.getHeight())),
                  x = this.getWidth()  / 2 - width / 2,
                  y = this.getHeight() / 2 - width / 2;
        
        g.drawImage(image,
                x, y, x + width, y + width,
                index * 400, 0, index * 400 + 400, 400,
                null);
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown = true;
        super.finalize();
    }
}
