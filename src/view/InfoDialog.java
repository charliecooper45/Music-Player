package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.TrackBean;

/**
 * Shows a dialog with information about the currently selected track displayed.
 * @author Charlie
 */
public class InfoDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private TrackBean track;
	private GridBagConstraints gbc;
	private JTextField trackNameTxtField;
	private JTextField artistNameTxtField;
	private JTextField albumNameTxtField;

	public InfoDialog(JFrame frame, TrackBean track) {
		super(frame, "Track Information", true);
		this.track = track;
		setSize(400, 500);
		this.setLayout(new GridBagLayout());
		
		init();
		
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
	private void init() {
		gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		Utils.setGBC(gbc, 0, 0, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Track Name: "), gbc);
		Utils.setGBC(gbc, 0, 1, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Artist Name: "), gbc);
		Utils.setGBC(gbc, 0, 2, 1, 1, GridBagConstraints.NONE);
		add(new JLabel("Album Name: "), gbc);
		
		// Add the textfields
		gbc.weightx = 5;
		trackNameTxtField = new JTextField(100);
		trackNameTxtField.setText(track.getTitle());
		Utils.setGBC(gbc, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL);
		add(trackNameTxtField, gbc);
		artistNameTxtField = new JTextField(100);
		artistNameTxtField.setText(track.getArtist());
		Utils.setGBC(gbc, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL);
		add(artistNameTxtField, gbc);
		albumNameTxtField = new JTextField(100);
		albumNameTxtField.setText(track.getAlbum().getTitle());
		Utils.setGBC(gbc, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL);
		add(albumNameTxtField, gbc);
		
		
		
		
	}
}
