package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.smartdashboard.types.DataType;

public class Dial extends Widget {
	
	public static final String NAME = "2485 Dial";
    public static final DataType[] TYPES = {DataType.NUMBER};
    double degrees;
    double val;
    double value;
    
    int scaling;
	private BufferedImage backgroundImg;
	DoubleProperty  SizeMultiplier = new DoubleProperty (this, "Size Multiplier", 6);
	DoubleProperty  Devitation     = new DoubleProperty (this, "Devitation", 5);
	IntegerProperty  Multiplier    = new IntegerProperty (this, "Multiplier", 1);
	IntegerProperty Range          = new IntegerProperty(this, "Range", 25);
	BooleanProperty Test		   = new BooleanProperty(this, "Test", false);
	StringProperty Units		   = new StringProperty(this, "Units", "IPS");
	
	@Override
	public void init() {
		 try {
	            backgroundImg = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Dial.png"));
	        } catch (IOException e) {}

	        final Dimension size = new Dimension(300, 300);
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
	                    if (Test.getValue()) {
	                    	val+= .1;
	                        setValue(val);
	                        if (val > Range.getValue()) {
	                            val = 0;
	                        }
	                    }
	                }
	            }
	        }).start();
	    }
		
	


	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Object arg0) {
		value = ((Number) arg0).doubleValue();
		degrees = value* SizeMultiplier.getValue() - 90;
		
		scaling = Math.min((int)(getWidth()*.75), getHeight());
		
		repaint();
		
	}

	
	
	protected void paintComponent(final Graphics g) {
		double displayVal = val * 100;
		displayVal = (int) displayVal;
		displayVal = displayVal/100;
		Graphics2D gg = (Graphics2D) g;
		
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
	    gg.setRenderingHints(rh);
	    
		Font font = new Font("BOOMBOX", Font.BOLD, (scaling / 16));
		gg.setFont(font);
		gg.setColor(new Color(228, 192, 37));
		gg.drawImage(backgroundImg, 0, 0,scaling,scaling*backgroundImg.getHeight()/backgroundImg.getWidth(), null);
		gg.translate(scaling / 2, (int) scaling * .475);
		gg.rotate(Math.toRadians(degrees));
		gg.fillRect((int)(-scaling*.02), (int)(-scaling*.36), (int)(scaling*.01), (int)(scaling*.38));
		gg.rotate(Math.toRadians(-degrees));
		gg.setColor(Color.GREEN);
		for (int i = 0; i <= Range.getValue(); i += Devitation.getValue()) {
			
			double x = Math.cos(Math.toRadians(i * SizeMultiplier.getValue()*.9 - 180));
			double y = Math.sin(Math.toRadians(i * SizeMultiplier.getValue()*.9 - 180));
			gg.drawString(
					"" + i,
					(int) (x*scaling*.3 - (gg.getFontMetrics().stringWidth("" + i) / 2)),
					(int) (y*scaling*.3 + (gg.getFontMetrics().getHeight() / 2)));
			
			gg.fillOval((int) (x*scaling*.2), (int) (y*scaling*.2), 2, 2);
		}
		
		gg.drawString("" + displayVal, (int)(g.getFontMetrics().getStringBounds("" + displayVal,gg).getWidth()/-2), scaling/6);
		gg.setFont(new Font("BOOMBOX", Font.PLAIN, (scaling / 20)));
		gg.drawString("" + Units.getValue() + " x " + Multiplier.getValue(), -scaling*1/10, scaling/4);
		gg.translate(-scaling / 2, -scaling / 2);
		//gg.drawString("" + value, 20, 20);
	}
}

