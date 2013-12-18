package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javafx.util.Duration;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import model.ArtistBean;
import model.TrackBean;
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
	private ProgressDialog progressDialog; 
	
	public MP3View() {
		super("MP3 Player");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}
	
	private void init() {
		topPanel = new TopPanel();
		topPanel.setTopPanelListener(new TopPanelListener() {
			@Override
			public Duration getCurrentTrackTime() {
				return controller.getCurrentTrackTime();
			}
		});
		add(topPanel, BorderLayout.NORTH);
		
		middlePanel = new MiddlePanel();
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
		topPanel.addMouseListener(listener);
	}
	
	public void addListListener(ListSelectionListener listener) {
		middlePanel.addListListener(listener);
	}
	
	public void addVolumeChangeListener(ChangeListener listener) {
		topPanel.addVolumeChangeListener(listener);
	}
	
	/**
	 * @param tableTracks the tableTracks to set
	 */
	public void setTableTracks(List<TrackBean> tableTracks) {
		middlePanel.setTableTracks(tableTracks);
	}

	public void setController(MP3Controller controller) {
		this.controller = controller;
	}
	
	public void updatePlayingTrack(TrackBean track) {
		topPanel.updatePlayingTrack(track);
	}
	
	public File[] showJFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {

	        @Override
	        public boolean accept(File f) {
	            return f.getName().endsWith(".mp3");
	        }

	        @Override
	        public String getDescription() {
	            return "MP3 files";
	        }

	    });
		fileChooser.setDialogTitle("Select music to add");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		
		int returnVal = fileChooser.showOpenDialog(this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFiles();
		}
		return null;
	}
	
	public void showProgressDialog(int maximumSize, ActionListener listener) {
		progressDialog = new ProgressDialog(this, maximumSize);
		progressDialog.addActionListener(listener);
		progressDialog.setProgressDialogListener(new ProgressDialogListener() {
			@Override
			public int getNumberProcessed() {
				return controller.getNumberProcessed();
			}
		});
		progressDialog.setVisible(true);
	}
	
	public void disposeProgressDialog() {
		progressDialog.disposeDialog();
	}
	
	public void updateArtists(List<ArtistBean> artists) {
		middlePanel.updateArtists(artists);
	}

	public void changeDisplayedArtist(ArtistBean artist) {
		middlePanel.changeDisplayedArtist(artist);
	}
}
