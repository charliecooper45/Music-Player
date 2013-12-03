package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumBean {
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
