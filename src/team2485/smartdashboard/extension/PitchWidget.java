package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class PitchWidget extends Widget {
	
	public static final String NAME = "PitchWidget";
    public static final DataType[] TYPES = {DataType.STRING};
    int degrees;
    int val;
    double value;
    
    int scaling;
	private double target;
	private double base;
	private boolean recievedBase;
	
	@Override
	public void init() {
//		 try {
//	            airtank = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank.png"));
//	            airtank2 = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank2.png"));
//	            airtank3 = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Tank3.png"));
//	        } catch (IOException e) {
//	        }

	        final Dimension size = new Dimension(300, 300);
	        this.setSize(size);
	        this.setPreferredSize(size);
	        this.setMinimumSize(new Dimension(10, 7));
	        this.setMaximumSize(new Dimension(4000, 2000));
	        this.setValue(10 + "," + 10);
	        
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
	                    	val+= 1;
	                        setValue(val + "," + 1);
	                        if (val > 360) {
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
		final String[] vals = ((String)arg0).split(",");
        value  = Double.parseDouble(vals[0]);
        if (vals.length > 1){
        target = Double.parseDouble(vals[1]);
        }
        if (vals.length > 2) {
            base = (Double.parseDouble(vals[2]));
            recievedBase = true;
        } else {
        	recievedBase = false;
        }
		//degrees = (int) value/20 - 75;
		
		scaling = Math.min((int)(getWidth()*.75), getHeight());
		
		repaint();
		
	}

	
	
	protected void paintComponent(final Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		Font font = new Font("BOOMBOX", Font.BOLD, (scaling / 16));
		gg.setFont(font);
		gg.setColor(Color.GRAY);
		gg.translate(getWidth() / 2, getHeight() / 2);
		gg.rotate(Math.toRadians(target));
		gg.fillRect(-scaling/4,-scaling/80,scaling/2,scaling/40);
		gg.rotate(Math.toRadians(value - target));
		gg.setColor(Color.YELLOW);
		gg.fillRect(-scaling/4, -scaling/80, scaling/2, scaling/40);
		gg.rotate(Math.toRadians(-value));
		gg.setColor(Color.GREEN);
		gg.translate(-getWidth() / 2, -getHeight() / 2);
		gg.drawString("" + value, 20, 20);
	}
}

