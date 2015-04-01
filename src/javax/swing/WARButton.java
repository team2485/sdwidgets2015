package javax.swing;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import team2485.smartdashboard.extension.WarlordsWindow;

public class WARButton extends JButton {
	WarlordsWindow window;
	private String	name;
	
	public WARButton(){
		window = new WarlordsWindow();
		this.name = "";
	}
	
	public WARButton(String name){
		window = new WarlordsWindow();
		this.name = name;
	}
	
	public void paint(Graphics g){
		window.paintComponent(g, 20, getWidth(), getHeight());
		g.setFont(new Font("Boombox", Font.PLAIN, 20));
		g.setColor(Color.green);
		g.drawString(name,(getWidth() - g.getFontMetrics().stringWidth(name))/2, getHeight()*4/7);
		
	}
}
