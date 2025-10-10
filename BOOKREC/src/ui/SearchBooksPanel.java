package ui;

import integrations.GoogleBooksClient;
import utils.DBConnection;
import utils.ImageLoader;
import utils.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class SearchBooksPanel extends JPanel {
    private final JTextField queryField;
    private final JButton searchBtn;
    private final DefaultListModel<GoogleBooksClient.Volume> model;
    private final JList<GoogleBooksClient.Volume> resultsList;
    private final JButton addBtn;
    private final GoogleBooksClient client = new GoogleBooksClient();
    private final ViewBooksPanel viewPanel; // reference to refresh after add

    public SearchBooksPanel(ViewBooksPanel viewPanel) {
        super(new BorderLayout(8, 8));
        this.viewPanel = viewPanel;

        // Top search bar
        JPanel top = new JPanel(new BorderLayout(6, 6));
        queryField = new JTextField();
        queryField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBtn.addActionListener(this::onSearch);

        top.add(queryField, BorderLayout.CENTER);
        top.add(searchBtn, BorderLayout.EAST);

        // List model + JList
        model = new DefaultListModel<>();
        resultsList = new JList<>(model);
        resultsList.setCellRenderer(new VolumeRenderer());
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add button
        addBtn = new JButton("Add Selected");
        addBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        addBtn.addActionListener(this::onAddSelected);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(resultsList), BorderLayout.CENTER);
        add(addBtn, BorderLayout.SOUTH);
    }

    private void onSearch(ActionEvent e) {
        String q = queryField.getText().trim();
        if (q.isEmpty()) return;

        searchBtn.setEnabled(false);
        model.clear();

        new Thread(() -> {
            try {
                List<GoogleBooksClient.Volume> vols = client.search(q, 20);
                SwingUtilities.invokeLater(() -> vols.forEach(model::addElement));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> searchBtn.setEnabled(true));
            }
        }).start();
    }

    private void onAddSelected(ActionEvent e) {
        GoogleBooksClient.Volume v = resultsList.getSelectedValue();
        if (v == null) return;

        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must be logged in.", "Auth", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String year = null;
        if (v.publishedDate != null && v.publishedDate.length() >= 4) {
            year = v.publishedDate.substring(0, 4);
            if (!year.matches("\\d{4}")) year = null;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            psUser.setString(1, username);
            var rs = psUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt(1);
                PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO books (user_id, title, author, cover_url, genres, year, rating) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                ins.setInt(1, userId);
                ins.setString(2, v.title);
                ins.setString(3, v.authors);
                ins.setString(4, v.thumbnail);
                ins.setString(5, v.genres);
                ins.setString(6, year);
                ins.setInt(7, 0);
                ins.executeUpdate();

                JOptionPane.showMessageDialog(this, "Added: " + v.title);

                // Refresh view panel immediately
                viewPanel.refresh();
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer
    private static class VolumeRenderer extends JPanel implements ListCellRenderer<GoogleBooksClient.Volume> {
        private final JLabel thumbLabel = new JLabel();
        private final JLabel titleLabel = new JLabel();
        private final JLabel authorLabel = new JLabel();

        public VolumeRenderer() {
            setLayout(new BorderLayout(8, 8));
            JPanel textPanel = new JPanel(new GridLayout(0, 1));
            textPanel.add(titleLabel);
            textPanel.add(authorLabel);
            textPanel.setOpaque(false);
            add(thumbLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GoogleBooksClient.Volume> list,
                                                      GoogleBooksClient.Volume value, int index, boolean isSelected, boolean cellHasFocus) {
            titleLabel.setText(value.title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

            String authorText = value.authors != null ? value.authors : "Unknown";
            if (value.publishedDate != null && value.publishedDate.length() >= 4) {
                authorText += " • " + value.publishedDate.substring(0, 4);
            }
            authorLabel.setText(authorText);
            authorLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            thumbLabel.setIcon(null);
            if (value.thumbnail != null && !value.thumbnail.isEmpty()) {
                new Thread(() -> {
                    try {
                        ImageIcon icon = ImageLoader.loadScaled(value.thumbnail, 40, 60);
                        if (icon != null) SwingUtilities.invokeLater(() -> thumbLabel.setIcon(icon));
                    } catch (Exception ignored) {}
                }).start();
            }

            setBackground(isSelected ? new Color(220, 220, 210) : new Color(245, 245, 235));
            setOpaque(true);
            return this;
        }
    }
}
