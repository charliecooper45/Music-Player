package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTable;

import model.MP3Model;
import model.TrackBean;
import view.MP3View;

public class MP3Controller extends MouseAdapter implements ActionListener{
	private MP3View view;
	private MP3Model model;

	public MP3Controller(MP3View view, MP3Model model) {
		this.view = view;
		this.view.setController(this);
		this.view.addActionListener(this);
		this.view.addMouseListener(this);
		this.model = model;
	}
	
	public TrackBean getTrack(int trackNo) {
		return model.getTrack(trackNo);
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
}
