package model;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;
import de.umass.lastfm.scrobble.ScrobbleResult;

public class LastFm {
	private static final String API_KEY = "d6256d27feb88ff326f6f5b442468bc1";
	private static final String SECRET = "41685746b10fd47f841c17db28144d97";
	private String username;
	private String password;
	private Session session;
	
	public LastFm(String username, String password) {
		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
		
		this.username = username;
		this.password = password;
	}
	
	public boolean connect() {
		System.out.println("Connecting...");
		session = Authenticator.getMobileSession(username, password, LastFm.API_KEY, LastFm.SECRET);
		
		if(session == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private void updateLastFmNowPlaying(TrackBean track, int timeStamp) {
		int time = (int) track.getDuration().toSeconds();
		
		ScrobbleData data = new ScrobbleData(track.getArtist(), track.getTitle(), timeStamp, time, track.getAlbum().getTitle(), track.getArtist(), "", track.getTrackNumber(), "");
		Track.updateNowPlaying(data, session);
	}
	
	public boolean scrobbleTrack(TrackBean track) {
		//TODO NEXT: Check what happens if lastfm is turnedon during a track getting played, also check for double scrobbling bug
		String artist = track.getArtist();
		String title = track.getTitle();
		
		int duration = (int) track.getDuration().toSeconds();
		updateLastFmNowPlaying(track, (duration/2));
		
	    int now = (int) (System.currentTimeMillis() / 1000);
	    ScrobbleResult result2 = Track.scrobble(artist, title, now, session);
	    return (result2.isSuccessful() && !result2.isIgnored());
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
}
