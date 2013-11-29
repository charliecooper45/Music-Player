package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an artist
 * @author Charlie
 *
 */
public class ArtistBean {
	private String name;
	// The albums the artist has
	private List<AlbumBean> albums;
	
	public ArtistBean() {
		albums = new ArrayList<>();
	}

	public ArtistBean(String name) {
		this();
		this.name = name;
	}
	
	public void addTrack(TrackBean track) {
		AlbumBean album = null;
		
		// Check if the track is part of a new album
		for(AlbumBean bean : albums) {
			if(bean.getTitle().equals(track.getAlbum())) {
				album = bean;
				System.out.println("Album exists");
				break;
			}
		}

		if(album == null) {
			// Create a new album
			album = new AlbumBean();
		}
	
		// album.addTrack(track);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
