package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	//TODO NEXT: Check for unused methods
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
		this.model = model;
		this.model.addObserver(this);

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

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MP3Model) {
			if (arg instanceof List) {
				view.updateArtists(model.getArtists());
			} else if (arg instanceof TrackBean) {
				view.updatePlayingTrack((TrackBean) arg);
			} else if (arg == null) {
				view.stopPlayingTrack();
			}
		}
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
					final File[] files = view.showJFileChooser();

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
						view.showProgressDialog(mp3Count, buttonListener);
					}
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
			//TODO NEXT B: change this to lowercase
			case "Add Track":
				List<TrackBean> selectedTracks = view.getSelectedTracks();
				model.addTracksToPlaylist(selectedTracks);
				view.setDisplayedPlaylist(model.getPlaylist());
				// TODO NEXT: Need to add functionality for adding to existing playlist and new playlist, right click -> clear playlist
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
				
				view.showPopupMenu(table, e.getX(), e.getY());
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
				double doubleValue = (double) (value / 100.0);
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
}
