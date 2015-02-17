package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BatteryWidget extends Widget {

    public static final String NAME = "2485 Battery Widget";
    public static final DataType[] TYPES = {DataType.NUMBER};

    private static final double MAX_VOLTAGE = 12.5, MIN_VOLTAGE = 7;

    private Font font;
    private Font fontS;

    private String text;

    private double value = 0;
    private int drawWidth = 0;
    private int X;
    private Color color;

    private BufferedImage battery;
    private BufferedImage battery2;

    private final Property style = new IntegerProperty(this, "Style (0 - 1)", 1);
    private final Property test = new BooleanProperty(this, "Test", false);

    @Override
    public void init() {
        try {
            battery = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/battery.png"));
            battery2 = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/battery2.png"));
        } catch (IOException e) {
        }

        final Dimension size = new Dimension(300, 150);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(new Dimension(10, 5));
        this.setMaximumSize(new Dimension(4000, 2000));
        this.setValue(10);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    if ((boolean) test.getValue()) {

                        value += .1;
                        setValue(value);
                        if (value > 16) {
                            value = 10;
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void propertyChanged(final Property prprt) {
    }
    
    @Override
    public void setValue(Object o) {
        X = (int) Math.min(getWidth(), getHeight() * 1.7);
        value = ((Number) o).doubleValue();
        
        //changing colors
        
        double R = 0, G = 0, B = 0;
        double adjVal = (value-11.7)*75 + 60;
        if (adjVal <= 60) {
        	R = 255;
        	G = 0;
        	B = 0;
        } else if ((adjVal > 60) && (adjVal <= 90)) {
            R = (255 - ((adjVal - 60) * (.2)));
            G = ((adjVal - 60) * 4.367);
            B = 0;
        } else if ((adjVal > 90) && (adjVal <= 120)) {
            R = (int) 255 - ((adjVal - 90) * ((adjVal - 90) / 3.66));
            G = ((adjVal - 90) * ((adjVal - 90) / 14.05)) + 131;
            B = 0;
        } else if (adjVal > 120) {
            R = 0;
            G = 195;
            B = 0;  
        }
        
        color = new Color((int)R,(int)G,(int)B);

        text = (String.format("%.2f", value));

        repaint();
    }

    @Override
    protected void paintComponent(final Graphics gg) {
        final Graphics2D g = (Graphics2D) gg;
        //System.out.println(X);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch ((int) style.getValue()) {
            case 0:
                if (value < MAX_VOLTAGE) {
                    drawWidth = (int) ((value - MIN_VOLTAGE) / (MAX_VOLTAGE - MIN_VOLTAGE) * 110);
                } else {
                    drawWidth = (int) (110);
                }
                g.drawImage(battery, 0, 0, this);
                g.setFont(new Font("Ubuntu", Font.PLAIN, 15));
                g.setColor(color);
                if (drawWidth > 0) {
                    g.fillRoundRect(13, 13, drawWidth, 41, 8, 8);
                }
                g.drawString(text, 72 - g.getFontMetrics().stringWidth(text) / 2, 80);
                break;
            default:
                g.setColor(color);
                if (value < MAX_VOLTAGE) {
                    drawWidth = (int) ((value - MIN_VOLTAGE) / (MAX_VOLTAGE - MIN_VOLTAGE) * X * .75);
                } else {
                    drawWidth = (int) (X * .75);
                }
                if (drawWidth
                        > 0) {
                    g.fillRoundRect((int) (X * .11), (int) (X * .05), drawWidth, (int) (X * .325), 0, 0);
                }
                font = new Font("BOOMBOX", Font.BOLD, (X / 10));
                fontS = new Font("BOOMBOX", Font.BOLD, (X / 17));

                g.setFont(font);

                g.drawString(text, X / 2 - g.getFontMetrics().stringWidth(text) / 2, X / 2);
                g.setFont(fontS);
                g.drawString("V", X * 5 / 11 + g.getFontMetrics(font).stringWidth(text) - g.getFontMetrics().stringWidth("V"), X * 19 / 40);
                g.drawImage(battery2, 0, 0, X, (int) (X * .42), this);

                break;
        }

    }
}
