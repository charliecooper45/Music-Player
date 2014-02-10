package model;

import java.io.Serializable;
import java.net.URI;

import javafx.util.Duration;

/**
 * Represents a single track.
 * @author Charlie
 */
public class TrackBean implements Serializable{
	private static final long serialVersionUID = 7863985297307469985L;
	private URI location;
	private int trackNumber;
	private String artist;
	private String title;
	private AlbumBean album;
	private TrackDuration trackDuration;
	private String genre;
	private String comments;
	private transient Duration duration;
	
	public TrackBean() {}
	
	public TrackBean(URI location, String trackNumber, String artist, String title, AlbumBean album, Duration duration, String genre) {
		this.location = location;
		this.trackNumber = Integer.parseInt(trackNumber);
		this.artist = artist;
		this.title = title;
		this.album = album;
		this.duration = duration;
		this.trackDuration = new TrackDuration(duration.toMillis()); 
		this.genre = genre;
	}

	/**
	 * @return the trackNumber
	 */
	public int getTrackNumber() {
		return trackNumber;
	}
	

	/**
	 * @param trackNumber the trackNumber to set
	 */
	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
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
	public URI getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(URI location) {
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
	public AlbumBean getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(AlbumBean album) {
		this.album = album;
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return (int) duration.toMinutes();
	}
	
	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		if(duration == null) {
			duration = new Duration(trackDuration.milliseconds);
		}
		return (int) (duration.toSeconds() - (60 * getMinutes()));
	}
	
	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Class that allow the track duration to be serialized.
	 * @author Charlie
	 */
	private class TrackDuration implements Serializable {
		private static final long serialVersionUID = -7289995643505042778L;
		private double milliseconds;
		
		public TrackDuration(double milliseconds) {
			this.milliseconds = milliseconds;
		}
	}
}
