package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JTable;

import model.MP3Model;
import model.TrackBean;
import view.MP3View;

public class MP3Controller extends MouseAdapter implements ActionListener, Observer{
	private MP3View view;
	private MP3Model model;

	public MP3Controller(MP3View view, MP3Model model) {
		this.view = view;
		this.view.setController(this);
		this.view.addActionListener(this);
		this.view.addMouseListener(this);
		this.model = model;
		this.model.addObserver(this);
	}
	
	public TrackBean getTrack(int trackNo) {
		return model.getTrack(trackNo);
	}
	
	public int getNumberOfTracks() {
		return model.getNumberOfTracks();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JButton) {
			// Handle events for the buttons
			JButton button = (JButton)e.getSource();
			String buttonName = button.getName();
			
			switch(buttonName) {
			case "play":
				if(model.getState() == model.getPausedState()) {
					model.resumeSong();
				}
				break;
			case "pause":
				model.pauseSong();
				break;
			case "addmusic":
				File[] files = view.showJFileChooser();
				if(files != null)
					model.processFiles(files);
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JTable table = (JTable)e.getSource();
		int selectedRow = table.getSelectedRow();
		TrackBean selectedTrack = getTrack(selectedRow);
		
		if(e.getClickCount() == 2) {
			model.stopSong();
			model.playSong(selectedTrack);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof MP3Model) {
			view.updateView(model.getArtists());
		}
	}
}
