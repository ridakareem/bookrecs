package ui;

import utils.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddBookApp extends JFrame {
    private final JTextField usernameField;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JButton addButton;

    public AddBookApp() {
        super("Add Book");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 230, 216));

        // --- Form panel ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(new Color(230, 230, 216));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Username ---
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField(25);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(usernameLabel);
        form.add(usernameField);
        form.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Book Title ---
        JLabel titleLabel = new JLabel("Book Title:");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleField = new JTextField(25);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(titleLabel);
        form.add(titleField);
        form.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Author ---
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        authorField = new JTextField(25);
        authorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        authorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(authorLabel);
        form.add(authorField);
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Add button ---
        addButton = new JButton("Add Book");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(29, 185, 84));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(this::onAddBook);

        form.add(addButton);

        add(form, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onAddBook(ActionEvent e) {
        String username = usernameField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        if (username.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Title, and Author are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            psUser.setString(1, username);
            ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO books (user_id, title, author) VALUES (?, ?, ?)"
                );
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, author);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                titleField.setText("");
                authorField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddBookApp::new);
    }
}
