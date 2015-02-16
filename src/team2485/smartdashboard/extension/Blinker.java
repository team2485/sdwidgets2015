package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Blinker extends Widget {
    
    public static final String NAME = "2485 Blinker";
    public static final DataType[] TYPES = {DataType.BOOLEAN};
    
    private static final int PAUSE_FRAMES = 30;
    
    private boolean val = true;
    
    private Color color;
    private int Y;
    
    private BufferedImage ImageNull;
    private BufferedImage ImageResize;
    private int frameHeight = 0;
    private final Property test = new BooleanProperty(this, "AutoBlink", false);
    private final Property Resizeable = new BooleanProperty(this, "Resizeable", false);
    private final Property rate = new IntegerProperty(this, "Blink Rate", 600);
    
    @Override
    @SuppressWarnings("empty-statement")
    public void init() {
        try {
            ImageNull   = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/BlinkerNull.png"));
            ImageResize = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/BlinkerResize.png"));
        } catch (IOException e) {
            System.err.println("Error loading images.");
        }
        
        frameHeight = ImageNull.getHeight();
        
        final Dimension size = new Dimension(50, 50);
        setSize(size);
        setResizable(false);
        setPreferredSize(size);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep((int) rate.getValue());
                    } catch (InterruptedException ex) {
                    }
                    if ((boolean) Resizeable.getValue()) {
                        setResizable(true);
                    } else {
                        setResizable(false);
                    }
                    if ((boolean) test.getValue()) {
                        
                        val = !val;
                        color = new Color(250, 150, 0);
                        repaint();
                        
                    }
                }
            }
        }).start();
    }
    
    @Override
    public void setValue(Object value) {
        val = (Boolean) value;
        
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        if ((boolean) Resizeable.getValue()) {
            
            if (getWidth() > getHeight()) {
                Y = getHeight();
            } else {
                Y = getWidth();
            }
        } else {
            Y = 50;
            
        }
        if (val) {
            if (!(boolean) test.getValue()) {
                color = new Color(0, 250, 0);
            }
        } else {
            color = new Color(250, 0, 0);
        }
        
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        gg.setColor(color);
        
        gg.fillOval(Y / 5, Y / 5, Y * 3 / 5, Y * 3 / 5);
        
        if ((boolean) Resizeable.getValue()) {
            gg.drawImage(ImageResize, 0, 0, Y, Y, this);            
        } else {
            gg.drawImage(ImageNull, 0, 0, this);
        }
    }
    
    @Override
    public void propertyChanged(Property property) {
    }
    
}
