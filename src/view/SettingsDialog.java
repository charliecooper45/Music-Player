package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	public SettingsDialog(JFrame frame, SettingsChangedListener settingsChangedListener, boolean lastFmOn) {
		super(frame, "Settings", true);
		this.settingsChangedListener = settingsChangedListener;
		this.lastFmOn = lastFmOn;
		setIconImage(Utils.createIcon("/view/resources/images/settingsicon.png").getImage());
		setSize(200, 200);
		setLayout(new GridBagLayout());
		setResizable(false);

		init();

		setLocationRelativeTo(frame);
	}

	private void init() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(lastFmBox.isSelected()) {
					settingsChangedListener.lastFMOn(usernameTxt.getText(), new String(passwordTxt.getPassword()));
				} else {
					settingsChangedListener.lastFMOff();
				}
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
	}
}
