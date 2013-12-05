package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javafx.util.Duration;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.AlbumBean;
import model.ArtistBean;
import model.MP3Model;
import model.TrackBean;
import view.MP3View;

public class MP3Controller extends MouseAdapter implements ActionListener, Observer, ListSelectionListener {
	private MP3View view;
	private MP3Model model;

	public MP3Controller(MP3View view, MP3Model model) {
		this.view = view;
		this.view.setController(this);
		this.view.addActionListener(this);
		this.view.addMouseListener(this);
		this.view.addListListener(this);
		this.model = model;
		this.model.addObserver(this);
		
	}

	public TrackBean getTrack(int trackNo) {
		return model.getTrack(trackNo);
	}

	public int getNumberOfTracks() {
		return model.getNumberOfTracks();
	}

	public Duration getCurrentTrackTime() {
		return model.getCurrentTrackTime();
	}
	
	// Methods for the listeners

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JButton) {
			// Handle events for the buttons
			JButton button = (JButton) e.getSource();
			String buttonName = button.getName();

			switch (buttonName) {
			case "play":
				if (model.getState() == model.getPausedState()) {
					model.resumeSong();
				}
				break;
			case "pause":
				model.pauseSong();
				break;
			case "addmusic":
				// TODO NEXT: Implement the JProgressBar for adding tracks (This should happen in a seperate thread)
				File[] files = view.showJFileChooser();
				if (files != null) model.processFiles(files);
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		
		if (source instanceof JTable) {
			JTable table = (JTable) source;
			int selectedRow = table.getSelectedRow();
			TrackBean selectedTrack = getTrack(selectedRow);

			if (e.getClickCount() == 2) {
				model.stopSong();
				model.playSong(selectedTrack);
				view.updatePlayingTrack(selectedTrack);
			}
		} else if(source instanceof JProgressBar) {
			JProgressBar progressBar = (JProgressBar) source;
			
			int mouseX = e.getX();
			
			// Calculate the percentage to set the track to
			int progressBarVal = (int)Math.round(((double)mouseX / (double)progressBar.getWidth()) * progressBar.getMaximum());
			double value = (100.0/progressBar.getMaximum()); 
			int percentage = (int) (progressBarVal * value);
			
			model.setTrackPercentagePlayed(percentage);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (!e.getValueIsAdjusting()) {
			@SuppressWarnings("unchecked")
			JList<Object> list = (JList<Object>) e.getSource();

			// Get the selected object in the list
			Object selected = list.getSelectedValue();

			// Check the type of the selected object
			if (selected instanceof ArtistBean) {
				ArtistBean artist = (ArtistBean) selected;

				// Update the album list to reflect the change of artist
				view.changeDisplayedArtist(artist);
			} else if (selected != null) {
				AlbumBean album = (AlbumBean) selected;

				// Change the tracks show in the table
				view.setTableTracks(album.getTracks());
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MP3Model) {
			view.updateArtists(model.getArtists());
		}
	}
}
