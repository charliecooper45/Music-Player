package view;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A JPanel which displays the icons at the bottom of the screen
 * @author Charlie
 */
@SuppressWarnings("serial")
public class BottomPanel extends JPanel{
	private JButton addTracksButton;
	private JButton settingsButton;
	
	public BottomPanel() {
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		init();
	}
	
	private void init() {
		settingsButton = new JButton("Settings");
		settingsButton.setName("settings");
		settingsButton.setIcon(Utils.createIcon("/view/resources/images/settingsicon.png"));
		add(settingsButton);
		
		addTracksButton = new JButton("Add Music");
		addTracksButton.setName("addmusic");
		addTracksButton.setIcon(Utils.createIcon("/view/resources/images/addicon.png"));
		add(addTracksButton);
	}
	
	/**
	 * Adds an ActionListener from the view to the appropriate components
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		// Add listener to the buttons
		addTracksButton.addActionListener(listener);
		settingsButton.addActionListener(listener);
	}
}
