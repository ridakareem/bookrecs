package ui;

import utils.DBConnection;
import utils.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddBookPanel extends JPanel {
    private final JTextField titleField;
    private final JTextField authorField;
    private final JButton addButton;
    private final ViewBooksPanel viewBooksPanel;

    public AddBookPanel(ViewBooksPanel viewBooksPanel) {
        this.viewBooksPanel = viewBooksPanel;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(230, 230, 216));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(230, 230, 216));

        // Title
        JLabel titleLabel = new JLabel("Book Title:");
        titleField = new JTextField(25);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Author
        JLabel authorLabel = new JLabel("Author:");
        authorField = new JTextField(25);
        authorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        authorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add Button
        addButton = new JButton("Add Book");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(29, 185, 84));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(this::onAddBook);

        formPanel.add(titleLabel);
        formPanel.add(titleField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(authorLabel);
        formPanel.add(authorField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(addButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private void onAddBook(ActionEvent e) {
        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please log in first.", "Auth", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required.", "Validation", JOptionPane.WARNING_MESSAGE);
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
                titleField.setText("");
                authorField.setText("");

                if (viewBooksPanel != null) {
                    viewBooksPanel.refresh();
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
