package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sun.nio.cs.ext.ISCII91;

import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

//TODO NEXT: Issue with wrong song being played when double clicked
public class MP3Model extends Observable {
	// Holds the track database
	private volatile List<TrackBean> tracks;
	// Holds the artist database
	private volatile List<ArtistBean> artists;
	private MediaPlayer player;
	private State state;
	private State initialState;
	private State playingState;
	private State pausedState;
	// Holds the number of tracks processed so far out of a batch
	private volatile int numberProcessed;
	// Used to execute a single ProcessFileThread threads
	//TODO NEXT B: ensure this is shutdown at somepoint
	private ExecutorService executor;
	// Future that is used to manipulate the current ProcessFilesThread
	private Future<?> future;

	public MP3Model() {
		executor = Executors.newSingleThreadExecutor();
		tracks = new ArrayList<>();
		artists = new ArrayList<>();

		initialState = new InitialState(this);
		playingState = new PlayingState(this);
		pausedState = new PausedState(this);
		state = initialState;
	}

	public int getMP3Count(File... files) {
		int count = 0;
		numberProcessed = 0;

		for (File file : files) {
			// Check for directory here then walk it
			if (file.isDirectory()) {
				// Go through the files in the directory
				System.out.println("Directory found");

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

	//TODO NEXT B: Document
	public void startProcessThread(final File... files) {
		future = executor.submit(new ProcessFilesThread(files));
	}

	public void stopProcessThread() {
		future.cancel(true);
	}

	public TrackBean getTrack(int trackNo) {
		return tracks.get(trackNo);
	}

	public int getNumberOfTracks() {
		return tracks.size();
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
			System.out.println("Seeking! + percentage: " + percentage);
			Duration totalDuration = player.getTotalDuration();
			Duration time = (totalDuration.divide(100).multiply(percentage));
			System.out.println("Total duration: " + totalDuration);
			System.out.println("Skip to: " + time);
			player.seek(time);
		}
	}

	/**
	 * @return the numberProcessed
	 */
	public int getNumberProcessed() {
		return numberProcessed;
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
		if (player != null)
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

	private class ProcessFile extends SimpleFileVisitor<Path> {
		private int count = 0;

		public ProcessFile(int count) {
			this.count = count;
		}

		@Override
		public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {
			System.out.println("Processing file:" + aFile);

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
				if (file.isDirectory()) {
					File[] filesInDirectory = file.listFiles();
					processFiles(filesInDirectory);
				} else {
					createTrack(file);
				}
			}
			setChanged();
			notifyObservers();
		}

		//TODO NEXT: use the track number and genre in the table (configurable columns?)
		private void createTrack(File file) {
			numberProcessed = numberProcessed + 1;
			Mp3File mp3File;
			String track, artist, title, album, year;
			int genre;
			try {
				mp3File = new Mp3File(file.getAbsolutePath());

				if (mp3File.hasId3v1Tag()) {
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
					//TODO NEXT B: Add correct behaviour - look up documentation to see if there is another way to read data
					System.err.println("Cannot read track data");
					return;
				}

				addTrackToDatabase(Paths.get(file.toURI()), artist, title, album, mp3File.getLengthInMilliseconds());
			} catch (UnsupportedTagException | InvalidDataException | IOException e) {
				// Thread interrupted and throws FNF exception
			}
		}

		private void addTrackToDatabase(Path path, String artist, String title, String album, long milliseconds) {
			TrackBean trackBean = new TrackBean(path, artist, title, album, new Duration(milliseconds));
			tracks.add(trackBean);

			ArtistBean trackArtist = null;

			// Check if the artist is already listed in the database
			for (ArtistBean bean : artists) {
				if (bean.getName().equals(artist)) {
					trackArtist = bean;
					break;
				}
			}

			// If the artist was not found, create a new artist
			if (trackArtist == null) {
				trackArtist = new ArtistBean(artist);
				artists.add(trackArtist);
			}

			trackArtist.addTrack(trackBean);
		}
	}
}
