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
	// Holds the current album and playlist
	private List<TrackBean> playlist;
	private volatile AlbumBean currentAlbum;
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
	//TODO NEXT B: ensure the ExecutorService is shutdown at somepoint
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
	}

	/**
	 * @return the songs remaining to be played in the current album or playlist
	 */
	public List<TrackBean> getPlaylist() {
		return playlist.subList(currentTrackNumber, playlist.size());
	}

	public TrackBean getTrack(int trackNumber) {
		return currentAlbum.getTrack(trackNumber);
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
		this.currentAlbum = currentAlbum;
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

	//TODO NEXT B: Document
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

	public void shuffle(boolean firstTrackSelected) {
		//TODO NEXT: Implement code to shuffle the playlist - shuffling when playing an album is not working correctly ATM
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
		currentAlbum = null;
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

	public void resumeSong() {
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
		
		// Check if one of the tracks being deleted is the current track
		if (tracks.contains(playlist.get(currentTrackNumber))) {
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
		return true;
	}

	private void saveDatabase() throws IOException {
		//TODO NEXT B: Check if this is Windows, if so then save in my document or another location? This could be configurable. 
		//TODO NEXT B: Configure this in preferences API.

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

	//TODO NEXT: Multiple additions of albums not working properly

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
			String track, artist, title, album, year, genre;
			try {
				mp3File = new Mp3File(file.getAbsolutePath());

				if (mp3File.hasId3v1Tag()) {
					ID3v1 id3v1Tag = mp3File.getId3v1Tag();
					track = id3v1Tag.getTrack();
					artist = id3v1Tag.getArtist();
					title = id3v1Tag.getTitle();
					album = id3v1Tag.getAlbum();
					year = id3v1Tag.getYear();
					genre = id3v1Tag.getGenreDescription();
				} else if (mp3File.hasId3v2Tag()) {
					ID3v1 id3v2Tag = mp3File.getId3v2Tag();
					track = id3v2Tag.getTrack();
					artist = id3v2Tag.getArtist();
					title = id3v2Tag.getTitle();
					album = id3v2Tag.getAlbum();
					year = id3v2Tag.getYear();
					genre = id3v2Tag.getGenreDescription();
				} else {
					//TODO NEXT B: Add correct behaviour - look up documentation to see if there is another way to read data
					System.err.println("Cannot read track data");
					return;
				}

				addTrackToDatabase(Paths.get(file.toURI()), artist, title, album, mp3File.getLengthInMilliseconds(), genre);
			} catch (UnsupportedTagException | InvalidDataException | IOException e) {
				//TODO NEXT B: Produce error to show that the user has deleted a file while it is being processed (check for interrupt)
				e.printStackTrace();
			}
		}

		private void addTrackToDatabase(Path path, String artist, String title, String album, long milliseconds, String genre) {
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

			trackBean = new TrackBean(path.toUri(), artist, title, trackAlbum, new Duration(milliseconds), genre);
			trackArtist.addTrack(trackBean, trackAlbum);
		}
	}

}
