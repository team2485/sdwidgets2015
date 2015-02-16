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
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class ToteWidget extends Widget{
	
	public static final String NAME = "2485 Tote Counter";
    public static final DataType[] TYPES = {DataType.NUMBER};
	private BufferedImage toteImg;
	private double value;
	private int scaling;
	
	@Override
	public void init() {
		try {
            toteImg = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/tote.png"));
        } catch (IOException e) {}
		 final Dimension size = new Dimension(300, 200);
	        this.setSize(size);	
	        this.setPreferredSize(size);
	        this.setMinimumSize(new Dimension(50, 50));
	        this.setMaximumSize((new Dimension(800, 800)));

		
	}

	@Override
	public void propertyChanged(Property arg0) {
			
	}

	@Override
	public void setValue(Object arg0) {
		value = ((Number)arg0).doubleValue();
		repaint();
	}
	
	protected void paintComponent(final Graphics gg) {
		final Graphics2D g = (Graphics2D) gg;
		
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		scaling = (int) Math.min(getWidth()*.65, getHeight());	
		
		g.setColor(Color.YELLOW);
		g.setFont(new Font("BOOMBOX", Font.PLAIN, (int)scaling*2/3));
		g.drawImage(toteImg, 0, 0, scaling * toteImg.getWidth()/toteImg.getHeight(),scaling,null);
		g.drawString("0" + (int)value, (int)(scaling * toteImg.getWidth()/toteImg.getHeight() - g.getFontMetrics().getStringBounds("0" + (int)value, null).getWidth())/2, (int)(scaling + g.getFontMetrics().getHeight()/1.5)/2);
	}
}
