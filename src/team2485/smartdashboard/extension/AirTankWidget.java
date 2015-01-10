package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AirTankWidget extends Widget {

    public static final String NAME = "Air Widget";
    public static final DataType[] TYPES = {DataType.NUMBER};

    private static final double MAX_VAL = 120, MIN_VAL = 0;

    private int MAX_DRAW_WIDTH = 132;
    private double value = 000;
    private int drawWidth = 0;
    private int X = 0;
    int val;
    int timerCounter = 0;
    private Color color = Color.RED;
    private double R;
    private double G;
    private double B;
    private double stylemulti1;
    private double stylemulti2;
    private boolean lowAir;
    private String text;
    private Font font;
    private Font fonts;
    private final Property prop = new BooleanProperty(this, "Smoothing", true);
    private final Property test = new BooleanProperty(this, "Test", false);
    private final Property propval = new DoubleProperty(this, "Smoothing Mulltipyler (0.0 - 0.9)", .8);
    private final Property style = new IntegerProperty(this, "Style (0 - 2)", 2);
    private BufferedImage airtank;
    private BufferedImage airtank2;
    private BufferedImage airtank3;

    @Override
    public void init() {
        try {
            airtank = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank.png"));
            airtank2 = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank2.png"));
            airtank3 = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank3.png"));
        } catch (IOException e) {
        }

        final Dimension size = new Dimension(300, 140);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(new Dimension(10, 7));
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

                        val++;
                        //System.out.println(value);
                        setValue(val);
                        if (val > 170) {
                            val = 0;
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
        if (prop.getValue().equals(true)) {
            value = (value * ((double) propval.getValue())) + (((Number) o).doubleValue() * (1 - ((double) propval.getValue())));
        } else {
            value = ((Number) o).doubleValue();
        }
        text = (int) value + "";
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics gg) {
        X = (int) Math.min(getWidth(), getHeight() * 1.7);
        timerCounter++;
        if (value <= 60 || value > 150) {
            R = 40 + Math.abs(30 * (timerCounter % 5));
            G = 50 - Math.abs(10 * (timerCounter % 5));
            B = 50 - Math.abs(10 * (timerCounter % 5));
            text = "CHECK AIR";
            lowAir = true;
        } else if ((value > 60) && (value <= 90)) {
            R = (255 - ((value - 60) * (.2)));
            G = ((value - 60) * 4.367);
            B = 0;
            lowAir = false;
        } else if ((value > 90) && (value <= 120)) {
            R = (int) 255 - ((value - 90) * ((value - 90) / 3.66));
            G = ((value - 90) * ((value - 90) / 14.05)) + 131;
            B = 0;
            lowAir = false;
        } else if (value > 120) {
            R = 0;
            G = 195;
            B = 0;
            lowAir = false;
        }
        color = (new java.awt.Color(((int) R), ((int) G), ((int) B)));
        color = color.brighter();
        if (timerCounter > 3) {
            timerCounter = -3;
        }

        final Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (value >= 000) {
            switch ((int) style.getValue()) {
                case 0:
                    font = new Font("Ubuntu", Font.PLAIN, (X / 11));
                    fonts = new Font("Consolas", 0, (X / 16));

                    if (drawWidth > MAX_DRAW_WIDTH) {
                        drawWidth = MAX_DRAW_WIDTH;
                    }

                    MAX_DRAW_WIDTH = (int) (X * .79);
                    drawWidth = (int) ((value - MIN_VAL) / (MAX_VAL - MIN_VAL) * MAX_DRAW_WIDTH);

                    stylemulti1 = (X / 2) * (0.24285714285);
                    stylemulti2 = (X / 2) * (0.47285714285);

                    g.setColor(color);
                    g.fillRoundRect(X / 10, (int) stylemulti1, drawWidth, (int) stylemulti2, X / 15, X / 15);

                    g.setColor(Color.WHITE);
                    g.setFont(font);
                    if (!lowAir) {
                        g.drawString(text + " PSI", (X - g.getFontMetrics().stringWidth(text + "PSI")) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        if ((prop.getValue().equals(true))) {
                            g.drawString("~", (int) (X / 2 - (g.getFontMetrics().stringWidth("~"))) - (g.getFontMetrics().stringWidth(text + "PSI") / 2), (int) (X / 1.97) + g.getFontMetrics().getHeight() / 4);
                        }
                    } else {
                        g.setColor(Color.RED);
                        g.drawString(text, (X - g.getFontMetrics().stringWidth(text)) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                    }
                    g.drawImage(airtank2, 0, 0, (X), (X / 2), this);
                    break;

                case 1:
                    font = new Font("Ubuntu", Font.BOLD, (X / 8));
                    fonts = new Font("Consolas", 0, (X / 16));
                    MAX_DRAW_WIDTH = (int) (X * .92);
                    drawWidth = (int) ((value - MIN_VAL) / (MAX_VAL - MIN_VAL) * MAX_DRAW_WIDTH);

                    g.setColor(new java.awt.Color(100, 100, 100, 100));
                    g.fillRoundRect((X / 23), (X / 15), MAX_DRAW_WIDTH, (int) (X / 2.7), 8, 8);

                    if (drawWidth > MAX_DRAW_WIDTH) {
                        drawWidth = MAX_DRAW_WIDTH;
                    }

                    g.setColor(color);
                    g.fillRoundRect(X / 23, X / 15, drawWidth, (int) (X / 2.7), 8, 8);

                    if (!lowAir) {
                        g.setColor(Color.GREEN);
                        g.setFont(font);
                        g.drawString(text, (X - g.getFontMetrics().stringWidth(text)) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        g.setFont(fonts);
                        g.drawString("PSI", (int) (X + (g.getFontMetrics(font).stringWidth(text))) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        if ((prop.getValue().equals(true))) {
                            g.setFont(new Font("Consolas", 0, (X / 10)));
                            g.drawString("~", (int) (X / 2 - (g.getFontMetrics().stringWidth("~"))) - (g.getFontMetrics(font).stringWidth(text) / 2), (int) (X / 1.97) + g.getFontMetrics().getHeight() / 4);
                        }
                    } else {
                        g.setColor(Color.RED);
                        g.setFont(font);
                        g.drawString(text, (X - g.getFontMetrics().stringWidth(text)) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                    }

                    g.drawImage(airtank, 0, 0, (X), (X / 2), this);
                    break;

                case 2:
                    font = new Font("BOOMBOX", Font.BOLD, (X / 10));
                    fonts = new Font("Consolas", 0, (X / 10));
                    X = X * 8 / 10;
                    g.translate(X / 16, X / 16);
                    MAX_DRAW_WIDTH = (int) (X);
                    drawWidth = (int) ((value - MIN_VAL) / (MAX_VAL - MIN_VAL) * MAX_DRAW_WIDTH);

                    g.setColor(new java.awt.Color(100, 100, 100, 100));
                    g.fillRoundRect((X / 22), (X / 18), MAX_DRAW_WIDTH, (int) (X / 2.8), 8, 8);

                    if (drawWidth > MAX_DRAW_WIDTH) {
                        drawWidth = MAX_DRAW_WIDTH;
                    }

                    g.setColor(color);
                    g.fillRect(X / 22, X / 18, drawWidth, (int) (X / 2.8));
                    g.setColor(Color.YELLOW);
                    g.translate(X / 20, 0);

                    if (!lowAir) {
                        g.setFont(font);
                        g.drawString(text, (X - g.getFontMetrics().stringWidth(text)) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        g.setFont(new Font("BOOMBOX", Font.BOLD, X / 15));
                        g.setColor(Color.GREEN);
                        g.drawString("PSI", (int) (X + (g.getFontMetrics(font).stringWidth(text))) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        if ((prop.getValue().equals(true))) {
                            g.setFont(fonts);
                            g.drawString("~", (int) (X / 2 - (g.getFontMetrics().stringWidth("~"))) - (g.getFontMetrics(font).stringWidth(text) / 2), (int) (X / 1.97) + g.getFontMetrics().getHeight() / 4);
                        }
                    } else {
                        g.setFont(font);
                        g.setColor(Color.RED);
                        g.translate(0,  6);
                        g.drawString(text, (X - g.getFontMetrics().stringWidth(text)) / 2, (int) (X / 1.97) + g.getFontMetrics().getHeight() / 2);
                        g.translate(0, -6);
                    }

                    g.translate(-X / 20, 0);
                    g.drawImage(airtank3, -X / 19, -X / 19, X * 12 / 10, (X * 12 / 10 / 2), null);

            }

            
        }

    }
}
