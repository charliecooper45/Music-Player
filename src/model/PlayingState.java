package model;


public class PlayingState implements State {
	private MP3Model model;
	
	public PlayingState(MP3Model model) {
		this.model = model;
	}
	
	@Override
	public void playSong(TrackBean track) {
		System.out.println("Change the song being played here");
	}

	@Override
	public void pauseSong() {
		model.getPlayer().pause();
		model.setState(model.getPausedState());
	}
	
	@Override
	public void resumeSong() {
		System.out.println("Cannot resume song");
	}

}
