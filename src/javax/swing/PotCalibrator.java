package javax.swing;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;

import java.awt.GridLayout;

public class PotCalibrator extends StaticWidget {
	public PotCalibrator() {

	}

	public static final String		NAME		= "Pot Calibrator";
	public static final DataType[]	TYPES		= { DataType.NUMBER };
	private static final String		FIELD_NAME	= "Pot Calibrator";

	@Override
	public void init() {
		// TODO Auto-generated method stub

		setLayout(new GridLayout(1, 0, 0, 0));

		WARButton button1 = new WARButton("Clapper: Low");
		add(button1);

		WARButton button2 = new WARButton("Clapper: High");
		add(button2);

		WARButton button3 = new WARButton("Claw: Low");
		add(button3);

		WARButton button4 = new WARButton("Claw: High");
		add(button4);
		
	}

	@Override
	public void propertyChanged(Property arg0) {
		// TODO Auto-generated method stub

	}
}
