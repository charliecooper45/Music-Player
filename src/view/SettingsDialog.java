package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Shows a dialog with settings for the program
 * @author Charlie
 */
public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private boolean lastFmOn;
	
	// listeners
	private SettingsChangedListener settingsChangedListener;
	
	// components
	private GridBagConstraints gbc;
	private JCheckBox lastFmBox;
	private JLabel usernameLbl;
	private JLabel passwordLbl;
	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	private JButton confirmButton;
	private JButton cancelButton;

	public SettingsDialog(JFrame frame, SettingsChangedListener settingsChangedListener, boolean lastFmOn, String username, String password) {
		super(frame, "Settings", true);
		this.settingsChangedListener = settingsChangedListener;
		this.lastFmOn = lastFmOn;
		setIconImage(Utils.createIcon("/view/resources/images/settingsicon.png").getImage());
		setSize(200, 200);
		setLayout(new GridBagLayout());
		setResizable(false);

		init(username, password);

		setLocationRelativeTo(frame);
	}

	private void init(String username, String password) {	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(!lastFmOn) {
					lastFmBox.setSelected(false);
					usernameTxt.setText("");
					passwordTxt.setText("");
				}
				SettingsDialog.this.dispose();
			}
		});
		gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		Insets lblInsets = new Insets(0, 10, 0, 0);
		gbc.insets = lblInsets;
		gbc.anchor = GridBagConstraints.LINE_END;
		lastFmBox = new JCheckBox("Last FM");
		lastFmBox.setSelected(lastFmOn);
		lastFmBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(lastFmBox.isSelected()) {
					usernameTxt.setEnabled(true);
					passwordTxt.setEnabled(true);
					usernameLbl.setEnabled(true);
					passwordLbl.setEnabled(true);
				} else {
					usernameTxt.setEnabled(false);
					passwordTxt.setEnabled(false);
					usernameLbl.setEnabled(false);
					passwordLbl.setEnabled(false);
				}
			}
		});
		Utils.setGBC(gbc, 0, 0, 1, 1, GridBagConstraints.BOTH);
		add(lastFmBox, gbc);
		usernameLbl = new JLabel("Username:");
		usernameLbl.setEnabled(lastFmOn);
		Utils.setGBC(gbc, 0, 1, 1, 1, GridBagConstraints.BOTH);
		add(usernameLbl, gbc);
		passwordLbl = new JLabel("Password");
		passwordLbl.setEnabled(lastFmOn);
		Utils.setGBC(gbc, 0, 2, 1, 1, GridBagConstraints.BOTH);
		add(passwordLbl, gbc);
		
		Insets fieldsInsets = new Insets(0, 0, 0, 10);
		gbc.insets = fieldsInsets;
		gbc.weightx = 10;
		gbc.anchor = GridBagConstraints.LINE_START;
		usernameTxt = new JTextField(40);
		usernameTxt.setEnabled(lastFmOn);
		Utils.setGBC(gbc, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL);
		add(usernameTxt, gbc);
		passwordTxt = new JPasswordField(40);
		passwordTxt.setEnabled(lastFmOn);
		Utils.setGBC(gbc, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL);
		add(passwordTxt, gbc);
		
		// If lastfm is active fill in the username and password
		if(lastFmOn) {
			usernameTxt.setText(username);
			passwordTxt.setText(password);
		}
		
		Insets defaultInsets = new Insets(0, 0, 0, 0);
		gbc.insets = defaultInsets;
		gbc.weightx = 1;
		Utils.setGBC(gbc, 0, 3, 4, 1, GridBagConstraints.BOTH);
		JPanel buttonPanel = new JPanel();
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(lastFmBox.isSelected()) {
					String username = usernameTxt.getText().trim();
					String password = new String(passwordTxt.getPassword()).trim();
					
					if(username.isEmpty() || password.isEmpty()) {
						JOptionPane.showMessageDialog(SettingsDialog.this, "The username or password field cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						boolean success = settingsChangedListener.lastFMOn(username, password);
						if(success)  {
							lastFmOn = true;
							SettingsDialog.this.dispose();
						}
					}
				} else {
					lastFmOn = false;
					settingsChangedListener.lastFMOff();
					usernameTxt.setText("");
					passwordTxt.setText("");
					SettingsDialog.this.dispose();
				}
			}
		});
		buttonPanel.add(confirmButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!lastFmOn) {
					lastFmBox.setSelected(false);
					usernameTxt.setText("");
					passwordTxt.setText("");
				}
				SettingsDialog.this.dispose();
			}
		});
		buttonPanel.add(cancelButton);
		add(buttonPanel, gbc);
		
	}
}
