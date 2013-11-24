package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import controller.MP3Controller;

/**
 * Displays the MP3 player user interface.
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class MP3View extends JFrame {
	private MP3Controller controller;
	private TopPanel topPanel;
	private BottomPanel bottomPanel;
	
	public MP3View() {
		super("MP3 Player");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}
	
	private void init() {
		topPanel = new TopPanel();
		add(topPanel, BorderLayout.NORTH);
		
		bottomPanel = new BottomPanel();
		bottomPanel.setTableValueListener(new TableValueListener() {
			@Override
			public Object getTableValue(int rowIndex) {
				return controller.getTrack(rowIndex);
			}
		});
		add(bottomPanel, BorderLayout.CENTER);
	}
	
	public void addActionListener(ActionListener listener) {
		topPanel.addActionListener(listener);
	}
	
	public void addMouseListener(MouseListener listener) {
		bottomPanel.addMouseListener(listener);
	}
	
	public void setController(MP3Controller controller) {
		this.controller = controller;
	}
}
