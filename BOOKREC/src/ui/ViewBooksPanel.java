package ui;

import utils.DBConnection;
import utils.Session;
import utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewBooksPanel extends JPanel {
    private final JPanel listPanel;
    private final JScrollPane scroll;

    public ViewBooksPanel() {
        super(new BorderLayout());
        setBackground(new Color(230, 230, 216));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(230, 230, 216));
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getViewport().setBackground(new Color(230, 230, 216));
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        listPanel.removeAll();
        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT b.id, b.title, b.author, b.cover_url FROM books b JOIN users u ON b.user_id=u.id WHERE u.username=? ORDER BY b.id DESC"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String cover = rs.getString("cover_url");

                JPanel row = createBookRow(bookId, title, author, cover);
                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load books: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createBookRow(int bookId, String title, String author, String coverUrl) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(250, 250, 240));
        row.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel coverLabel = new JLabel();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            try {
                ImageIcon icon = ImageLoader.loadScaled(coverUrl, 60, 90);
                if (icon != null) coverLabel.setIcon(icon);
            } catch (Exception ignored) {}
        }

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);

        row.add(coverLabel, BorderLayout.WEST);
        row.add(infoPanel, BorderLayout.CENTER);

        return row;
    }
}

