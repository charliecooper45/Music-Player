package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel which displays the icons at the bottom of the screen
 * @author Charlie
 */
@SuppressWarnings("serial")
public class BottomPanel extends JPanel{
	private JPanel lastFmPanel;
	private JLabel statusLabel;
	private static final String ONLINE_TEXT = "online";
	private static final String OFFLINE_TEXT = "offline";
	
	private JPanel buttonsPanel;
	private JButton addTracksButton;
	private JButton settingsButton;
	
	public BottomPanel() {
		setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		lastFmPanel = new JPanel();
		lastFmPanel.add(new JLabel("Last.fm status:"));
		statusLabel = new JLabel(OFFLINE_TEXT);
		lastFmPanel.add(statusLabel);
		add(lastFmPanel, BorderLayout.WEST);
		
		buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		settingsButton = new JButton("Settings");
		settingsButton.setName("settings");
		settingsButton.setIcon(Utils.createIcon("/view/resources/images/settingsicon.png"));
		buttonsPanel.add(settingsButton);
		addTracksButton = new JButton("Add Music");
		addTracksButton.setName("addmusic");
		addTracksButton.setIcon(Utils.createIcon("/view/resources/images/addicon.png"));
		buttonsPanel.add(addTracksButton);
		add(buttonsPanel, BorderLayout.CENTER);
	}
	
	public void setLastFmStatus(boolean status, String user) {
		if(status) {
			statusLabel.setText(ONLINE_TEXT + " (" + user + ")");
		} else {
			statusLabel.setText(OFFLINE_TEXT);
		}
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
