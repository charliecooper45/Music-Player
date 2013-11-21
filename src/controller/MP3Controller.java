package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.MP3Model;
import view.MP3View;

public class MP3Controller implements ActionListener {
	private MP3View view;
	private MP3Model model;

	public MP3Controller(MP3View view, MP3Model model) {
		this.view = view;
		view.addActionListener(this);
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// Handle events from buttons
		if (source instanceof JButton) {
			JButton button = (JButton)e.getSource();
			String buttonName = button.getName();
			
			switch(buttonName) {
			case "play":
				model.playSong();
				break;
			case "pause":
				model.pauseSong();
				break;
			}
			
		}
		
	}
}
