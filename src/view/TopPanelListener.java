package view;

import javafx.util.Duration;

public interface TopPanelListener {
	public Duration getCurrentTrackTime();
	public void trackFinished();
}
