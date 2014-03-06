package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.util.Duration;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.AlbumBean;
import model.AllArtistsBean;
import model.ArtistBean;
import model.MP3Model;
import model.TrackBean;
import view.MP3View;

public class MP3Controller implements Observer {
	private MP3View view;
	private MP3Model model;

	// Listeners 
	private ActionListener buttonListener = new ButtonListener();

	public MP3Controller(MP3View view, MP3Model model) {
		this.view = view;
		this.view.setController(this);
		this.view.addWindowListener(new WindowChangeListener());
		this.view.addActionListener(buttonListener);
		this.view.addMouseListener(new MouseListener());
		this.view.addListListener(new ListListener());
		this.view.addVolumeChangeListener(new VolumeChangeListener());
		this.view.addPopupMenuListener(new PopupMenuListener());
		this.view.addKeyListener(new KeyListener());
		this.model = model;
		this.model.addObserver(this);
		
		boolean lastFmState = model.getLastFMState();
		if(lastFmState) {
			view.setLastFmStatus(lastFmState, model.getLastFmUsername());
		}
	}

	public Duration getCurrentTrackTime() {
		return model.getCurrentTrackTime();
	}

	public int getNumberProcessed() {
		return model.getNumberProcessed();
	}

	public void songFinished() {
		model.playNextSong();
	}

	public void updateTrackDetails(TrackBean track, int trackNumber, String title, String artist, String album, String genre, String comments) {
		model.updateTrackDetails(track, trackNumber, title, artist, album, genre, comments);
		// Update the view
		//view.setTableTracks(view.getSelectedAlbum().getTracks());
		view.updateArtists(model.getArtists());
		view.refreshTableTracks();
	}
	
	public boolean changeLastFMState(boolean on, String username, String password) {
		return model.changeLastFMState(on, username, password);
	}
	
	public boolean getLastFMState() {
		return model.getLastFMState();
	}
	
	public boolean scrobbleTrack(TrackBean currentTrack) {
		return model.scrobbleTrack(currentTrack);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MP3Model) {
			if (arg instanceof List) {
				List<?> list = (List<?>) arg;
				if(list.get(0).getClass().equals(TrackBean.class)) {
					// If the list is full of tracks then we need to update the playlist
					view.setDisplayedPlaylist(model.getPlaylist());
				} else if (list.get(0).getClass().equals(File.class)) {
					// Display an error message to the user
					view.displayErrorMessage("Unable to add " + list.size() + " files. Please check the log.");
				} else {
					// Update the displayed artists
					view.updateArtists(model.getArtists());
				}
			} else if (arg instanceof TrackBean) {
				view.updatePlayingTrack((TrackBean) arg);
			} else if (arg == null) {
				view.stopPlayingTrack();
			} else if (arg instanceof String) {
				System.out.println("Update lastfm here!");
			}
		}
	}

	private void removeTracks() {
		List<TrackBean> tracks = view.getSelectedTracks();
		AlbumBean tracksAlbum = tracks.get(0).getAlbum();
		
		boolean delete = view.displayPopupMenu(view, -1, -1);
		if(delete)
			model.deleteTracks(tracks);
		view.setTableTracks(tracksAlbum.getTracks());
		view.updateArtists(model.getArtists());
	}
	
	// listener classes
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source instanceof JButton) {
				// Handle events for the buttons
				JButton button = (JButton) e.getSource();
				String buttonName = button.getName();

				switch (buttonName) {
				case "backward":
					model.playPreviousSong();
					break;
				case "play":
					model.play();
					break;
				case "pause":
					model.pauseSong();
					break;
				case "stop":
					model.stopSong(false);
					view.stopPlayingTrack();
					break;
				case "forward":
					model.playNextSong();
					break;
				case "addmusic":
					final File[] files = view.displayJFileChooser();

					if (files != null) {
						int mp3Count = model.getMP3Count(files);
						Runnable run = new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								model.startProcessFilesThread(files);
							}
						};
						new Thread(run).start();
						view.displayProgressDialog(mp3Count, buttonListener);
					}
					break;
				case "settings":
					String username = null;
					String password = null;
					boolean lastFmActive = model.getLastFMState();
					if(lastFmActive) {
						 username = model.getLastFmUsername();
						 password = model.getLastFmPassword();
					}
					view.displaySettingsDialog(username, password);
					break;
				case "cancel":
					model.stopProcessFilesThread();
					view.disposeProgressDialog();
					break;
				case "mute":
					model.mute();
					view.changeMuteIcon();
					break;
				case "shuffle":
					boolean shuffled = !model.isShuffled();
					model.setShuffled(shuffled);
					view.setShuffle(shuffled);
					// After the shuffle the playlist will have changed so update the view
					view.setDisplayedPlaylist(model.getPlaylist());
					break;
				case "loop":
					boolean looped = !model.isLooped();
					model.setLooped(looped);
					view.setLooped(looped);
					break;
				}
			}
		}
	}

	private class PopupMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem popup = (JMenuItem) e.getSource();
			String name = popup.getName();
			
			switch(name) {
			case "add track":
				List<TrackBean> selectedTracks = view.getSelectedTracks();
				model.addTracksToPlaylist(selectedTracks);
				view.setDisplayedPlaylist(model.getPlaylist());
				break;
			case "add album":
				AlbumBean selectedAlbum = view.getSelectedAlbum();
				model.addTracksToPlaylist(selectedAlbum.getTracks());
				view.setDisplayedPlaylist(model.getPlaylist());
				break;
			case "clear playlist":
				model.stopSong(false);
				view.setDisplayedPlaylist(model.getPlaylist());
				break;
			case "remove track":
				removeTracks();
				break;
			case "get info":
				view.displayInfoDialog(view.getSelectedTracks().get(0));
				break;
			}
		}
	}
	
	private class MouseListener extends MouseAdapter {
		private Object source;

		@Override
		public void mouseClicked(MouseEvent e) {
			source = e.getSource();

			if (source instanceof JTable) {
				tableClicked(e);
			} else if (source instanceof JProgressBar) {
				JProgressBar progressBar = (JProgressBar) source;

				int mouseX = e.getX();

				// Calculate the percentage to set the track to
				int progressBarVal = (int) Math.round(((double) mouseX / (double) progressBar.getWidth()) * progressBar.getMaximum());
				double value = (100.0 / progressBar.getMaximum());
				int percentage = (int) (progressBarVal * value);

				model.setTrackPercentagePlayed(percentage);
			}
		}

		private void tableClicked(MouseEvent e) {
			boolean isSelected = false;
			JTable table = (JTable) source;
			
			if (SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
				int row = table.rowAtPoint(e.getPoint());
				
				int[] rows = table.getSelectedRows();
				
				for(int i = 0; i < rows.length; i++) {
					if(rows[i] == row) {
						isSelected = true;
						break;
					}
				}
				
				if(!isSelected) {
					// The row that has been clicked on was not previously selected so select it
					table.changeSelection(row, 0, false, false);
				}
				
				view.displayPopupMenu(table, e.getX(), e.getY());
			} else {
				int selectedRow = table.getSelectedRow();

				if (selectedRow >= 0) {

					if (e.getClickCount() == 2) {
						model.stopSong(true);
						model.setAlbum(view.getSelectedAlbum(), view.getTrackNumber());
						model.playPlaylist();
						view.setDisplayedPlaylist(model.getPlaylist());
					}
				}
			}
		}
	}

	private class ListListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting()) {
				@SuppressWarnings("unchecked")
				JList<Object> list = (JList<Object>) e.getSource();

				// Get the selected object in the list
				Object selected = list.getSelectedValue();

				// Check the type of the selected object
				if (selected instanceof ArtistBean) {
					if (selected instanceof AllArtistsBean) {
						// Display all the albums
						view.displayAllAlbums(model.getAllAlbums());
					} else {
						ArtistBean artist = (ArtistBean) selected;

						// Update the album list to reflect the change of artist
						view.changeDisplayedArtist(artist);
					}

				} else if (selected != null) {
					AlbumBean album = (AlbumBean) selected;

					// Change the tracks show in the table
					view.setTableTracks(album.getTracks());
				}
			}
		}
	}

	private class VolumeChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			Object source = e.getSource();

			if (source instanceof JSlider) {
				JSlider slider = (JSlider) source;
				int value = slider.getValue();

				// Convert to a double value for use with the model 
				double doubleValue = value / 100.0;
				model.setVolume(doubleValue);
			}
		}

	}

	private class WindowChangeListener extends WindowAdapter {

		@Override
		public void windowOpened(WindowEvent arg0) {
			if (!model.openPlayer()) {
				view.displayErrorMessage("Unable to load track database");
			}

			// Refresh the gui
			view.updateArtists(model.getArtists());
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if (!model.closePlayer()) {
				view.displayErrorMessage("Unable to save track database");
			}
		}
	}

	private class KeyListener extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			if(e.getKeyChar() == KeyEvent.VK_DELETE) {
				System.out.println("Key Typed + delete");
				removeTracks();
			}
		}
	}

}
