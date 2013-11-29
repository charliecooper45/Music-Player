package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
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
	private MiddlePanel middlePanel;
	private BottomPanel bottomPanel;
	private JFileChooser fileChooser;
	
	public MP3View() {
		super("MP3 Player");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}
	
	private void init() {
		topPanel = new TopPanel();
		add(topPanel, BorderLayout.NORTH);
		
		middlePanel = new MiddlePanel();
		middlePanel.setTableValueListener(new TableValueListener() {
			@Override
			public Object getTableValue(int rowIndex) {
				return controller.getTrack(rowIndex);
			}

			@Override
			public int getNumberOfTracks() {
				return controller.getNumberOfTracks();
			}
		});
		add(middlePanel, BorderLayout.CENTER);
		
		bottomPanel = new BottomPanel();
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public void addActionListener(ActionListener listener) {
		topPanel.addActionListener(listener);
		bottomPanel.addActionListener(listener);
	}
	
	public void addMouseListener(MouseListener listener) {
		middlePanel.addMouseListener(listener);
	}
	
	public void setController(MP3Controller controller) {
		this.controller = controller;
	}
	
	public File[] showJFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select music to add");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		
		int returnVal = fileChooser.showOpenDialog(this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFiles();
		}
		return null;
	}
	
	public void updateView(List<String> artists) {
		middlePanel.updatePanel(artists);
	}
}
