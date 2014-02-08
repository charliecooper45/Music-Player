package view;

import model.TrackBean;

/**
 * Fired when a track is edited in the InfoDialog panel
 * @author Charlie
 */
public interface TrackEditedListener {
	public void trackChanged(TrackBean track, int trackNumber, String title, String artist, String album, String genre, String comments);
}
