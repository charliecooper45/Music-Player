package model;

import java.nio.file.Path;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class InitialState implements State {
	private MP3Model model;
	
	public InitialState(MP3Model model) {
		this.model = model;
	}
	
	@Override
	public void playSong(TrackBean bean) {
		new JFXPanel();
		Path path = bean.getLocation();
		MediaPlayer player = new MediaPlayer(new Media(path.toUri().toString()));
		model.setPlayer(player);
		player.play();
		model.setState(model.getPlayingState());
	}

	@Override
	public void pauseSong() {
		// Do nothing
	}
	
	@Override
	public void resumeSong() {
		// Do nothing
	}
}
