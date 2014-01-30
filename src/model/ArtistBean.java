package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an artist
 * @author Charlie
 */
public class ArtistBean implements Serializable {
	private static final long serialVersionUID = -8571623797558533992L;
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
	
	/**
	 * @return a specified album or null if it does not exist
	 */
	public AlbumBean getAlbum(String name) {
		for(AlbumBean album : albums) {
			if(album.getTitle().equals(name)) {
				return album;
			}
		}
		return null;
	}
	
	public void addTrack(TrackBean track, AlbumBean album) {
		// Check if this is a new album
		if(!albums.contains(album))
			albums.add(album);

		album.addTrack(track);
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

	/**
	 * @return the albums
	 */
	public List<AlbumBean> getAlbums() {
		return Collections.unmodifiableList(albums);
	}

	@Override
	public String toString() {
		return name;
	}
}
