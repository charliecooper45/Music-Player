package model;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.MediaPlayer;

public class MP3Model {
	// Holds the track database
	private List<TrackBean> tracks;
	
	private MediaPlayer player;
	private State state;
	private State initialState;
	private State playingState;
	private State pausedState;

	public MP3Model() {
		tracks = new ArrayList<>();
		populateTracks();
		
		initialState = new InitialState(this);
		playingState = new PlayingState(this);
		pausedState = new PausedState(this);
		state = initialState;
	}
	
	private void populateTracks() {
		TrackBean track = new TrackBean(Paths.get("./one.mp3"));
		track.setArtist("30 Seconds to Mars");
		track.setTitle("One");
		track.setMinutes(2);
		track.setSeconds(30);
		tracks.add(track);
		//TODONEXT: Sort out the implementation of this, need to pass the bean as an argument to the play method when selected in the table
	}
	
	public TrackBean getTrack(int trackNo) {
		return tracks.get(trackNo);
	}

	//TODO NEXT B: Document
	public void playSong(TrackBean track) {
		state.playSong(track);
	}

	//TODO NEXT B: Document
	public void pauseSong() {
		state.pauseSong();
	}
	
	//TODO NEXT B: Document
	public void resumeSong() {
		state.resumeSong();
	}
	
	//TODO NEXT B: Document
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
