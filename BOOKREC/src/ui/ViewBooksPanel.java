package ui;

import utils.DBConnection;
import utils.ImageLoader;
import utils.CoverBackfill;
import utils.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class ViewBooksPanel extends JPanel {
    private final JPanel listPanel;
    private final JScrollPane scroll;

    public ViewBooksPanel() {
        super(new BorderLayout());
        setBackground(UiStyles.BG);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UiStyles.BG);
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getViewport().setBackground(UiStyles.BG);
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

        // Backfill missing covers
        CoverBackfill.backfillMissingCoversForUser(username, 50);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT b.id, b.title, b.author, COALESCE(b.rating,0) as rating, b.year, b.date_added, b.cover_url " +
                    "FROM books b JOIN users u ON b.user_id=u.id WHERE u.username=? ORDER BY b.id DESC"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int rating = rs.getInt("rating");
                String year = rs.getString("year");
                String dateAdded = rs.getDate("date_added") != null ? sdf.format(rs.getDate("date_added")) : null;
                String coverUrl = rs.getString("cover_url");

                JPanel row = createBookRow(bookId, title, author, rating, year, dateAdded, coverUrl);
                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            listPanel.add(Box.createVerticalGlue());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load books: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        listPanel.revalidate();
        listPanel.repaint();
        scroll.getVerticalScrollBar().setValue(0);
    }

    private JPanel createBookRow(int bookId, String title, String author, int rating, String year, String dateAdded, String coverUrl) {
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setBackground(new Color(250, 250, 240));
        row.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Cover
        JLabel coverLabel = new JLabel();
        coverLabel.setOpaque(false);
        coverLabel.setPreferredSize(new Dimension(60, 90));

        if (coverUrl != null && !coverUrl.isEmpty()) {
            new Thread(() -> {
                try {
                    ImageIcon icon = ImageLoader.loadScaled(coverUrl, 60, 90);
                    if (icon != null) SwingUtilities.invokeLater(() -> coverLabel.setIcon(icon));
                } catch (Exception ignored) {}
            }).start();
        } else {
            coverLabel.setBackground(new Color(200, 200, 200));
            coverLabel.setOpaque(true);
        }

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        JLabel authorLabel = new JLabel((author == null ? "Unknown" : author) +
                (year != null ? " • " + year : "") +
                (dateAdded != null ? " • Added: " + dateAdded : ""));
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        authorLabel.setForeground(new Color(100, 100, 100));

        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);

        // Stars + Delete panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JPanel starsPanel = createInteractiveStars(bookId, rating);
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setBackground(new Color(240, 240, 230));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + title + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                    refresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        rightPanel.add(starsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(deleteBtn);

        // Combine cover + info
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(coverLabel, BorderLayout.WEST);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        row.add(leftPanel, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);

        // Hover effect
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(240, 240, 230));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(250, 250, 240));
            }
        });

        return row;
    }

    private JPanel createInteractiveStars(int bookId, int initialRating) {
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        starsPanel.setOpaque(false);

        final int[] rating = {initialRating};
        JLabel[] starLabels = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            JLabel star = new JLabel(i < initialRating ? "\u2605" : "\u2606");
            star.setFont(new Font("SansSerif", Font.PLAIN, 18));
            star.setForeground(i < initialRating ? new Color(0x1DB954) : new Color(150, 150, 150));

            star.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (rating[0] == starIndex && starIndex == 1) rating[0] = 0;
                    else rating[0] = starIndex;

                    updateStars(starLabels, rating[0]);

                    // Save rating to DB
                    new Thread(() -> {
                        try (Connection conn = DBConnection.getConnection()) {
                            PreparedStatement ps = conn.prepareStatement("UPDATE books SET rating=? WHERE id=?");
                            ps.setInt(1, rating[0]);
                            ps.setInt(2, bookId);
                            ps.executeUpdate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

            starLabels[i] = star;
            starsPanel.add(star);
        }

        return starsPanel;
    }

    private void updateStars(JLabel[] starLabels, int rating) {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                starLabels[i].setText("\u2605");
                starLabels[i].setForeground(new Color(0x1DB954));
            } else {
                starLabels[i].setText("\u2606");
                starLabels[i].setForeground(new Color(150, 150, 150));
            }
        }
    }
}
