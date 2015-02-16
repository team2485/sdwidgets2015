package team2485.smartdashboard.extension;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WarlordsWindow {
	
	
private BufferedImage Window, Top, Bottom, Left, Right, TopLeft, TopRight, BottomLeft, BottomRight;
    

	public WarlordsWindow(){	
    	try {
        	Window      = ImageIO.read(getClass().getResourceAsStream("/team2485/smartdashboard/extension/res/warlordswindow.png"));
        	
        	TopLeft     = Window.getSubimage(0,                     0,                      250, 250);
        	TopRight    = Window.getSubimage(Window.getWidth()-250, 0,                      250, 250);
        	BottomLeft  = Window.getSubimage(0,                     Window.getHeight()-250, 250, 250);
        	BottomRight = Window.getSubimage(Window.getWidth()-250, Window.getHeight()-250, 250, 250);
        	
        	Top         = Window.getSubimage(250,                   0,                      250, 250);
        	Bottom      = Window.getSubimage(250,                   Window.getHeight()-250, 250, 250);
        	Left        = Window.getSubimage(0,                     200,                    250, 150);
        	Right       = Window.getSubimage(Window.getWidth()-250, 200,                    250, 150);
        	
        } catch (IOException e) {}
	}
    	
	protected void paintComponent(final Graphics gg, double scale, int width, int height) {
		int intScale = (int) scale;
		int adjustedWidth  = (int) (width  - 2*scale);
		int adjustedHeight = (int) (height - 2*scale);
		
		gg.drawImage(TopLeft , 0,						 0, intScale,      intScale, null);
		gg.drawImage(Top     , intScale,				 0, adjustedWidth, intScale, null);
		gg.drawImage(TopRight, intScale + adjustedWidth, 0, intScale     , intScale, null);
		
		gg.drawImage(Left    , 0,                        intScale, intScale, adjustedHeight, null);
		gg.drawImage(Right   , intScale + adjustedWidth, intScale, intScale, adjustedHeight, null);
		
		gg.drawImage(BottomLeft , 0,                        intScale + adjustedHeight, intScale,      intScale, null);
		gg.drawImage(Bottom     , intScale,                 intScale + adjustedHeight, adjustedWidth, intScale, null);
		gg.drawImage(BottomRight, intScale + adjustedWidth, intScale + adjustedHeight, intScale,      intScale, null);
	}

	public void paintComponent(final Graphics gg, double scale, double width, double height) {
		int intScale = (int) scale;
		int adjustedWidth  = (int) (width  - 2*scale);
		int adjustedHeight = (int) (height - 2*scale);
		
		gg.drawImage(TopLeft , 0,						 0, intScale,      intScale, null);
		gg.drawImage(Top     , intScale,				 0, adjustedWidth, intScale, null);
		gg.drawImage(TopRight, intScale + adjustedWidth, 0, intScale     , intScale, null);
		
		gg.drawImage(Left    , 0,                        intScale, intScale, adjustedHeight, null);
		gg.drawImage(Right   , intScale + adjustedWidth, intScale, intScale, adjustedHeight, null);
		
		gg.drawImage(BottomLeft , 0,                        intScale + adjustedHeight, intScale,      intScale, null);
		gg.drawImage(Bottom     , intScale,                 intScale + adjustedHeight, adjustedWidth, intScale, null);
		gg.drawImage(BottomRight, intScale + adjustedWidth, intScale + adjustedHeight, intScale,      intScale, null);
		
	}

}
