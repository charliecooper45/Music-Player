package model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MP3Model extends Observable {
	// Holds the location of the saved database
	private final static Path SAVED_DATABASE_FOLDER = Paths.get(System.getProperty("user.dir") + "/MediaPlayerData");
	private final static Path SAVED_DATABASE_FILE = Paths.get(SAVED_DATABASE_FOLDER + "/data.dt");
	// Holds the preferences information
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	// Holds the class that manages the Lastfm functionality
	private LastFm lastFm;
	private boolean lastFmIsActive = false;
	// Holds the current album and playlist
	private List<TrackBean> playlist;
	private int currentTrackNumber;
	// Holds the artist database
	private volatile List<ArtistBean> artists;
	private MediaPlayer player;
	private boolean playerMuted = false;
	private boolean shuffle = false;
	private boolean loop = false;
	private double volume = 0.5;
	// State pattern
	private State state;
	private State initialState;
	private State playingState;
	private State pausedState;
	// Holds the number of tracks processed so far out of a batch
	private volatile int numberProcessed;
	// Used to execute a single ProcessFileThread threads
	private ExecutorService executor;
	// Future that is used to manipulate the current ProcessFilesThread
	private Future<?> future;

	public MP3Model() {
		executor = Executors.newSingleThreadExecutor();
		artists = new ArrayList<>();
		playlist = new LinkedList<>();
		artists.add(new AllArtistsBean());

		initialState = new InitialState(this);
		playingState = new PlayingState(this);
		pausedState = new PausedState(this);
		state = initialState;
		
		if(prefs.getBoolean("lastfm", false)) {
			String username = prefs.get("username", null);
			String password = prefs.get("password", null);
			System.out.println("Loaded: " + username + " " + password);
			lastFm = new LastFm(username, password);
			lastFm.connect();
		}
	}

	/**
	 * @return the songs remaining to be played in the current album or playlist
	 */
	public List<TrackBean> getPlaylist() {
		return playlist.subList(currentTrackNumber, playlist.size());
	}

	public int getMP3Count(File... files) {
		int count = 0;
		numberProcessed = 0;

		for (File file : files) {
			// Check for directory here then walk it
			if (file.isDirectory()) {
				// Go through the files in the directory
				ProcessFile fileProcessor = new ProcessFile(count);

				try {
					Files.walkFileTree(file.toPath(), fileProcessor);
				} catch (IOException e) {
					e.printStackTrace();
				}

				count = fileProcessor.count;
			} else {
				if (checkIfMP3File(file)) {
					count++;
				}
			}
		}

		return count;
	}

	private boolean checkIfMP3File(File file) {
		String path = file.getAbsolutePath();
		int dot = path.lastIndexOf(".");
		String extension = path.substring(dot + 1);

		if (extension.equalsIgnoreCase("mp3")) {
			return true;
		}
		return false;
	}

	public List<ArtistBean> getArtists() {
		List<ArtistBean> artistStrings = new ArrayList<>();

		for (ArtistBean artist : artists) {
			artistStrings.add(artist);
		}

		return Collections.unmodifiableList(artistStrings);
	}

	public Duration getCurrentTrackTime() {
		ReadOnlyProperty<Duration> d = player.currentTimeProperty();
		return d.getValue();
	}

	public void setTrackPercentagePlayed(int percentage) {
		if (player != null) {
			Duration totalDuration = player.getTotalDuration();
			Duration time = (totalDuration.divide(100).multiply(percentage));
			player.seek(time);
		}
	}

	/** 
	 * @return all the albums in the database
	 */
	public List<AlbumBean> getAllAlbums() {
		List<AlbumBean> allAlbums = new ArrayList<>();

		for (ArtistBean artist : artists) {
			allAlbums.addAll(artist.getAlbums());
		}

		return allAlbums;
	}

	/**
	 * @param currentAlbum the album that is selected
	 */
	public void setAlbum(AlbumBean currentAlbum, int currentTrackNumber) {
		this.currentTrackNumber = currentTrackNumber;

		playlist = new ArrayList<>(currentAlbum.getTracks());

		if (shuffle) {
			shuffle(true);
		}
	}

	/** 
	 * Starts playing the current playlist from the first track
	 */
	public void playPlaylist() {
		playSong(playlist.get(currentTrackNumber));
	}

	public void addTracksToPlaylist(List<TrackBean> tracks) {
		playlist.addAll(tracks);

		if (shuffle) {
			shuffle(false);
		}
	}

	public void updateTrackDetails(TrackBean track, int trackNumber, String title, String artist, String album, String genre, String comments) {
		track.setTrackNumber(trackNumber);
		track.setTitle(title);
		track.setGenre(genre);
		track.setComments(comments);

		if (!track.getArtist().equals(artist)) {
			// The artist of the track has changed
			changeArtistOfTrack(track, artist);
			AllArtistsBean all = (AllArtistsBean) artists.get(0);
			all.setNumberOfArtists(artists.size() - 1);
		}
		if (!track.getAlbum().getTitle().equals(album)) {
			// The album of the track has changed
			changeAlbumOfTrack(track, album);
		}
	}

	private void changeArtistOfTrack(TrackBean track, String newArtist) {
		boolean trackAdded = false;

		// Get the ArtistBean representing the old artist and remove the track
		for (ArtistBean artist : artists) {
			if (artist.containsTrack(track)) {
				boolean artistEmpty = artist.removeTrack(track);
				if (artistEmpty)
					artists.remove(artist);
				break;
			}
		}

		for (ArtistBean artist : artists) {
			if (artist.getName().equals(newArtist)) {
				// The artist already exists so add the track
				artist.addTrack(track);
				trackAdded = true;
				break;
			}
		}

		if (!trackAdded) {
			// The artist does not exist so create them 
			ArtistBean artist = new ArtistBean(newArtist);
			artist.addTrack(track);
			artists.add(artist);
		}
	}

	private void changeAlbumOfTrack(TrackBean track, String newAlbum) {
		for (ArtistBean artist : artists) {
			if (artist.getName().equals(track.getArtist())) {
				AlbumBean album = artist.getAlbum(newAlbum);

				List<TrackBean> trackToDelete = new ArrayList<>();
				trackToDelete.add(track);

				boolean albumEmpty = track.getAlbum().deleteTracks(trackToDelete);
				if (albumEmpty)
					artist.removeAlbum(track.getAlbum());

				if (album != null) {
					album.addTrack(track);
				} else {
					artist.addTrackToAlbum(track, new AlbumBean(newAlbum));
				}
				break;
			}
		}
	}

	private void playSong(TrackBean track) {
		state.playSong(track);
		player.setMute(playerMuted);

		setChanged();
		notifyObservers(track);
	}

	/**
	 * Checks if there is another song in the current playlist, if there is then plays it
	 */
	public void playNextSong() {
		TrackBean newTrack = null;

		if (state != initialState) {
			if (currentTrackNumber < playlist.size() - 1 || loop) {
				currentTrackNumber++;

				if (player != null)
					player.stop();

				if (loop) {
					// If looping add the song we just played to the playlist
					playlist.add(playlist.get(currentTrackNumber - 1));

					setChanged();
					notifyObservers(playlist);
				}

				newTrack = playlist.get(currentTrackNumber);
				playSong(newTrack);
			} else {
				System.out.println("The album is finished");
				state.stopSong();

				setChanged();
				notifyObservers(null);
			}
		}
	}

	/**
	 * Checks if there is a previous song in the playlist, if there is then plays it
	 */
	public void playPreviousSong() {
		if (state != initialState) {
			currentTrackNumber--;

			if (currentTrackNumber < 0) {
				currentTrackNumber = 0;
			}

			if (player != null)
				player.stop();

			TrackBean newTrack = playlist.get(currentTrackNumber);

			playSong(newTrack);
		}
	}

	public void play() {
		if (state == pausedState) {
			resumeSong();
		} else {
			if (!playlist.isEmpty()) {
				playPlaylist();

				setChanged();
				notifyObservers(playlist.get(currentTrackNumber));
			}
		}
	}

	private void shuffle(boolean firstTrackSelected) {
		if (!playlist.isEmpty()) {
			if (firstTrackSelected) {
				TrackBean firstTrack = playlist.remove(currentTrackNumber);
				Collections.shuffle(playlist);
				playlist.add(currentTrackNumber, firstTrack);
			} else {
				Collections.shuffle(playlist);
			}
		}
	}

	private void clearPlaylist() {
		// Called when a playlist is finished
		playlist.clear();
		currentTrackNumber = 0;
	}

	public void mute() {
		playerMuted = !playerMuted;

		if (player != null) {
			player.setMute(playerMuted);
		}
	}

	public void pauseSong() {
		state.pauseSong();
	}

	private void resumeSong() {
		state.resumeSong();
	}

	public void stopSong(boolean playAnotherSong) {
		if (player != null) {
			state.stopSong();
		}

		// Check to see if the user is ceasing to play any songs
		if (!playAnotherSong) {
			state = initialState;
		}

		clearPlaylist();
	}

	public void deleteTracks(List<TrackBean> tracks) {
		String artistName = tracks.get(0).getArtist();
		AlbumBean album = tracks.get(0).getAlbum();

		boolean albumEmpty = album.deleteTracks(tracks);

		if (albumEmpty) {
			for (ArtistBean artist : artists) {
				if (artist.getName().equals(artistName)) {
					boolean artistEmpty = artist.removeAlbum(album);
					if (artistEmpty) {
						// The artist has no more albums so remove it from the database
						artists.remove(artist);
					}
					break;
				}
			}
		}

		// Check if one of the tracks being deleted is the current track in the playlist
		if (playlist.size() > 0 && tracks.contains(playlist.get(currentTrackNumber))) {
			stopSong(false);

			setChanged();
			notifyObservers(null);
		}
	}

	/**
	 * Runs all the tasks necessary before the program shutsdown
	 */
	public boolean closePlayer() {
		if (player != null)
			player.stop();

		try {
			saveDatabase();
		} catch (IOException e) {
			return false;
		}

		// Shutdow the executor
		executor.shutdownNow();
		return true;
	}

	private void saveDatabase() throws IOException {
		if (!Files.exists(SAVED_DATABASE_FOLDER)) {
			// There is no previously saved data so create the folder
			Files.createDirectory(SAVED_DATABASE_FOLDER);
		}

		ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(SAVED_DATABASE_FILE));
		os.writeObject(artists);
		os.close();
	}

	/**
	 * Runs all the tasks necessary at the start of the program
	 */
	public boolean openPlayer() {
		try {
			loadDatabase();
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void loadDatabase() throws IOException, ClassNotFoundException {
		// Add the menu option to select all the artists in the database
		if (Files.exists(SAVED_DATABASE_FILE)) {
			// Load the artists database
			ObjectInputStream is = new ObjectInputStream(Files.newInputStream(SAVED_DATABASE_FILE));
			artists = (List<ArtistBean>) is.readObject();
		}
	}

	/**
	 * @param on
	 * @param username
	 * @param password
	 * @param startup
	 * @return false if the username or password were incorrect
	 */
	public boolean changeLastFMState(boolean on, String username, String password) {
		lastFmIsActive = on;

		if (lastFmIsActive) {
			if(lastFm != null) {
				String currentUsername = lastFm.getUsername();
				if(currentUsername.equals(username)) {
					return true;
				}
			}
			lastFm = new LastFm(username, password);
			boolean connected = lastFm.connect();
			if (connected) {
				prefs.putBoolean("lastfm", on);
				prefs.put("username", username);
				prefs.put("password", password);
			}
			return connected;
		} else {
			lastFm = null;
			prefs.putBoolean("lastfm", false);
			prefs.remove("username");
			prefs.remove("password");
			return true;
		}
	}

	public boolean getLastFMState() {
		lastFmIsActive = prefs.getBoolean("lastfm", false);
		return lastFmIsActive;
	}

	public String getLastFmUsername() {
		return lastFm.getUsername();
	}

	public String getLastFmPassword() {
		return lastFm.getPassword();
	}

	public boolean scrobbleTrack(TrackBean currentTrack) {
		if(lastFmIsActive) {
			return lastFm.scrobbleTrack(currentTrack);
		}
		return true;
	}
	
	public void setVolume(double value) {
		volume = value;

		if (player != null) {
			player.setVolume(volume);
		}
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
		player.setVolume(volume);
		this.player = player;
	}

	public boolean isShuffled() {
		return shuffle;
	}

	public boolean isLooped() {
		return loop;
	}

	public void setShuffled(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public void setLooped(boolean loop) {
		this.loop = loop;
	}

	public void startProcessFilesThread(final File... files) {
		future = executor.submit(new ProcessFilesThread(files));
	}

	public void stopProcessFilesThread() {
		future.cancel(true);
	}

	/**
	 * @return the numberProcessed
	 */
	public int getNumberProcessed() {
		return numberProcessed;
	}

	private class ProcessFile extends SimpleFileVisitor<Path> {
		private int count = 0;

		public ProcessFile(int count) {
			this.count = count;
		}

		@Override
		public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {
			File file = aFile.toFile();

			if (file.isDirectory()) {
				ProcessFile subProcessFile = new ProcessFile(this.count);
				this.count = subProcessFile.count;
			} else {
				if (checkIfMP3File(file))
					this.count++;
			}
			return FileVisitResult.CONTINUE;
		}
	};

	private class ProcessFilesThread extends Thread {
		private File[] files;

		public ProcessFilesThread(File... files2) {
			this.files = files2;
		}

		@Override
		public void run() {
			processFiles(files);
		}

		private void processFiles(File[] files) {
			for (File file : files) {
				if (!Thread.currentThread().isInterrupted()) {
					if (file.isDirectory()) {
						File[] filesInDirectory = file.listFiles();
						processFiles(filesInDirectory);
					} else {
						createTrack(file);
					}
				} else {
					break;
				}
			}
			setChanged();
			notifyObservers(artists);
		}

		private void createTrack(File file) {
			numberProcessed = numberProcessed + 1;
			Mp3File mp3File;
			String trackNumber, artist, title, album, genre;
			try {
				mp3File = new Mp3File(file.getAbsolutePath());

				if (mp3File.hasId3v1Tag()) {
					ID3v1 id3v1Tag = mp3File.getId3v1Tag();
					trackNumber = id3v1Tag.getTrack();
					artist = id3v1Tag.getArtist();
					title = id3v1Tag.getTitle();
					album = id3v1Tag.getAlbum();
					genre = id3v1Tag.getGenreDescription();
				} else if (mp3File.hasId3v2Tag()) {
					ID3v1 id3v2Tag = mp3File.getId3v2Tag();
					trackNumber = id3v2Tag.getTrack();
					artist = id3v2Tag.getArtist();
					title = id3v2Tag.getTitle();
					album = id3v2Tag.getAlbum();
					genre = id3v2Tag.getGenreDescription();
				} else {
					System.err.println("Cannot read track data");
					return;
				}

				addTrackToDatabase(Paths.get(file.toURI()), trackNumber, artist, title, album, mp3File.getLengthInMilliseconds(), genre);
			} catch (UnsupportedTagException | InvalidDataException | IOException e) {
				e.printStackTrace();
			}
		}

		private void addTrackToDatabase(Path path, String trackNumber, String artist, String title, String album, long milliseconds, String genre) {
			AlbumBean trackAlbum = null;
			TrackBean trackBean = null;
			ArtistBean trackArtist = null;

			// Check if the artist is already listed in the database
			for (ArtistBean artistBean : artists) {
				if (artistBean.getName().equals(artist)) {
					// Check if the artist has this album
					trackAlbum = artistBean.getAlbum(album);

					if (trackAlbum == null) {
						// No album was found for the artist so create a new album
						trackAlbum = new AlbumBean(album);
					}

					trackArtist = artistBean;
					break;
				}
			}

			// If the artist was not found, create a new artist
			if (trackArtist == null) {
				trackArtist = new ArtistBean(artist);
				artists.add(trackArtist);

				// update the number of artists
				AllArtistsBean all = (AllArtistsBean) artists.get(0);
				all.setNumberOfArtists(artists.size() - 1);

				// Create a new album for the artists
				trackAlbum = new AlbumBean(album);
			}

			trackBean = new TrackBean(path.toUri(), trackNumber, artist, title, trackAlbum, new Duration(milliseconds), genre);
			trackArtist.addTrackToAlbum(trackBean, trackAlbum);
		}
	}

}
