package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import model.AlbumBean;
import model.ArtistBean;
import model.TrackBean;

/**
 * A JPanel that displays the tracks in the user`s library.
 * @author Charlie
 */
@SuppressWarnings("serial")
public class MiddlePanel extends JPanel {
	private JList<ArtistBean> artistsList;
	private DefaultListModel<ArtistBean> artistsListModel;
	private JList<AlbumBean> albumsList;
	private DefaultListModel<AlbumBean> albumsListModel;
	private JTable tracksTable;
	private TracksTableModel tableModel;
	// Holds the current songs to display in the table at the bottom of the panel
	private List<TrackBean> tableTracks;
	private PlaylistPanel playlistPanel;
	// Popup menus
	private JPopupMenu tracksTablePopup;

	public MiddlePanel() {
		tableTracks = new ArrayList<>();
		setLayout(new BorderLayout());
		init();
	}

	private void init() {
		setupListsPanel();

		// Setup the table to hold the track information
		tableModel = new TracksTableModel();
		tracksTable = new JTable(tableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < tracksTable.getColumnCount(); i++) {
			tracksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		tracksTable.getColumnModel().getColumn(0).setMinWidth(150);
		tracksTable.getColumnModel().getColumn(0).setMaxWidth(150);

		add(new JScrollPane(tracksTable), BorderLayout.CENTER);
		tableModel.fireTableDataChanged();

		playlistPanel = new PlaylistPanel();
		add(playlistPanel, BorderLayout.WEST);

		tracksTablePopup = new JPopupMenu();
		JMenuItem addTrack = new JMenuItem("Add to playlist");
		addTrack.setName("add track");
		JMenuItem addAlbum = new JMenuItem("Add album to playlist");
		addAlbum.setName("add album");
		JMenuItem clearPlaylist = new JMenuItem("Clear playlist");
		clearPlaylist.setName("clear playlist");
		JMenuItem removeTrack = new JMenuItem("Remove from library");
		removeTrack.setName("remove track");
		JMenuItem getTrackInfo = new JMenuItem("Get info");
		getTrackInfo.setName("get info");
		tracksTablePopup.add(addTrack);
		tracksTablePopup.add(addAlbum);
		tracksTablePopup.add(clearPlaylist);
		tracksTablePopup.addSeparator();
		tracksTablePopup.add(removeTrack);
		tracksTablePopup.addSeparator();
		tracksTablePopup.add(getTrackInfo);
		tracksTablePopup.setPreferredSize(new Dimension(200, 100));
	}

	private void setupListsPanel() {
		artistsListModel = new DefaultListModel<>();
		artistsList = new JList<>(artistsListModel);
		artistsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		albumsListModel = new DefaultListModel<>();
		albumsList = new JList<>(albumsListModel);
		albumsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, artistsList, albumsList);
		pane.setResizeWeight(0.5);
		pane.setPreferredSize(new Dimension(0, 200));
		add(pane, BorderLayout.NORTH);
	}

	/**
	 * Adds an ActionListener from the view to the appropriate components
	 * @param listener
	 */
	public void addMouseListener(MouseListener listener) {
		// Add listener to the table
		tracksTable.addMouseListener(listener);
	}

	/**
	 * @param listListener the listListener to set
	 */
	public void addListListener(ListSelectionListener listener) {
		artistsList.addListSelectionListener(listener);
		albumsList.addListSelectionListener(listener);
	}

	public void addActionListener(ActionListener listener) {
		for (Component comp : tracksTablePopup.getComponents()) {
			if (comp instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) comp;
				menuItem.addActionListener(listener);
			}
		}
	}

	public void addKeyListener(KeyListener listener) {
		tracksTable.addKeyListener(listener);
	}

	public void updateArtists(List<ArtistBean> artists) {
		ArtistBean selectedArtist = artistsList.getSelectedValue();

		tableModel.fireTableDataChanged();

		// Update the artists list
		artistsListModel.removeAllElements();
		for (ArtistBean artist : artists) {
			artistsListModel.addElement(artist);
		}

		if (selectedArtist != null && artists.contains(selectedArtist)) {
			artistsList.setSelectedValue(selectedArtist, true);
		} else {
			artistsList.setSelectedIndex(0);
		}
	}

	public void changeDisplayedAlbums(List<AlbumBean> albums) {
		AlbumBean selectedAlbum = albumsList.getSelectedValue();
		
		albumsListModel.clear();

		for (AlbumBean album : albums) {
			albumsListModel.addElement(album);
		}

		if(albumsListModel.contains(selectedAlbum)) {
			albumsList.setSelectedValue(selectedAlbum, true);
		} else {
			albumsList.setSelectedIndex(0);
		}
	}

	public void setDisplayedPlaylist(List<TrackBean> playlist) {
		playlistPanel.setDisplayedPlaylist(playlist);
	}

	public void updatePlaylist(TrackBean track) {
		playlistPanel.updatePlaylist(track);
	}

	/**
	 * @param tableTracks the tableTracks to set
	 */
	public void setTableTracks(List<TrackBean> tableTracks) {
		this.tableTracks = tableTracks;
		tableModel.fireTableDataChanged();
	}

	public ArtistBean getDisplayedArtist() {
		return artistsList.getSelectedValue();
	}

	public AlbumBean getSelectedAlbum() {
		return albumsList.getSelectedValue();
	}

	public int getTrackNumber() {
		return tracksTable.getSelectedRow();
	}

	public List<TrackBean> getSelectedTracks() {
		int[] selectedRows = tracksTable.getSelectedRows();
		List<TrackBean> selectedTracks = new ArrayList<>();

		for (int i : selectedRows) {
			selectedTracks.add(tableTracks.get(i));
		}

		return selectedTracks;
	}

	public boolean showPopupMenu(Object component, int x, int y) {
		if (component == tracksTable) {
			tracksTablePopup.show(tracksTable, x, y);
		} else {
			// This is a confirmation popup about deleting a track
			int confirm = JOptionPane.showConfirmDialog((JFrame) component, "Are you sure you wish to delete the selected tracks?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION)
				return true;
		}
		return false;
	}

	private class TracksTableModel extends AbstractTableModel {
		private String[] colNames = { "Track Number", "Artist", "Title", "Length", "Genre" };

		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public int getRowCount() {
			return tableTracks.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TrackBean track = tableTracks.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return track.getTrackNumber();
			case 1:
				return track.getArtist();
			case 2:
				return track.getTitle();
			case 3:
				int seconds = track.getSeconds();
				if (seconds < 10) {
					String secondsString = "0" + seconds;
					return track.getMinutes() + ":" + secondsString;
				}
				return track.getMinutes() + ":" + seconds;
			case 4:
				return track.getGenre();
			}
			return null;
		}
	}

	/**
	 * Class that manages the panel holding the playlist of tracks.
	 * @author Charlie
	 */
	private class PlaylistPanel extends JPanel {
		private JTable playlistTable;
		private List<TrackBean> displayedTracks;
		private AbstractTableModel playlistTableModel;

		public PlaylistPanel() {
			setPreferredSize(new Dimension(200, 300));
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder());

			init();
		}

		private void init() {
			displayedTracks = new ArrayList<TrackBean>();
			JLabel upNextLabel = new JLabel("Playlist:");
			add(upNextLabel, BorderLayout.NORTH);

			playlistTable = new JTable() {
				@Override
				public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
					Component c = super.prepareRenderer(renderer, row, column);

					int modelRow = convertRowIndexToModel(row);

					if (modelRow == 0) {
						c.setForeground(Color.RED);
					} else {
						c.setForeground(Color.BLACK);
					}
					return c;
				}
			};
			playlistTableModel = new AbstractTableModel() {
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					TrackBean track = displayedTracks.get(rowIndex);
					return (rowIndex + 1) + ": " + track.getTitle();
				}

				@Override
				public int getRowCount() {
					return displayedTracks.size();
				}

				@Override
				public int getColumnCount() {
					return 1;
				}
			};
			playlistTable.setModel(playlistTableModel);
			add(playlistTable, BorderLayout.CENTER);
		}

		/**
		 * @param playlist the playlist to display
		 */
		public void setDisplayedPlaylist(List<TrackBean> playlist) {
			displayedTracks = new ArrayList<>(playlist);

			playlistTableModel.fireTableDataChanged();
		}

		public void updatePlaylist(TrackBean track) {
			if (track == null) {
				// The playlist is finished so clear it
				displayedTracks.clear();
			} else if (!displayedTracks.isEmpty()) {
				// Checks which way we are moving through the playlist and adjusts what the table displays as necessary
				if (displayedTracks.size() != 1) {
					if (displayedTracks.get(1).equals(track)) {
						displayedTracks.remove(0);
					} else if (!displayedTracks.get(0).equals(track)) {
						displayedTracks.add(0, track);
					}
				}
			}
			playlistTableModel.fireTableDataChanged();
		}
	}
}
