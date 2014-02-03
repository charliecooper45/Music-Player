package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumBean implements Serializable {
	private static final long serialVersionUID = 2703233139456179492L;
	private String title;
	private List<TrackBean> tracks;
	
	public AlbumBean() {
		tracks = new ArrayList<>();
	}
	
	public AlbumBean(String title) {
		this();
		this.title = title;
	}
	
	public void addTrack(TrackBean track) {
		tracks.add(track);
	}
	
	/**
	 * @return the track number of the given TrackBean or -1
	 */
	public int getTrackNumber(TrackBean track) {
		return tracks.indexOf(track) + 1;
	}
	
	/**
	 * @param track in the album
	 * @return a copy of the collection holding the tracks from the current track onwards
	 */
	public List<TrackBean> getTracks(TrackBean track) {
		return tracks.subList(getTrackNumber(track) - 1, tracks.size());
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
		if(tracks.isEmpty()) 
			return true;
		
		return false;
	}
	
	/**
	 * @return the tracks in the album
	 */
	public List<TrackBean> getTracks() {
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
