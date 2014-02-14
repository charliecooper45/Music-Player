package view;

import javafx.util.Duration;
import model.TrackBean;

public interface TopPanelListener {
	public Duration getCurrentTrackTime();
	public void trackFinished();
	public void scrobbleTrack(TrackBean currentTrack);
}
