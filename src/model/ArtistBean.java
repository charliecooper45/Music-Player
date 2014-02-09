package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

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
	
	public void addTrackToAlbum(TrackBean track, AlbumBean album) {
		// Check if this is a new album
		if(!albums.contains(album))
			albums.add(album);

		album.addTrack(track);
	}

	public boolean removeAlbum(AlbumBean album) {
		albums.remove(album);
		
		if(albums.isEmpty()) {
			return true;
		}
		return false;
	}

	public void addTrack(TrackBean track) {
		AlbumBean trackAlbum = null;
		track.setArtist(name);
		
		for(AlbumBean album : albums) {
			if(album.getTitle().equals(track.getAlbum().getTitle())) {
				album.addTrack(track);
				trackAlbum = album;
				break;
			}
		}
		
		if(trackAlbum == null) {
			trackAlbum = new AlbumBean(track.getAlbum().getTitle());
			trackAlbum.addTrack(track);
			albums.add(trackAlbum);
		}
	}
	
	/**
	 * @param track to remove 
	 * @return if the artist has no more albums remaining
	 */
	public boolean removeTrack(TrackBean track) {
		ListIterator<AlbumBean> iterator = albums.listIterator();
		
		while(iterator.hasNext()) {
			AlbumBean album = iterator.next();
			if(album.getTracks().contains(track)) {
				List<TrackBean> trackToRemove = new ArrayList<>();
				trackToRemove.add(track);
				boolean albumEmpty = album.deleteTracks(trackToRemove);
				if(albumEmpty)
					iterator.remove();
			}
		}
		
		//Check if the artist has any tracks remaining
		if(albums.isEmpty()) 
			return true;
		
		return false;
	}
	
	public boolean containsTrack(TrackBean track) {
		for(AlbumBean album : albums) {
			if(album.getTracks().contains(track)) {
				return true;
			}
		}
		return false;
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
