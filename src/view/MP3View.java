package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javafx.util.Duration;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import model.AlbumBean;
import model.ArtistBean;
import model.TrackBean;
import controller.MP3Controller;

/**
 * Displays the MP3 player user interface.
 * @author Charlie
 */
@SuppressWarnings("serial")
public class MP3View extends JFrame {
	private MP3Controller controller;
	private TopPanel topPanel;
	private MiddlePanel middlePanel;
	private BottomPanel bottomPanel;
	private JFileChooser fileChooser;
	private SettingsDialog settingsDialog;
	private ProgressDialog progressDialog;
	
	public MP3View() {
		super("CMedia Player");
		setMinimumSize(new Dimension(600, 500));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}

	private void init() {
		setIconImage(Utils.createIcon("/view/resources/images/icon.png").getImage());
		topPanel = new TopPanel();
		topPanel.setTopPanelListener(new TopPanelListener() {
			@Override
			public Duration getCurrentTrackTime() {
				return controller.getCurrentTrackTime();
			}

			@Override
			public void trackFinished() {
				// The current track has finished playing so alert the controller
				controller.songFinished();
			}
			
			@Override
			public void scrobbleTrack(TrackBean currentTrack) {
				if(!controller.scrobbleTrack(currentTrack)) {
					MP3View.this.displayErrorMessage("Unable to scrobble to last.fm");
				}
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

	public void addPopupMenuListener(ActionListener listener) {
		middlePanel.addActionListener(listener);
	}
	
	@Override
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
	
	@Override
	public void addKeyListener(KeyListener listener) {
		middlePanel.addKeyListener(listener);
	}
	
	/**
	 * @param tableTracks the tableTracks to set
	 */
	public void setTableTracks(List<TrackBean> tableTracks) {
		middlePanel.setTableTracks(tableTracks);
	}
	
	public void refreshTableTracks() {
		middlePanel.refreshTableTracks();
	}

	public List<TrackBean> getSelectedTracks() {
		return middlePanel.getSelectedTracks();
	}
	
	public void setController(MP3Controller controller) {
		this.controller = controller;
	}

	public void updatePlayingTrack(TrackBean track) {
		topPanel.updatePlayingTrack(track);
		middlePanel.updatePlaylist(track);
	}

	public void stopPlayingTrack() {
		topPanel.stopPlayingTrack();
		middlePanel.updatePlaylist(null);
	}

	public File[] displayJFileChooser() {
		fileChooser = new JFileChooser(new File("C:/Users/Charlie/Desktop"));
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

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFiles();
		}
		return null;
	}
	
	public void displaySettingsDialog(String username, String password) {
		if(settingsDialog == null) {
			SettingsChangedListener settingsChangedListener = new SettingsChangedListener() {
				@Override
				public boolean lastFMOn(String username, String password) {
					boolean success = controller.changeLastFMState(true, username, password);
					if(!success) {
						// The username or password is incorrect
						displayErrorMessage("Cannot connect to Last.fm, please check your username and password.");
						return false;
					}
					bottomPanel.setLastFmStatus(true, username);
					topPanel.setLastFmStatus(true);
					return true;
				}
				
				@Override
				public void lastFMOff() {
					controller.changeLastFMState(false, null, null);
					bottomPanel.setLastFmStatus(false, null);
					topPanel.setLastFmStatus(false);
				}
			};
			boolean lastFmOn = controller.getLastFMState();
			settingsDialog = new SettingsDialog(this, settingsChangedListener, lastFmOn, username, password);
		}
		
		settingsDialog.setVisible(true);
	}
	
	public void setLastFmStatus(boolean on, String username) {
		bottomPanel.setLastFmStatus(on, username);
		topPanel.setLastFmStatus(on);
	}

	public void displayProgressDialog(int maximumSize, ActionListener listener) {
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
	
	public void displayErrorMessage(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void displayInfoDialog(TrackBean track) {
		TrackEditedListener trackEditedListener = new TrackEditedListener() {
			@Override
			public void trackChanged(TrackBean track, int trackNumber, String title, String artist, String album, String genre, String comments) {
				controller.updateTrackDetails(track, trackNumber, title, artist, album, genre, comments);
			}					
		};
		InfoDialog infoDialog = new InfoDialog(this, track, trackEditedListener);
		infoDialog.setVisible(true);
	}

	public void disposeProgressDialog() {
		progressDialog.disposeDialog();
	}

	public void updateArtists(List<ArtistBean> artists) {
		middlePanel.updateArtists(artists);
	}
	
	public void setShuffle(boolean shuffle) {
		topPanel.changeShuffleIcon(shuffle);
	}
	
	public void setLooped(boolean looped) {
		topPanel.changeLoopIcon(looped);
	}
	
	public void changeMuteIcon() {
		topPanel.changeMuteIcon();
	}

	public void changeDisplayedArtist(ArtistBean artist) {
		middlePanel.changeDisplayedAlbums(artist.getAlbums());
	}

	public void displayAllAlbums(List<AlbumBean> albums) {
		middlePanel.changeDisplayedAlbums(albums);
	}

	public void setDisplayedPlaylist(List<TrackBean> playlist) {
		middlePanel.setDisplayedPlaylist(playlist);
	}

	public boolean displayPopupMenu(Object component, int x, int y) {
		return middlePanel.showPopupMenu(component, x, y);
	}
	
	public AlbumBean getSelectedAlbum() {
		return middlePanel.getSelectedAlbum();
	}
	
	public int getTrackNumber() {
		return middlePanel.getTrackNumber();
	}
}
