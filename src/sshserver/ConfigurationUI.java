package sshserver;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConfigurationUI extends JDialog {
	private static final long serialVersionUID = -1833787576102591717L;
	private JPanel contentPane;
	private JTextField textFieldUsername;
	private JTextField textFieldPort;
	private JPasswordField passwordField;


	public ConfigurationUI(final boolean initialSetup) {
		setModal(true);
		setResizable(false);
		setTitle("SSH Server Setup");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 338, 150);
		setIconImage(Utility.getAppIcon().getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 312, 70);
		contentPane.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblUsername = new JLabel("Username :");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 0;
		panel.add(lblUsername, gbc_lblUsername);
		
		textFieldUsername = new JTextField(Configuration.getUsername());
		GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.gridx = 1;
		gbc_textFieldUsername.gridy = 0;
		panel.add(textFieldUsername, gbc_textFieldUsername);
		textFieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password :");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		panel.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField(Configuration.getPassword());
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		panel.add(passwordField, gbc_passwordField);
		
		JLabel lblPort = new JLabel("Port :");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 2;
		panel.add(lblPort, gbc_lblPort);
		
		textFieldPort = new JTextField(String.valueOf(Configuration.getPort()));
		GridBagConstraints gbc_textFieldPort = new GridBagConstraints();
		gbc_textFieldPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPort.gridx = 1;
		gbc_textFieldPort.gridy = 2;
		panel.add(textFieldPort, gbc_textFieldPort);
		textFieldPort.setColumns(10);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textFieldUsername.getText()==null || textFieldUsername.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Username must be filled in!", SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (passwordField.getPassword().length==0) {
					JOptionPane.showMessageDialog(null, "Password must be filled in!", SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (textFieldPort.getText()==null || textFieldPort.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Port must be filled in!", SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				} else if (!Utility.isNumber(textFieldPort.getText()) || Long.parseLong(textFieldPort.getText())<=0 || Long.parseLong(textFieldPort.getText())>=65536 ) {
					JOptionPane.showMessageDialog(null, "Invalid port number!", SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int oldPort=Configuration.getPort();
				Configuration.setUsername(textFieldUsername.getText());
				Configuration.setPassword(new String(passwordField.getPassword()));
				Configuration.setPort(Integer.parseInt(textFieldPort.getText()));
				
				try {
					Configuration.writeSettings();
				} catch (Exception exp) {
					JOptionPane.showMessageDialog(null, "Error writing configuration file. "+exp.getMessage() , SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
				}
				
				if (oldPort!=Configuration.getPort()) {
					Thread t=new Thread(() -> {
						if (!SSHServer.sshd.isClosed()) {
							try {
								SSHServer.stopSSHServer();
								SSHServer.startSSHServer();
							} catch (Exception exp) {
								JOptionPane.showMessageDialog(null, "Restart SSH Server Error. "+exp.getMessage() , SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
								exp.printStackTrace();
							}
						}
					});
					t.start();
				}
				
				dispose();
			}
		});
		btnOK.setBounds(163, 92, 72, 23);
		this.getRootPane().setDefaultButton(btnOK);
		contentPane.add(btnOK);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (initialSetup) System.exit(0);
				else dispose();
			}
		});
		btnCancel.setBounds(245, 92, 77, 23);
		contentPane.add(btnCancel);
	}
}
