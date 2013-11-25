package view;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A JPanel which displays the add icon
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class BottomPanel extends JPanel{
	private JButton addTracksButton;
	
	public BottomPanel() {
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		init();
	}
	
	private void init() {
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
	}
}
