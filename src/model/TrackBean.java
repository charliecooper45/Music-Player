package model;

import java.nio.file.Path;

/**
 * Represents a single track.
 * @author Charlie
 *
 */
public class TrackBean {
	private Path location;
	private String artist;
	private String title;
	private String album;
	private int minutes;
	private int seconds;
	
	public TrackBean() {}
	
	public TrackBean(Path location) {
		this.location = location;
	}
	
	public TrackBean(Path location, String artist, String title, String album) {
		this.location = location;
		this.artist = artist;
		this.title = title;
		this.album = album;
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

	/**
	 * @return the location
	 */
	public Path getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Path location) {
		this.location = location;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @param minutes the minutes to set
	 */
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * @param seconds the seconds to set
	 */
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
}
