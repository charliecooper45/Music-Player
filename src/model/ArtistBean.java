package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an artist
 * @author Charlie
 *
 */
public class ArtistBean {
	// TODO NEXT B: In the Artists/Albums list create an All option
	
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
				break;
			}
		}

		if(album == null) {
			// Create a new album
			album = new AlbumBean(track.getAlbum());
			albums.add(album);
		}
	
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
