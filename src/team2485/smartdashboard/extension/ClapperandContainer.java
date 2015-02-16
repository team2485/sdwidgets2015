package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
//import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Aidan
 * STRING DataType
 * Shows position of the clapper, the container and the angle of the strongback.
 *
 */
public class ClapperandContainer extends Widget {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "2485 Clapper and Container";
    public static final DataType[] TYPES = {DataType.STRING};


    Property Test = new BooleanProperty(this, "Test", false);

    private BufferedImage containerArm, clapper, strongBack, base;
	double clapperVal;

	private double containerVal;
	private double rotVal;

	private double rawVal;
	
	double clapperY;

	double containerY;

	private int clapperRange, clapperLoc, containerRange, containerLoc;
	
	private double scaling;
	private Font font1, font2;
	private double temporaryStringWidth1;
	private double temporaryStringWidth2;

	private String rotation;

	private boolean error = false;

	private double clapperRaw;

	private double containerRaw;
	
	

    @Override
    public void init() {

        try {
        	clapper      = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/clapper.png"     ));
        	containerArm = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/containerArm.png"));
        	strongBack   = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/strongBack.png"  ));
        	base         = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/base.png"        ));
        } catch (IOException e) {
        }

        final Dimension size = new Dimension(300, 300);
        this.setSize(size);	
        this.setPreferredSize(size);
        this.setMinimumSize(new Dimension(100, 100));
        this.setMaximumSize((new Dimension(800, 800)));

        new Thread(new Runnable() {


			@Override
            public void run() {
                while (true) {
                	try {
                        Thread.sleep(30);
                    } catch (InterruptedException ex) {
                    }
                    if ((boolean) Test.getValue()) {
                        rawVal = rawVal + 1;
                        int tempVal = (int) rawVal;
                        if (rawVal > 100){
                        	tempVal = 100;
                        } else if(rawVal < 0){
                        	tempVal = 0;
                    	}
                        setValue(tempVal*.01 + "," + 2700 + "," + (100-tempVal)*.01 + "," + 4500 + "," + (rawVal-45)*.1);
                        if (rawVal > 115) {
                            rawVal = -15;
                        }
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
        		clapperVal     = 	   Double.parseDouble(vals[0]);
        		clapperRaw	   =       Double.parseDouble(vals[1]);
                containerVal   =       Double.parseDouble(vals[2]);
                containerRaw   =       Double.parseDouble(vals[3]);
                rotVal 		   =       Double.parseDouble(vals[4]);
        	} else {
        		clapperVal     = 	   Double.parseDouble(vals[0]);
                containerVal   =       Double.parseDouble(vals[1]);
                rotVal 		   =       Double.parseDouble(vals[2]);
        	}
        } catch(Exception e){
        	new IllegalArgumentException(" Must be passed a string consisting of three doubles separated by commas: \n "
        			+ "Clapper percent height, container percent height, and strongback angle. ");
        	error = true;
        }
        
        clapperRange   = (int) (300*scaling);
        clapperLoc     = (int) (80*scaling);
        
        containerRange = (int) (500*scaling);
        containerLoc   = (int) (-10  *scaling);
        
        clapperY       = (((1-clapperVal)   *  clapperRange   + clapperLoc  )+(-strongBack.getHeight()*scaling));
        containerY     = (((1-containerVal) *  containerRange + containerLoc)+(-strongBack.getHeight()*scaling));
        
        
        repaint();
    }

	@Override
    protected void paintComponent(final Graphics gg) {
		scaling = Math.min(getWidth(), getHeight())*0.0012;//0.0015;
		font1 = new Font("BoomBox", 0, (int)(35*scaling));
		font2 = new Font("BoomBox", 0, (int)(20*scaling));
		Graphics2D g = (Graphics2D) gg;

		g.setColor(Color.GREEN);
		g.setFont(font1);
		
		g.translate(scaling*400, scaling*750);
		
			g.rotate(Math.toRadians(rotVal));
				
				g.drawString("" + (int)(clapperVal*100),   (int)(200*scaling), (int)(clapperY   + 90*scaling));
				g.drawString("" + (int)(containerVal*100), (int)(60*scaling) , (int)(containerY - 20*scaling));
				temporaryStringWidth1 = g.getFontMetrics().getStringBounds("" + (int)(clapperVal  *100), g).getWidth();
				temporaryStringWidth2 = g.getFontMetrics().getStringBounds("" + (int)(containerVal*100) + "", g).getWidth();
				g.setFont(font2);
				
				g.setColor(Color.YELLOW);
				g.drawString("%",(int)(200*scaling + temporaryStringWidth1), (int)(clapperY    + 90*scaling));
				g.drawString("%",(int)(60 *scaling + temporaryStringWidth2), (int)(containerY  - 20*scaling));
				if (containerRaw > 0 && clapperRaw > 0){
					g.drawString("" + containerRaw,(int)(200*scaling), (int)(clapperY + 110*scaling));
					g.drawString("" + clapperRaw,  (int)(60 *scaling), (int)(containerY           ));
				}
				
				g.drawImage (clapper,      (int)(strongBack.getWidth()*.5*scaling), (int)clapperY,   (int)(clapper.getWidth()*scaling),     (int)(clapper.getHeight()*scaling),      null);	
				g.drawImage (containerArm, (int)(strongBack.getWidth()*.5*scaling), (int)containerY, (int)(containerArm.getWidth()*scaling),(int)(containerArm.getHeight()*scaling), null);
				
				g.drawImage (strongBack,   (int)(strongBack.getWidth()*-1/2*scaling), (int)(-strongBack.getHeight()*.98*scaling), (int)(strongBack.getWidth()*scaling),(int)(strongBack.getHeight()*scaling), null);
				
				g.rotate(Math.toRadians(-rotVal));
		
			g.drawImage(base,(int)(-base.getWidth()*.47*scaling),(int)(-base.getHeight()*3/4*scaling), (int)(base.getWidth()*scaling),(int)(base.getHeight()*scaling), null);
			
			g.setColor(Color.GREEN);
			g.setFont(font1);
			g.drawString(((int)rotVal) + "", (int)(-225*scaling), (int)(-100*scaling));
			temporaryStringWidth1 = g.getFontMetrics().getStringBounds(((int)rotVal)   + "", g).getWidth();
			g.setFont(font2);
			
			g.setColor(Color.YELLOW);
			g.drawString("° Detected",(int)+(-225*scaling + temporaryStringWidth1), (int)(-120*scaling));
			if (error){
				gg.drawString("Errors!", 20, 20);
			}
		
		g.translate(-scaling*400, -scaling*750);
		
		//g.drawString(":" + scaling, 5, 25);


    }
}
