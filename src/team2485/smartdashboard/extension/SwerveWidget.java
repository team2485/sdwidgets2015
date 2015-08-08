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

public class SwerveWidget extends Widget {

	
		private static final long serialVersionUID = 1L;
		
		public static final String NAME = "2485 Swerve";
	    public static final DataType[] TYPES = {DataType.STRING};


	    Property Test = new BooleanProperty(this, "Test", false);

	    private BufferedImage body, wheel;
		
		double clapperY;

		double containerY;
		
		private double scaling;
		private Font font1, font2;
		private double temporaryStringWidth1;
		private double temporaryStringWidth2;


		private double rotVal, frontLeftRot, frontRightRot,backLeftRot, backRightRot;
		
		

	    @Override
	    public void init() {

	        try {
	        	wheel      = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/Wheel.png"     ));
	        } catch (IOException e) {
	        }

	        final Dimension size = new Dimension(300, 300);
	        this.setSize(size);	
	        this.setPreferredSize(size);
	        this.setMinimumSize(new Dimension(100, 100));
	        this.setMaximumSize((new Dimension(800, 800)));

	        new Thread(new Runnable() {


				private int val;

				@Override
	            public void run() {
	                while (true) {
	                	try {
	                        Thread.sleep(30);
	                    } catch (InterruptedException ex) {
	                    }
	                    if ((boolean) Test.getValue()) {
	                        val += 5;
	                        if (val > 360){
	                        	val = 0;
	                        }
	                        
	                        setValue("" + val + "," + val + "," + val + "," + val + "," + val);
	                        
	                    }
	                }
	            }
	        }).start();
	    }

	    @Override
	    public void propertyChanged(final Property prprt) {
	    	
	    }

	    /**
	     * Must be passed a string consisting of three doubles separated by commas:
	     * Clapper percent height, container percent height, and strongback angle.
	     * @param o
	     */
	    @Override
	    public void setValue(Object o) {
	        final String[] vals = ((String) o).split(",");
	        
	        try{
	        	if (vals.length>3){
	        		frontLeftRot    = 	   	Double.parseDouble(vals[0]);
	        		frontRightRot	=       Double.parseDouble(vals[1]);
	        		backLeftRot   	=       Double.parseDouble(vals[2]);
	        		backRightRot   	=       Double.parseDouble(vals[3]);
	                rotVal 		   	=       Double.parseDouble(vals[4]);
	        	}
	        } catch(Exception e){
	        	new IllegalArgumentException("Must be passed 5 doubles seperated by commas. ");
	        }
		        
	        repaint();
	    }

		@Override
	    protected void paintComponent(final Graphics gg) {
			scaling = Math.min(getWidth(), getHeight())*0.0012;//0.0015;
			font1 = new Font("BoomBox", 0, (int)(40*scaling));
			font2 = new Font("BoomBox", 0, (int)(20*scaling));
			Graphics2D g = (Graphics2D) gg;

			g.setColor(Color.GREEN);
			g.setFont(font1);
			
			g.translate(scaling*400, scaling*400);
			
				g.rotate(Math.toRadians(rotVal));
					
					g.translate(-scaling*200, -scaling*200);
					g.rotate(Math.toRadians(frontLeftRot));
				
						g.drawImage (wheel,   (int)(wheel.getWidth()*-1/2*scaling), (int)(wheel.getHeight()*-1/2*scaling), (int)(wheel.getWidth()*scaling),(int)(wheel.getHeight()*scaling), null);
						
						
					g.rotate(Math.toRadians(-frontLeftRot));
					
					
					
					g.translate(scaling*200, scaling*200);
					
					g.translate(scaling*200, -scaling*200);
					g.rotate(Math.toRadians(frontRightRot));
					
						g.drawImage (wheel,   (int)(wheel.getWidth()*-1/2*scaling), (int)(wheel.getHeight()*-1/2*scaling), (int)(wheel.getWidth()*scaling),(int)(wheel.getHeight()*scaling), null);
						
					g.rotate(Math.toRadians(-frontRightRot));
					g.translate(-scaling*200, scaling*200);
					
					g.translate(-scaling*200, scaling*200);
					g.rotate(Math.toRadians(backLeftRot));
					
						g.drawImage (wheel,   (int)(wheel.getWidth()*-1/2*scaling), (int)(wheel.getHeight()*-1/2*scaling), (int)(wheel.getWidth()*scaling),(int)(wheel.getHeight()*scaling), null);
						
					g.rotate(Math.toRadians(-backLeftRot));
					g.translate(scaling*200, -scaling*200);
					
					g.translate(scaling*200, scaling*200);
					g.rotate(Math.toRadians(backRightRot));
				
						g.drawImage (wheel,   (int)(wheel.getWidth()*-1/2*scaling), (int)(wheel.getHeight()*-1/2*scaling), (int)(wheel.getWidth()*scaling),(int)(wheel.getHeight()*scaling), null);
						
					g.rotate(Math.toRadians(-backRightRot));
					g.translate(-scaling*200, -scaling*200);
					
				
						
					
					
				g.rotate(Math.toRadians(-rotVal));
				
				g.translate((int)(Math.sin(Math.toRadians(-rotVal)) * scaling * -200) , (int)(Math.cos(Math.toRadians(-rotVal)) * scaling * -200));
				g.drawString("" + (int)frontLeftRot + "°", (int)(scaling * 75), (int)(scaling * -75));
			
				//g.drawImage(base,(int)(-base.getWidth()*.47*scaling),(int)(-base.getHeight()*3/4*scaling), (int)(base.getWidth()*scaling),(int)(base.getHeight()*scaling), null);
				
				g.setColor(Color.GREEN);
				
			
			g.translate(-scaling*400, -scaling*750);
			
			//g.drawString(":" + scaling, 5, 25);


	    }
	}
