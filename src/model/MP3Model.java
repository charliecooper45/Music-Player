package model;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//TODO NEXT: Implement the state pattern here
public class MP3Model {
	private MediaPlayer player;

	//TODO NEXT B: Document
	public void playSong() {
		if (player != null) {
			player.play();
		} else {
			new JFXPanel();
			Path path = Paths.get("./one.mp3");
			player = new MediaPlayer(new Media(path.toUri().toString()));
			player.play();
		}
	}
	
	//TODO NEXT B: Document
	public void pauseSong() {
		player.pause();
	}
}
