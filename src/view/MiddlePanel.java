package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import model.TrackBean;

/**
 * A JPanel that displays the tracks in the user`s library.
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class MiddlePanel extends JPanel{
	
	private JList<String> artistsList;
	private DefaultListModel<String> artistsListModel;
	private JList<String> albumsList;
	private DefaultListModel<String> albumsListModel;
	private JTable tracksTable;
	private TracksTableModel tableModel;
	private TableValueListener tableValueListener;

	public MiddlePanel() {
		setBackground(Color.BLUE);
		setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		setupListsPanel();
		
		tableModel = new TracksTableModel();
		tracksTable = new JTable(tableModel);
		add(new JScrollPane(tracksTable), BorderLayout.CENTER);
		tableModel.fireTableDataChanged();
	}
	
	private void setupListsPanel() {
		// TODO NEXT B: Set this up with labels for "Artist" and "Album" to make it clearer to the user.
		String[] test = {"Brand New", "Other", "more", "etc", "blah"};
		
		artistsListModel = new DefaultListModel<>();
		artistsListModel.add(0, "test");
		artistsList = new JList<>(artistsListModel);
		artistsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		albumsList = new JList<>(test);
		albumsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, artistsList, albumsList);
		pane.setResizeWeight(0.5);
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
	 * @param tableValueListener the tableValueListener to set
	 */
	public void setTableValueListener(TableValueListener tableValueListener) {
		this.tableValueListener = tableValueListener;
	}
	
	public void updatePanel(List<String> artists) {
		tableModel.fireTableDataChanged();
		
		// Update the artists list
		artistsListModel.removeAllElements();
		for(String artist : artists) {
			artistsListModel.addElement(artist);
		}
		
	}
	
	private class TracksTableModel extends AbstractTableModel {
		private String[] colNames = {"Artist", "Title", "Length"};
		
		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public int getRowCount() {
			return tableValueListener.getNumberOfTracks();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TrackBean track = (TrackBean) tableValueListener.getTableValue(rowIndex);
			switch(columnIndex) {
			case 0:
				return track.getArtist();
			case 1:
				return track.getTitle();
			case 2:
				return track.getMinutes() + ":" + track.getSeconds();
			}
			return null;
		}
	}
}
