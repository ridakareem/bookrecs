package ui;

import utils.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignupFrame extends JFrame {
	private final JTextField usernameField;
	private final JPasswordField passwordField;

	public SignupFrame() {
		super("The Book Shelf - Sign Up");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(12, 12));
		getContentPane().setBackground(UiStyles.BG);

		// Header with title
		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		header.setBackground(UiStyles.BG);
		JLabel title = UiStyles.heading1("THE BOOK SHELF");
		header.add(title);

		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(UiStyles.BG);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		JLabel userLabel = UiStyles.label("Username");
		gbc.gridx = 0; gbc.gridy = 0;
		form.add(userLabel, gbc);
		usernameField = UiStyles.textField(18);
		gbc.gridx = 1;
		form.add(usernameField, gbc);

		JLabel passLabel = UiStyles.label("Password");
		gbc.gridx = 0; gbc.gridy = 1;
		form.add(passLabel, gbc);
		passwordField = UiStyles.passwordField(18);
		gbc.gridx = 1;
		form.add(passwordField, gbc);

		JButton signupBtn = UiStyles.primaryButton("Sign Up");
		signupBtn.addActionListener(this::onSignup);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		bottom.setBackground(UiStyles.BG);
		JButton backBtn = UiStyles.primaryButton("Back");
		backBtn.addActionListener(ev -> dispose());
		bottom.add(backBtn);
		bottom.add(signupBtn);

		JPanel centerWrapper = UiStyles.cardPanel(new BorderLayout());
		centerWrapper.add(form, BorderLayout.CENTER);
		add(header, BorderLayout.NORTH);
		add(centerWrapper, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}

	private void onSignup(ActionEvent e) {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword());
		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			Connection conn = DBConnection.getConnection();
			if (conn == null) {
				JOptionPane.showMessageDialog(this, "Database connection failed. Check JDBC jar, MySQL, and credentials.", "DB Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			PreparedStatement check = conn.prepareStatement("SELECT id FROM users WHERE username=?");
			check.setString(1, username);
			ResultSet rs = check.executeQuery();
			if (rs.next()) {
				JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
			insert.setString(1, username);
			insert.setString(2, password);
			insert.executeUpdate();

			System.out.println("Signup success for user: " + username);
			Session.setCurrentUsername(username);
			MainFrame mf = new MainFrame();
			mf.setVisible(true);
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Signup error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}


