package view;

import java.awt.GridBagConstraints;
import java.net.URL;

import javax.swing.ImageIcon;

public class Utils {
	private Utils() {};
	
	public static ImageIcon createIcon(String path) {
		URL url = System.class.getResource(path);

		if(url == null) {
			System.err.println("Unable to load image: + path");
		}
		
		ImageIcon icon = new ImageIcon(url);
		
		return icon;
	}
	
	public static void setGBC(GridBagConstraints gc, int column, int row, int width, int height, int fill) {
		gc.gridx = column;
		gc.gridy = row;
		gc.gridwidth = width;
		gc.gridheight = height;
		gc.fill = fill;
	}
}
