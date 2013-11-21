package view;
import javax.swing.SwingUtilities;

import model.MP3Model;
import controller.MP3Controller;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			       MP3View theView = new MP3View();
			       MP3Model theModel = new MP3Model();
			       MP3Controller theController = new MP3Controller(theView,theModel);

			       theView.setVisible(true);
			}
		});
	}
}
