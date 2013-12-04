package model;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MP3Model extends Observable{
	// Holds the track database
	private List<TrackBean> tracks;
	// Holds the artist database
	private List<ArtistBean> artists;
	//TODO NEXT: Implement the observer model, the controller should observe the model and watch out for table update orders
	private MediaPlayer player;
	private State state;
	private State initialState;
	private State playingState;
	private State pausedState;

	public MP3Model() {
		tracks = new ArrayList<>();
		artists = new ArrayList<>();
		
		initialState = new InitialState(this);
		playingState = new PlayingState(this);
		pausedState = new PausedState(this);
		state = initialState;
	}
	
	//TODO NEXT B: Document
	public void processFiles(File... files) {
		for(File file : files) {
			if(file.isDirectory()) {
				File[] filesInDirectory = file.listFiles();
				//TODO NEXT: Check this recursion
				processFiles(filesInDirectory);
			} else {
				createTrack(file);
			}
		}
		setChanged();
		notifyObservers();
	}
	
	private void createTrack(File file) {
		Mp3File mp3File;
		String track, artist, title, album, year;
		int genre;
		try {
			mp3File = new Mp3File(file.getAbsolutePath());
			
	        if (mp3File.hasId3v1Tag())
	        {
	            ID3v1 id3v1Tag = mp3File.getId3v1Tag();
	            track = id3v1Tag.getTrack();
	            artist = id3v1Tag.getArtist();
	            title = id3v1Tag.getTitle();
	            album = id3v1Tag.getAlbum();
	            year = id3v1Tag.getYear();
	            genre = id3v1Tag.getGenre();
	        } else if (mp3File.hasId3v2Tag()) {
	        	ID3v1 id3v2Tag = mp3File.getId3v2Tag();
	            track = id3v2Tag.getTrack();
	            artist = id3v2Tag.getArtist();
	            title = id3v2Tag.getTitle();
	            album = id3v2Tag.getAlbum();
	            year = id3v2Tag.getYear();
	            genre = id3v2Tag.getGenre();
	        } else {
	        	//TODO NEXT B: Add correct behaviour
	        	return;
	        }
	        
	        addTrackToDatabase(Paths.get(file.toURI()), artist, title, album, mp3File.getLengthInMilliseconds());

		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addTrackToDatabase(Path path, String artist, String title, String album, long milliseconds) {
		TrackBean trackBean = new TrackBean(path, artist, title, album, new Duration(milliseconds));
		tracks.add(trackBean);
		
		ArtistBean trackArtist = null;
		
		// Check if the artist is already listed in the database
		for(ArtistBean bean : artists) {
			if(bean.getName().equals(artist)) {
    			trackArtist = bean;
    			break;
			}
		}
		
		// If the artist was not found, create a new artist
		if(trackArtist == null) {
        	trackArtist = new ArtistBean(artist);
        	artists.add(trackArtist);
		}
        
        trackArtist.addTrack(trackBean);
	}
	
	public TrackBean getTrack(int trackNo) {
		return tracks.get(trackNo);
	}
	
	public int getNumberOfTracks() {
		return tracks.size();
	}
	
	public List<ArtistBean> getArtists() {
		List<ArtistBean> artistStrings = new ArrayList<>();
		
		for(ArtistBean artist : artists) {
			artistStrings.add(artist);
		}
		
		return Collections.unmodifiableList(artistStrings);
	}

	public Duration getCurrentTrackTime() {
		ReadOnlyProperty<Duration> d = player.currentTimeProperty();
		return d.getValue();
	}
	
	//TODO NEXT B: Document
	public void playSong(TrackBean track) {
		state.playSong(track);
		System.out.println("Track Length: " + player.getTotalDuration());
	}

	public void pauseSong() {
		state.pauseSong();
	}
	
	public void resumeSong() {
		state.resumeSong();
	}
	
	public void stopSong() {
		if(player != null) 
			player.stop();
		state = initialState;
	}
	
	/**
	 * @return the pausedState
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @return the pausedState
	 */
	public State getPausedState() {
		return pausedState;
	}
	
	/**
	 * @return the playingState
	 */
	public State getPlayingState() {
		return playingState;
	}
	
	/**
	 * @return the initialState
	 */
	public State getInitialState() {
		return initialState;
	}

	/**
	 * @param playingState the playingState to set
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * @return the player
	 */
	public MediaPlayer getPlayer() {
		return player;
	}
	
	/**
	 * @param player the player to set
	 */
	public void setPlayer(MediaPlayer player) {
		this.player = player;
	}
}
