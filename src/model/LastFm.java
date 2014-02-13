package model;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;

//TODO NEXT: Implement this class
//TODO NEXT: Make sure lastfm connects when we start the program
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
