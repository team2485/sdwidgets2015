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

public class ClapperandContainer extends Widget {

    public static final String NAME = "Clapper and Container";
    public static final DataType[] TYPES = {DataType.STRING};


    Property smoothingfactor = new IntegerProperty(this, "Smoothing Factor", 5);
    Property style = new IntegerProperty(this, "Style", 2);
    Property Test = new BooleanProperty(this, "Test", false);

    private BufferedImage containerArm, clapper, background;
	private int clapperVal;
	private int ContainerVal;
	private int[] preVal;
	private int rawVal;

    @Override
    public void init() {

        try {
        	clapper = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/clapper.png"));
        } catch (IOException e) {
        }

        final Dimension size = new Dimension(300, 300);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(new Dimension(100, 100));
        this.setMaximumSize((new Dimension(800, 800)));

        final BorderLayout layout = new BorderLayout(0, 0);
        this.setLayout(layout);
        this.propertyChanged(style);

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

                        setValue(rawVal + "," + rawVal);
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

    @Override
    public void setValue(Object o) {
        final String[] vals = ((String) o).split(",");
        clapperVal = (int) (Double.parseDouble(vals[0]));
        ContainerVal = (int) Double.parseDouble(vals[1]);
        
        repaint();
    }

	@Override
    protected void paintComponent(final Graphics gg) {
		Graphics2D g = (Graphics2D) gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.YELLOW);
		g.drawImage(clapper, 0, clapperVal, clapper.getWidth()/5,clapper.getHeight()/5, null);
		g.drawString("" + clapperVal, 0, 0);


    }
}
