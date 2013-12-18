package model;

public class PausedState implements State{
	private MP3Model model;
	
	public PausedState(MP3Model model) {
		this.model = model;
	}
	
	@Override
	public void playSong(TrackBean track) {
		model.getPlayer().play();
		model.setState(model.getPlayingState());
	}

	@Override
	public void pauseSong() {
		// Do nothing
	}
	
	@Override
	public void resumeSong() {
		model.getPlayer().play();
		model.setState(model.getPlayingState());
	}

}
