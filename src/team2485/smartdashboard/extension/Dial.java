package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class Dial extends Widget {
	
	public static final String NAME = "Dial";
    public static final DataType[] TYPES = {DataType.NUMBER};
    int degrees;
    int val;
    int value;
    
    int scaling;
	private BufferedImage backgroundImg;
	
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
	        
	        boolean test = true;
	        
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (true) {
	                    try {
	                        Thread.sleep(100);
	                    } catch (InterruptedException ex) {
	                    }
	                    if (test) {
	                    	val+= 10;
	                        setValue(val);
	                        if (val > 2500) {
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
		value = (int) arg0;
		degrees = (int) value/15 - 90;
		
		scaling = Math.min((int)(getWidth()*.75), getHeight());
		
		repaint();
		
	}

	
	
	protected void paintComponent(final Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		Font font = new Font("BOOMBOX", Font.BOLD, (scaling / 16));
		gg.setFont(font);
		gg.setColor(Color.YELLOW);
		gg.drawImage(backgroundImg, 0, 0,scaling,scaling, null);
		gg.translate(scaling / 2, (int) scaling * .55);
		gg.rotate(Math.toRadians(degrees));
		gg.fillRect((int)(-scaling*.02), (int)(-scaling*.39), (int)(scaling*.01), (int)(scaling*.41));
		gg.rotate(Math.toRadians(-degrees));
		gg.setColor(Color.GREEN);
		for (int i = 0; i < 3001; i += 500) {
			double x = Math.cos(Math.toRadians(i / 15 - 165));
			double y = Math.sin(Math.toRadians(i / 15 - 165));
			gg.drawString(
					"" + i/100,
					(int) (x*scaling*.3 - (gg.getFontMetrics().stringWidth("" + i/100) / 2)),
					(int) (y*scaling*.3 + (gg.getFontMetrics().getHeight() / 2)));
			
			gg.fillOval((int) (x*scaling*.25), (int) (y*scaling*.25), 2, 2);
		}
		gg.translate(-scaling / 2, -scaling / 2);
		//gg.drawString("" + value, 20, 20);
	}
}

