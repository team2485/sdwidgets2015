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
	
	public static final String NAME = "Clapper and Container";
    public static final DataType[] TYPES = {DataType.STRING};


    Property smoothingfactor = new IntegerProperty(this, "Smoothing Factor", 5);
    Property Test = new BooleanProperty(this, "Test", false);

    private BufferedImage containerArm, clapper, strongBack, base;
	private int clapperVal, containerVal;
	private double rotVal;

	private double rawVal;
	
	private int clapperY, containerY, clapperRange, clapperLoc, containerRange, containerLoc;
	
	private double scaling;
	private Font font1, font2;
	private double temporaryStringWidth1;
	private double temporaryStringWidth2;

	private String rotation;
	
	

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

        final BorderLayout layout = new BorderLayout(0, 0);
        this.setLayout(layout);
        
        clapperRange = 100;
        clapperLoc = 240;
        
        containerRange = 190;
        containerLoc = 0;

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

                        setValue(rawVal + "," + (100-rawVal) + "," + (rawVal-45)*.5);
                        if (rawVal > 100) {
                            rawVal = 1;
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
        clapperVal     = (int) Double.parseDouble(vals[0]);
        containerVal   = (int) Double.parseDouble(vals[1]);
        rotVal 		   =       Double.parseDouble(vals[2]);
        } catch(Exception e){
        	new IllegalArgumentException(" Must be passed a string consisting of three doubles separated by commas: \n "
        			+ "Clapper percent height, container percent height, and strongback angle. ");
        }
        
        clapperRange   = (int)(270*scaling);
        clapperLoc     = (int)(80*scaling);
        
        containerRange = (int)(270*scaling);
        containerLoc   = (int)(0  *scaling);
        
        clapperY       = (int) ((clapperVal*.01   *  clapperRange   + clapperLoc  )+(-strongBack.getHeight()*scaling));
        containerY     = (int) ((containerVal*.01 *  containerRange + containerLoc)+(-strongBack.getHeight()*scaling));
        
        
        repaint();
    }

	@Override
    protected void paintComponent(final Graphics gg) {
		scaling = Math.min(getWidth(), getHeight())*0.0012;//0.0015;
		font1 = new Font("BoomBox", 0, (int)(35*scaling));
		font2 = new Font("BoomBox", 0, (int)(15*scaling));
		Graphics2D g = (Graphics2D) gg;
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHints(rh);
		g.setColor(Color.GREEN);
		g.setFont(font1);
		
		g.translate(scaling*400, scaling*750);
		
			g.rotate(Math.toRadians(rotVal));
				
				g.drawString(clapperVal + "", (int)(200*scaling), (int)(clapperY + 100*scaling));
				g.drawString(containerVal + "", (int)(50*scaling), containerY);
				temporaryStringWidth1 = g.getFontMetrics().getStringBounds(clapperVal   + "", g).getWidth();
				temporaryStringWidth2 = g.getFontMetrics().getStringBounds(containerVal + "", g).getWidth();
				g.setFont(font2);
				
				g.setColor(Color.YELLOW);
				g.drawString("%",(int)(200*scaling + temporaryStringWidth1), (int)(clapperY  + 100*scaling));
				g.drawString("%",(int)(50 *scaling + temporaryStringWidth2), (int)(containerY             ));
				
				g.drawImage (clapper,      (int)(strongBack.getWidth()*.5*scaling), clapperY,   (int)(clapper.getWidth()*scaling),     (int)(clapper.getHeight()*scaling),      null);	
				g.drawImage (containerArm, (int)(strongBack.getWidth()*.5*scaling), containerY, (int)(containerArm.getWidth()*scaling),(int)(containerArm.getHeight()*scaling), null);
				
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
		
		g.translate(-scaling*400, -scaling*750);
		
		//g.drawString(":" + scaling, 5, 25);


    }
}
