package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.TrackBean;

/**
 * A JPanel that displays the tracks in the user`s library.
 * @author Charlie
 *
 */
@SuppressWarnings("serial")
public class BottomPanel extends JPanel{
	
	private JTable tracksTable;
	private TracksTableModel tableModel;
	private TableValueListener tableValueListener;

	public BottomPanel() {
		setBackground(Color.BLUE);
		setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		tableModel = new TracksTableModel();
		tracksTable = new JTable(tableModel);
		add(new JScrollPane(tracksTable), BorderLayout.CENTER);
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
	
	private class TracksTableModel extends AbstractTableModel {
		private String[] colNames = {"Artist", "Title", "Length"};
		
		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public int getRowCount() {
			return 1;
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
