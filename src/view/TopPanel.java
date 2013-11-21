package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;

/**
 * A JPanel which displays information about the track and buttons to manipulate the playing state. 
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class TopPanel extends JPanel {
	private GridBagConstraints gc;
	private JLabel time;
	private JLabel trackInfo;
	private JSlider volumeControl;
	private JProgressBar trackProgress;
	private JButton[] controlButtons;

	public TopPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.GRAY);
		gc = new GridBagConstraints();
		init();
	}

	private void init() {
		time = new JLabel("0:00");
		time.setFont(new Font(Font.SERIF, Font.BOLD, 40));
		time.setHorizontalAlignment(JLabel.CENTER);
		Utils.setGBC(gc, 1, 1, 1, 2, GridBagConstraints.BOTH);
		gc.weightx = 1;
		gc.weighty = 1;
		add(time, gc);

		trackInfo = new JLabel("Arcade Fire - Intervention(4:19)");
		Utils.setGBC(gc, 2, 1, 1, 1, GridBagConstraints.BOTH);
		add(trackInfo, gc);

		volumeControl = new JSlider(JSlider.HORIZONTAL);
		Utils.setGBC(gc, 2, 2, 1, 1, GridBagConstraints.BOTH);
		gc.weighty = 0.8;
		add(volumeControl, gc);

		trackProgress = new JProgressBar(JProgressBar.HORIZONTAL);
		trackProgress.setEnabled(false);
		Utils.setGBC(gc, 1, 3, 2, 1, GridBagConstraints.BOTH);
		gc.weighty = 1;
		add(trackProgress, gc);

		controlButtons = new JButton[8];
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel modeButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// TODO NEXT: Add these buttons with there relevant icons
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i] = new JButton("A");
			if (i < 5) {
				buttonsPanel.add(controlButtons[i]);
			} else {
				modeButtonsPanel.add(controlButtons[i]);
			}
			// Load the icons for each image
			switch(i) {
			case 0:
				break;
			case 1:
				controlButtons[i].setName("play");
				break;
			case 2:
				controlButtons[i].setName("pause");
			}
		}
		Utils.setGBC(gc, 1, 4, 1, 1, GridBagConstraints.BOTH);
		add(buttonsPanel, gc);
		Utils.setGBC(gc, 2, 4, 1, 1, GridBagConstraints.BOTH);
		add(modeButtonsPanel, gc);
		
		
	}

	/**
	 * Adds an ActionListener from the view to the appropriate components
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		// Add listener to the buttons
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].addActionListener(listener);
		}
	}
}
