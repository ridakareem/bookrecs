package ui;

import utils.DBConfig;
import utils.DBInit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DBSetupFrame extends JFrame {
	private final JTextField driverField;
	private final JTextField urlField;
	private final JTextField userField;
	private final JPasswordField passField;

	public DBSetupFrame() {
		super("The Book Shelf - Database Setup");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new Color(230, 230, 216)); // light beige

		DBConfig.Values v = DBConfig.loadOrDefaults();
		driverField = new JTextField(v.driver, 28);
		urlField = new JTextField(v.url, 28);
		userField = new JTextField(v.user, 28);
		passField = new JPasswordField(v.pass, 28);

		// Header with title
		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		header.setBackground(new Color(230, 230, 216));
		JLabel title = new JLabel("THE BOOK SHELF", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));
		title.setForeground(Color.BLACK);
		header.add(title);

		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(new Color(230, 230, 216));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		JLabel driverLabel = new JLabel("Driver:");
		driverLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		gbc.gridx=0; gbc.gridy=0; gbc.anchor=GridBagConstraints.WEST; form.add(driverLabel, gbc);
		gbc.gridx=1; form.add(driverField, gbc);
		
		JLabel urlLabel = new JLabel("URL:");
		urlLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		gbc.gridx=0; gbc.gridy=1; form.add(urlLabel, gbc);
		gbc.gridx=1; form.add(urlField, gbc);
		
		JLabel userLabel = new JLabel("User:");
		userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		gbc.gridx=0; gbc.gridy=2; form.add(userLabel, gbc);
		gbc.gridx=1; form.add(userField, gbc);
		
		JLabel passLabel = new JLabel("Password:");
		passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		gbc.gridx=0; gbc.gridy=3; form.add(passLabel, gbc);
		gbc.gridx=1; form.add(passField, gbc);

		JButton testBtn = new JButton("Test & Save");
		testBtn.setFont(new Font("Arial", Font.PLAIN, 16));
		testBtn.setBackground(new Color(70, 96, 118)); // #466076
		testBtn.setForeground(Color.WHITE);
		testBtn.setFocusPainted(false);
		testBtn.addActionListener(this::onTestSave);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		bottom.setBackground(new Color(230, 230, 216));
		JButton backBtn = new JButton("Back");
		backBtn.setFont(new Font("Arial", Font.PLAIN, 16));
		backBtn.setBackground(new Color(70, 96, 118));
		backBtn.setForeground(Color.WHITE);
		backBtn.setFocusPainted(false);
		backBtn.addActionListener(ev -> dispose());
		bottom.add(backBtn);
		bottom.add(testBtn);

		add(header, BorderLayout.NORTH);
		add(form, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}

	private void onTestSave(ActionEvent e) {
		try {
			DBConfig.Values v = new DBConfig.Values();
			v.driver = driverField.getText().trim();
			v.url = urlField.getText().trim();
			v.user = userField.getText().trim();
			v.pass = new String(passField.getPassword());
			DBConfig.save(v);
			DBInit.initialize();
			JOptionPane.showMessageDialog(this, "Connection OK and schema ensured.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}


