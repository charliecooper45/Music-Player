package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Displays the MP3 player user interface.
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class MP3View extends JFrame {
	private TopPanel topPanel;
	
	public MP3View() {
		super("MP3 Player");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}
	
	private void init() {
		topPanel = new TopPanel();
		add(topPanel, BorderLayout.NORTH);
	}
	
	public void addActionListener(ActionListener listener) {
		topPanel.addActionListener(listener);
	}
}
