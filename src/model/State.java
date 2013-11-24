package model;

/**
 * State pattern
 * @author Charlie
 *
 */
public interface State {
	public void playSong(TrackBean track);
	
	public void pauseSong();
	
	public void resumeSong();
}
