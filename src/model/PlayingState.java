package model;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class PlayingState implements State {
	private MP3Model model;
	
	public PlayingState(MP3Model model) {
		this.model = model;
	}
	
	@Override
	public void playSong(TrackBean bean) {
		Path path = Paths.get(bean.getLocation());
		MediaPlayer player = new MediaPlayer(new Media(path.toUri().toString()));
		model.setPlayer(player);
		player.play();
	}

	@Override
	public void pauseSong() {
		model.getPlayer().pause();
		model.setState(model.getPausedState());
	}
	
	@Override
	public void resumeSong() {
		System.err.println("Cannot resume song");
	}

	@Override
	public void stopSong() {
		model.getPlayer().stop();
		model.setState(model.getInitialState());
		model.stopSong(false);
	}
}
