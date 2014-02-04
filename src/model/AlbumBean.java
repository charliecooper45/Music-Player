package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AlbumBean implements Serializable {
	private static final long serialVersionUID = 2703233139456179492L;
	private String title;
	private List<TrackBean> tracks;
	private boolean sorted = true;

	public AlbumBean() {
		tracks = new LinkedList<>();
	}

	public AlbumBean(String title) {
		this();
		this.title = title;
	}

	public void addTrack(TrackBean track) {
		tracks.add(track);
		sorted = false;
	}
	
	private void sortTracks() {
		Collections.sort(tracks, new Comparator<TrackBean>() {
			@Override
			public int compare(TrackBean track1, TrackBean track2) {
				return track1.getTrackNumber() - track2.getTrackNumber();
			}
		});
	}

	/**
	 * @param trackNumber
	 * @return The TrackBean associated with the trackNumber for this album
	 */
	public TrackBean getTrack(int trackNumber) {
		return tracks.get(trackNumber);
	}

	public boolean deleteTracks(List<TrackBean> tracksToRemove) {
		//TODO NEXT: Tracks should potentially have their own track number stored, this could be read in?
		tracks.removeAll(tracksToRemove);

		// Check if all tracks have been deleted from the album
		if (tracks.isEmpty())
			return true;

		return false;
	}

	/**
	 * @return the tracks in the album
	 */
	public List<TrackBean> getTracks() {
		if(!sorted) {
			sortTracks();
			sorted = true;
		}
		return Collections.unmodifiableList(tracks);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
}
