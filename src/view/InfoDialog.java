package view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import model.TrackBean;

/**
 * Shows a dialog with information about the currently selected track displayed.
 * @author Charlie
 */
public class InfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private boolean dataChanged = false;
	
	// listeners
	private TrackEditedListener trackEditedListener;
	
	// components for the dialog
	private TrackBean track;
	private GridBagConstraints gbc;
	private JTextField trackNumberTxtField;
	private JTextField trackNameTxtField;
	private JTextField artistNameTxtField;
	private JTextField albumNameTxtField;
	private JTextField genreTxtField;
	private JTextArea commentTxtArea;
	private JScrollPane commentScrlPane;
	
	// components for the button panel
	private JPanel buttonPanel;
	private JButton confirmBtn;
	private JButton cancelBtn;

	public InfoDialog(JFrame frame, TrackBean track, TrackEditedListener trackEditedListener) {
		super(frame, "Track Information", true);
		this.track = track;
		this.trackEditedListener = trackEditedListener;
		setSize(400, 500);
		setLayout(new GridBagLayout());
		setResizable(false);

		init();

		setLocationRelativeTo(frame);
	}

	private void init() {
		gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;

		// add the labels
		Utils.setGBC(gbc, 0, 0, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Track Number: "), gbc);
		Utils.setGBC(gbc, 0, 1, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Track Name: "), gbc);
		Utils.setGBC(gbc, 0, 2, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Artist Name: "), gbc);
		Utils.setGBC(gbc, 0, 3, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Album Name: "), gbc);
		Utils.setGBC(gbc, 0, 4, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Genre: "), gbc);

		// add the textfields
		gbc.weightx = 5;
		trackNumberTxtField = new JTextField(100);
		trackNumberTxtField.setText(String.valueOf(track.getTrackNumber()));
		Utils.setGBC(gbc, 1, 0, 3, 1, GridBagConstraints.HORIZONTAL);
		add(trackNumberTxtField, gbc);
		trackNameTxtField = new JTextField(100);
		trackNameTxtField.setText(track.getTitle());
		Utils.setGBC(gbc, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL);
		add(trackNameTxtField, gbc);
		artistNameTxtField = new JTextField(100);
		artistNameTxtField.setText(track.getArtist());
		Utils.setGBC(gbc, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL);
		add(artistNameTxtField, gbc);
		albumNameTxtField = new JTextField(100);
		albumNameTxtField.setText(track.getAlbum().getTitle());
		Utils.setGBC(gbc, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL);
		add(albumNameTxtField, gbc);
		genreTxtField = new JTextField(100);
		genreTxtField.setText(track.getGenre());
		Utils.setGBC(gbc, 1, 4, 3, 1, GridBagConstraints.HORIZONTAL);
		add(genreTxtField, gbc);

		// add the comments box
		Utils.setGBC(gbc, 0, 5, 4, 1, GridBagConstraints.NONE);
		add(new JLabel("Comments: "), gbc);
		commentTxtArea = new JTextArea(100, 50);
		commentTxtArea.setLineWrap(true);
		commentTxtArea.setWrapStyleWord(true);
		commentScrlPane = new JScrollPane(commentTxtArea);
		commentScrlPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Insets insets = new Insets(0, 10, 0, 10);
		Utils.setGBC(gbc, 0, 6, 4, 2, GridBagConstraints.BOTH);
		gbc.insets = insets;
		add(commentScrlPane, gbc);
		
		// add the document listener for the txt components
		JTextComponent[] txtComponentArray =  {trackNumberTxtField, trackNameTxtField, artistNameTxtField, albumNameTxtField, genreTxtField, commentTxtArea};
		for(JTextComponent comp : txtComponentArray) {
			DocumentListener docListener = new DocumentListener(){
				@Override
				public void removeUpdate(DocumentEvent e) {
					dataChanged = true;
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					dataChanged = true;
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					dataChanged = true;
				}
			};
			comp.getDocument().addDocumentListener(docListener);
		}
		
		// add the buttons panel
		buttonPanel = new JPanel();
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 25, 0);
		buttonPanel.setLayout(flow);
		confirmBtn = new JButton("Confirm");
		confirmBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Data changed: " + dataChanged);
				if(dataChanged) {
					int trackNumber = Integer.parseInt(trackNumberTxtField.getText());
					String trackTitle = trackNameTxtField.getText();
					String artist = artistNameTxtField.getText();
					String album = albumNameTxtField.getText();
					String genre = genreTxtField.getText();
					String comments = commentTxtArea.getText();
					
					trackEditedListener.trackChanged(track, trackNumber, trackTitle, artist, album, genre, comments);
				}
				dispose();
			}
		});
		buttonPanel.add(confirmBtn);
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(cancelBtn);
		Utils.setGBC(gbc, 0, 8, 4, 1, GridBagConstraints.HORIZONTAL);
		Insets defaultInsets = new Insets(0, 0, 0, 0);
		gbc.insets = defaultInsets;
		gbc.anchor = GridBagConstraints.CENTER;
		add(buttonPanel, gbc);
	}
}
