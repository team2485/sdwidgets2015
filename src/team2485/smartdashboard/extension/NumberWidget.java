package team2485.smartdashboard.extension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

public class NumberWidget extends Widget
{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "2485 Number";
    public static final DataType[] TYPES = {DataType.NUMBER};
    
    private double val;
    
    private WarlordsWindow window;
    
    private final DoubleProperty  GUISize = new DoubleProperty(this, "GUI Size", 30);
    private final BooleanProperty Integer = new BooleanProperty(this, "Integer Toggle", false);
	private double value;
    
    @Override
	public void init() {
    	Dimension size = new Dimension(150, 150);
        setSize(size);
        setPreferredSize(size);
        setResizable(true);
        
        window = new WarlordsWindow();
	}
    
	@Override
	public void propertyChanged(Property arg0) {
		repaint();		
	}

	@Override
	public void setValue(Object arg0) {
		value = ((Number)arg0).doubleValue();
		repaint();
	}
	
	protected void paintComponent(final Graphics gg) {
		double guiSize = GUISize.getValue();
		int intVal = (int) value;
		
		gg.setColor(Color.YELLOW);
		gg.setFont(new Font("BOOMBOX", Font.PLAIN, (int)guiSize));
		if (Integer.getValue()){
			gg.drawString("" + intVal, (int)(getWidth() - gg.getFontMetrics().getStringBounds("" + intVal, null).getWidth())/2, (int)((getHeight() + gg.getFontMetrics().getHeight()/2)/2 - guiSize*.2));
		} else {
			gg.drawString("" + value, (int)(getWidth() - gg.getFontMetrics().getStringBounds("" + value, null).getWidth())/2, (int)((getHeight() + gg.getFontMetrics().getHeight()/2)/2 - guiSize*.2));
		}
		
		gg.setColor(Color.GREEN);
		gg.setFont(new Font("BOOMBOX", Font.PLAIN, (int)(guiSize*.5)));
		gg.drawString(getFieldName(), (int)(getWidth() - gg.getFontMetrics().getStringBounds("" + getFieldName(), null).getWidth())/2, (int)(getHeight()/2 + gg.getFontMetrics().getHeight()/4+guiSize*.5));
		
		window.paintComponent(gg, GUISize.getValue(), getWidth(), getHeight());
	}	
}
