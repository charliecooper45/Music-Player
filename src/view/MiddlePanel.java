package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import model.AlbumBean;
import model.ArtistBean;
import model.TrackBean;

/**
 * A JPanel that displays the tracks in the user`s library.
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class MiddlePanel extends JPanel{
	
	private JList<ArtistBean> artistsList;
	private DefaultListModel<ArtistBean> artistsListModel;
	private JList<AlbumBean> albumsList;
	private DefaultListModel<AlbumBean> albumsListModel;
	private JTable tracksTable;
	private TracksTableModel tableModel;
	// Holds the current songs to display in the table at the bottom of the panel
	private List<TrackBean> tableTracks; 
	
	public MiddlePanel() {
		tableTracks = new ArrayList<>();
		setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		setupListsPanel();
		
		tableModel = new TracksTableModel();
		tracksTable = new JTable(tableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tracksTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		tracksTable.getColumnModel().getColumn(0).setMinWidth(150);
		tracksTable.getColumnModel().getColumn(0).setMaxWidth(150);
		
		add(new JScrollPane(tracksTable), BorderLayout.CENTER);
		tableModel.fireTableDataChanged();
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

	public void updateArtists(List<ArtistBean> artists) {
		ArtistBean selectedArtist = artistsList.getSelectedValue();
		
		tableModel.fireTableDataChanged();
		
		// Update the artists list
		artistsListModel.removeAllElements();
		for(ArtistBean artist : artists) {
			artistsListModel.addElement(artist);
		}
		
		if(selectedArtist != null) {
			artistsList.setSelectedValue(selectedArtist, true);
		} else {
			artistsList.setSelectedIndex(0);
		}
	}
	
	public void changeDisplayedAlbums(List<AlbumBean> albums) {
		albumsListModel.clear();
		
		for(AlbumBean album : albums) {
			albumsListModel.addElement(album);
		}
		
		albumsList.setSelectedIndex(0);
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
	
	private class TracksTableModel extends AbstractTableModel {
		private String[] colNames = {"Track Number", "Artist", "Title", "Length"};
		
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
			return 4;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TrackBean track = tableTracks.get(rowIndex);
			
			switch(columnIndex) {
			case 0:
				return albumsList.getSelectedValue().getTrackNumber(track);
			case 1:
				return track.getArtist();
			case 2:
				return track.getTitle();
			case 3:
				int seconds = track.getSeconds();
				if(seconds < 10) {
					String secondsString = "0" + seconds;
					return track.getMinutes() + ":" + secondsString;
				}
				return track.getMinutes() + ":" + seconds;
			}
			return null;
		}
	}
}
