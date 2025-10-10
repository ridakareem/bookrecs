package ui;

import utils.DBConnection;
import utils.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        super("Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(230, 230, 216));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(230, 230, 216));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(29, 185, 84));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this::onLogin);

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
    }

    // This method can be overridden from App.java or elsewhere
    protected void onLoginSuccess() {
        // Default empty implementation
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login success for user: " + username);
                Session.setCurrentUsername(username);

                // Call overridable method
                onLoginSuccess();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Standalone testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
