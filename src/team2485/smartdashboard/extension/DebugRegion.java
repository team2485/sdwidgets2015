package team2485.smartdashboard.extension;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import team2485.smartdashboard.extension.util.NiceBorder;

public class DebugRegion extends StaticWidget {
    public static final String NAME = "Debug Region";

    @Override
    public void init() {
        final Dimension size = new Dimension(400, 50);
        setSize(size);
        setPreferredSize(size);

        setBorder(new NiceBorder(Color.white, 1, 8));

        final JLabel label = new JLabel("Debug");
        label.setFont(new Font("Ubuntu", Font.PLAIN, 13));
        label.setForeground(Color.white);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label);
    }

    @Override
    public void propertyChanged(Property property) {
    }
}
