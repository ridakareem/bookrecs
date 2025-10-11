package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import utils.DBConnection;
import utils.Session;

public class RecommendationPanel extends JPanel {
    private JComboBox<String> genreComboBox;
    private JList<BookRecommendation> recommendationsList;
    private DefaultListModel<BookRecommendation> listModel;
    private JLabel statusLabel;
    private JButton getRecommendationsBtn;
    private JButton addToLibraryBtn;
    private final ViewBooksPanel viewPanel;

    public RecommendationPanel(ViewBooksPanel viewPanel) {
        this.viewPanel = viewPanel;
        setBackground(new Color(230, 230, 216));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for genre selection
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel for recommendations
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for actions
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Load available genres
        loadAvailableGenres();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(230, 230, 216));
        panel.setBorder(BorderFactory.createTitledBorder("Select Genre"));

        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(genreLabel);

        genreComboBox = new JComboBox<>();
        genreComboBox.setPreferredSize(new Dimension(200, 30));
        genreComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(genreComboBox);

        getRecommendationsBtn = createStyledButton("Get Recommendations");
        getRecommendationsBtn.addActionListener(this::getRecommendations);
        panel.add(getRecommendationsBtn);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 230, 216));
        panel.setBorder(BorderFactory.createTitledBorder("Book Recommendations"));

        // Create list model and list
        listModel = new DefaultListModel<>();
        recommendationsList = new JList<>(listModel);
        recommendationsList.setCellRenderer(new BookRecommendationRenderer());
        recommendationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendationsList.setBackground(Color.WHITE);
        recommendationsList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(recommendationsList);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("Select a genre and click 'Get Recommendations'");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(230, 230, 216));

        addToLibraryBtn = createStyledButton("Add to My Library");
        addToLibraryBtn.setEnabled(false);
        addToLibraryBtn.addActionListener(this::addToLibrary);
        panel.add(addToLibraryBtn);

        JButton refreshBtn = createStyledButton("Refresh Genres");
        refreshBtn.addActionListener(e -> loadAvailableGenres());
        panel.add(refreshBtn);

        // Enable/disable add button based on selection
        recommendationsList.addListSelectionListener(e -> {
            addToLibraryBtn.setEnabled(recommendationsList.getSelectedValue() != null);
        });

        return panel;
    }

    private void loadAvailableGenres() {
        genreComboBox.removeAllItems();
        genreComboBox.addItem("All Genres");
        
        Set<String> allGenres = new HashSet<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Get genres from user's own books
            String username = Session.getCurrentUsername();
            if (username != null && !username.isEmpty()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT DISTINCT genres FROM books b " +
                    "JOIN users u ON b.user_id = u.id " +
                    "WHERE u.username = ? AND genres IS NOT NULL AND genres != ''"
                );
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    String genres = rs.getString("genres");
                    if (genres != null && !genres.trim().isEmpty()) {
                        String[] genreArray = genres.split(",");
                        for (String genre : genreArray) {
                            allGenres.add(genre.trim());
                        }
                    }
                }
            }
            
            // Add some popular genres if user has no books yet
            if (allGenres.isEmpty()) {
                allGenres.add("Fiction");
                allGenres.add("Mystery");
                allGenres.add("Romance");
                allGenres.add("Science Fiction");
                allGenres.add("Fantasy");
                allGenres.add("Biography");
                allGenres.add("History");
                allGenres.add("Self-Help");
                allGenres.add("Thriller");
                allGenres.add("Classic");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Add default genres if database error
            allGenres.add("Fiction");
            allGenres.add("Mystery");
            allGenres.add("Romance");
            allGenres.add("Science Fiction");
            allGenres.add("Fantasy");
        }
        
        // Add genres to combo box
        allGenres.stream().sorted().forEach(genreComboBox::addItem);
        
        statusLabel.setText("Found " + allGenres.size() + " genres. Select one to get recommendations.");
    }

    private void getRecommendations(ActionEvent e) {
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        if (selectedGenre == null || selectedGenre.equals("All Genres")) {
            statusLabel.setText("Please select a specific genre.");
            return;
        }

        statusLabel.setText("Getting recommendations for " + selectedGenre + "...");
        getRecommendationsBtn.setEnabled(false);

        // Run in background thread
        new Thread(() -> {
            try {
                List<BookRecommendation> recommendations = fetchRecommendations(selectedGenre);
                
                SwingUtilities.invokeLater(() -> {
                    listModel.clear();
                    for (BookRecommendation rec : recommendations) {
                        listModel.addElement(rec);
                    }
                    statusLabel.setText("Found " + recommendations.size() + " recommendations for " + selectedGenre);
                    getRecommendationsBtn.setEnabled(true);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error getting recommendations: " + ex.getMessage());
                    getRecommendationsBtn.setEnabled(true);
                });
            }
        }).start();
    }

    private List<BookRecommendation> fetchRecommendations(String genre) throws SQLException {
        List<BookRecommendation> recommendations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String username = Session.getCurrentUsername();
            
            // Get recommendations based on genre and user's reading history
            String sql = "SELECT DISTINCT title, author, cover_url, genres, rating " +
                        "FROM books b " +
                        "JOIN users u ON b.user_id = u.id " +
                        "WHERE (genres LIKE ? OR genres LIKE ? OR genres LIKE ?) " +
                        "AND (u.username != ? OR ? IS NULL) " +
                        "ORDER BY rating DESC, title ASC " +
                        "LIMIT 20";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + genre + "%");
            ps.setString(2, genre + ",%");
            ps.setString(3, "%," + genre);
            ps.setString(4, username);
            ps.setString(5, username);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String coverUrl = rs.getString("cover_url");
                String genres = rs.getString("genres");
                int rating = rs.getInt("rating");
                
                BookRecommendation rec = new BookRecommendation(title, author, coverUrl, genres, rating);
                recommendations.add(rec);
            }
        }
        
        // If no recommendations found, add some popular books for the genre
        if (recommendations.isEmpty()) {
            recommendations.addAll(getPopularBooksForGenre(genre));
        }
        
        return recommendations;
    }

    private List<BookRecommendation> getPopularBooksForGenre(String genre) {
        List<BookRecommendation> popularBooks = new ArrayList<>();
        
        switch (genre.toLowerCase()) {
            
     case "fiction":
         popularBooks.add(new BookRecommendation("The Night Circus", "Erin Morgenstern", "https://covers.openlibrary.org/b/isbn/9780385534635-L.jpg", "Fiction, Fantasy, Romance", 5));
         popularBooks.add(new BookRecommendation("Little Fires Everywhere", "Celeste Ng", "https://covers.openlibrary.org/b/isbn/9780735224292-L.jpg", "Fiction, Contemporary", 5));
         popularBooks.add(new BookRecommendation("Normal People", "Sally Rooney", "https://covers.openlibrary.org/b/isbn/9780571338740-L.jpg", "Fiction, Romance, Contemporary", 4));
         popularBooks.add(new BookRecommendation("Where the Crawdads Sing", "Delia Owens", "https://covers.openlibrary.org/b/isbn/9780735219090-L.jpg", "Fiction, Mystery, Contemporary", 5));
         popularBooks.add(new BookRecommendation("The Goldfinch", "Donna Tartt", "https://covers.openlibrary.org/b/isbn/9780316055437-L.jpg", "Fiction, Contemporary, Drama", 4));
         break;
     case "classic":
         popularBooks.add(new BookRecommendation("Pride and Prejudice", "Jane Austen", "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg", "Fiction, Classic, Romance", 5));
         popularBooks.add(new BookRecommendation("The Great Gatsby", "F. Scott Fitzgerald", "https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg", "Fiction, Classic", 5));
         popularBooks.add(new BookRecommendation("To Kill a Mockingbird", "Harper Lee", "https://covers.openlibrary.org/b/isbn/9780061120084-L.jpg", "Fiction, Classic", 5));
         popularBooks.add(new BookRecommendation("Moby-Dick", "Herman Melville", "https://covers.openlibrary.org/b/isbn/9780142437247-L.jpg", "Fiction, Classic, Adventure", 4));
         popularBooks.add(new BookRecommendation("War and Peace", "Leo Tolstoy", "https://covers.openlibrary.org/b/isbn/9780143035008-L.jpg", "Fiction, Classic, Historical", 5));
         break;

     case "mystery":
         popularBooks.add(new BookRecommendation("The Murder of Roger Ackroyd", "Agatha Christie", "https://covers.openlibrary.org/b/isbn/9780062079991-L.jpg", "Mystery, Crime", 5));
         popularBooks.add(new BookRecommendation("The Hound of the Baskervilles", "Arthur Conan Doyle", "https://covers.openlibrary.org/b/isbn/9780140623458-L.jpg", "Mystery, Detective", 5));
         popularBooks.add(new BookRecommendation("And Then There Were None", "Agatha Christie", "https://covers.openlibrary.org/b/isbn/9780062073470-L.jpg", "Mystery, Thriller", 5));
         popularBooks.add(new BookRecommendation("The Girl with the Dragon Tattoo", "Stieg Larsson", "https://covers.openlibrary.org/b/isbn/9780307269751-L.jpg", "Mystery, Thriller", 4));
         popularBooks.add(new BookRecommendation("In the Woods", "Tana French", "https://covers.openlibrary.org/b/isbn/9780143113492-L.jpg", "Mystery, Psychological", 4));
         break;

     case "science fiction":
     case "sci-fi":
         popularBooks.add(new BookRecommendation("Dune", "Frank Herbert", "https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg", "Science Fiction, Fantasy", 5));
         popularBooks.add(new BookRecommendation("Foundation", "Isaac Asimov", "https://covers.openlibrary.org/b/isbn/9780553293357-L.jpg", "Science Fiction", 5));
         popularBooks.add(new BookRecommendation("The Martian", "Andy Weir", "https://covers.openlibrary.org/b/isbn/9780804139021-L.jpg", "Science Fiction, Adventure", 4));
         popularBooks.add(new BookRecommendation("Neuromancer", "William Gibson", "https://covers.openlibrary.org/b/isbn/9780441569595-L.jpg", "Science Fiction, Cyberpunk", 4));
         popularBooks.add(new BookRecommendation("Ender's Game", "Orson Scott Card", "https://covers.openlibrary.org/b/isbn/9780812550702-L.jpg", "Science Fiction, Adventure", 5));
         break;

     case "fantasy":
         popularBooks.add(new BookRecommendation("The Lord of the Rings", "J.R.R. Tolkien", "https://covers.openlibrary.org/b/isbn/9780544003415-L.jpg", "Fantasy, Adventure", 5));
         popularBooks.add(new BookRecommendation("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "https://covers.openlibrary.org/b/isbn/9780590353403-L.jpg", "Fantasy, Young Adult", 5));
         popularBooks.add(new BookRecommendation("The Hobbit", "J.R.R. Tolkien", "https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg", "Fantasy, Adventure", 5));
         popularBooks.add(new BookRecommendation("A Game of Thrones", "George R.R. Martin", "https://covers.openlibrary.org/b/isbn/9780553573404-L.jpg", "Fantasy, Epic", 5));
         popularBooks.add(new BookRecommendation("The Name of the Wind", "Patrick Rothfuss", "https://covers.openlibrary.org/b/isbn/9780756404741-L.jpg", "Fantasy, Adventure", 5));
         break;

     case "romance":
         popularBooks.add(new BookRecommendation("Jane Eyre", "Charlotte Brontë", "https://covers.openlibrary.org/b/isbn/9780141441146-L.jpg", "Romance, Fiction", 5));
         popularBooks.add(new BookRecommendation("The Notebook", "Nicholas Sparks", "https://covers.openlibrary.org/b/isbn/9780446605235-L.jpg", "Romance, Contemporary", 4));
         popularBooks.add(new BookRecommendation("Pride and Prejudice", "Jane Austen", "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg", "Romance, Classic", 5));
         popularBooks.add(new BookRecommendation("Outlander", "Diana Gabaldon", "https://covers.openlibrary.org/b/isbn/9780440212560-L.jpg", "Romance, Historical", 4));
         popularBooks.add(new BookRecommendation("Me Before You", "Jojo Moyes", "https://covers.openlibrary.org/b/isbn/9780670026609-L.jpg", "Romance, Contemporary", 4));
         break;

     case "biography":
         popularBooks.add(new BookRecommendation("Long Walk to Freedom", "Nelson Mandela", "https://covers.openlibrary.org/b/isbn/9780316548182-L.jpg", "Biography, History", 5));
         popularBooks.add(new BookRecommendation("Steve Jobs", "Walter Isaacson", "https://covers.openlibrary.org/b/isbn/9781451648539-L.jpg", "Biography, Technology", 4));
         popularBooks.add(new BookRecommendation("Becoming", "Michelle Obama", "https://covers.openlibrary.org/b/isbn/9781524763138-L.jpg", "Biography, Memoir", 5));
         popularBooks.add(new BookRecommendation("The Diary of a Young Girl", "Anne Frank", "https://covers.openlibrary.org/b/isbn/9780553296983-L.jpg", "Biography, History", 5));
         popularBooks.add(new BookRecommendation("Einstein: His Life and Universe", "Walter Isaacson", "https://covers.openlibrary.org/b/isbn/9780743264747-L.jpg", "Biography, Science", 4));
         break;

     case "history":
         popularBooks.add(new BookRecommendation("A Short History of Nearly Everything", "Bill Bryson", "https://covers.openlibrary.org/b/isbn/9780767908184-L.jpg", "History, Science", 5));
         popularBooks.add(new BookRecommendation("Sapiens", "Yuval Noah Harari", "https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg", "History, Anthropology", 5));
         popularBooks.add(new BookRecommendation("Guns, Germs, and Steel", "Jared Diamond", "https://covers.openlibrary.org/b/isbn/9780393061314-L.jpg", "History, Sociology", 5));
         popularBooks.add(new BookRecommendation("The Wright Brothers", "David McCullough", "https://covers.openlibrary.org/b/isbn/9781476728742-L.jpg", "History, Biography", 4));
         popularBooks.add(new BookRecommendation("Team of Rivals", "Doris Kearns Goodwin", "https://covers.openlibrary.org/b/isbn/9780684824901-L.jpg", "History, Political", 4));
         break;

     case "self-help":
         popularBooks.add(new BookRecommendation("The 7 Habits of Highly Effective People", "Stephen Covey", "https://covers.openlibrary.org/b/isbn/9780743269513-L.jpg", "Self-Help, Business", 4));
         popularBooks.add(new BookRecommendation("Atomic Habits", "James Clear", "https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg", "Self-Help, Psychology", 5));
         popularBooks.add(new BookRecommendation("How to Win Friends and Influence People", "Dale Carnegie", "https://covers.openlibrary.org/b/isbn/9780671027032-L.jpg", "Self-Help, Communication", 4));
         popularBooks.add(new BookRecommendation("Think and Grow Rich", "Napoleon Hill", "https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg", "Self-Help, Finance", 4));
         popularBooks.add(new BookRecommendation("The Power of Habit", "Charles Duhigg", "https://covers.openlibrary.org/b/isbn/9780812981605-L.jpg", "Self-Help, Psychology", 5));
         break;

     case "thriller":
         popularBooks.add(new BookRecommendation("The Girl with the Dragon Tattoo", "Stieg Larsson", "https://covers.openlibrary.org/b/isbn/9780307269751-L.jpg", "Thriller, Mystery", 4));
         popularBooks.add(new BookRecommendation("Gone Girl", "Gillian Flynn", "https://covers.openlibrary.org/b/isbn/9780307588364-L.jpg", "Thriller, Psychological", 4));
         popularBooks.add(new BookRecommendation("The Silence of the Lambs", "Thomas Harris", "https://covers.openlibrary.org/b/isbn/9780312924584-L.jpg", "Thriller, Crime", 5));
         popularBooks.add(new BookRecommendation("Shutter Island", "Dennis Lehane", "https://covers.openlibrary.org/b/isbn/9780060589460-L.jpg", "Thriller, Mystery", 4));
         popularBooks.add(new BookRecommendation("Before I Go to Sleep", "S.J. Watson", "https://covers.openlibrary.org/b/isbn/9780062060556-L.jpg", "Thriller, Psychological", 4));
         break;

     default:
         popularBooks.add(new BookRecommendation("1984", "George Orwell", "https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg", "Fiction, Dystopian", 5));
         popularBooks.add(new BookRecommendation("The Catcher in the Rye", "J.D. Salinger", "https://covers.openlibrary.org/b/isbn/9780316769174-L.jpg", "Fiction, Coming-of-age", 4));
         popularBooks.add(new BookRecommendation("Brave New World", "Aldous Huxley", "https://covers.openlibrary.org/b/isbn/9780060850524-L.jpg", "Fiction, Dystopian", 5));
         popularBooks.add(new BookRecommendation("Fahrenheit 451", "Ray Bradbury", "https://covers.openlibrary.org/b/isbn/9781451673319-L.jpg", "Fiction, Dystopian", 4));
         popularBooks.add(new BookRecommendation("Animal Farm", "George Orwell", "https://covers.openlibrary.org/b/isbn/9780451526342-L.jpg", "Fiction, Satire", 5));
         break;
}

        
        return popularBooks;
    }

    private void addToLibrary(ActionEvent e) {
        BookRecommendation selected = recommendationsList.getSelectedValue();
        if (selected == null) return;

        String username = Session.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must be logged in.", "Auth", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Get user ID
            PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            psUser.setString(1, username);
            ResultSet rs = psUser.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt(1);
                
                // Check if book already exists
                PreparedStatement psCheck = conn.prepareStatement(
                    "SELECT COUNT(*) FROM books WHERE user_id=? AND title=? AND author=?"
                );
                psCheck.setInt(1, userId);
                psCheck.setString(2, selected.title);
                psCheck.setString(3, selected.author);
                ResultSet rsCheck = psCheck.executeQuery();
                rsCheck.next();
                
                if (rsCheck.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "This book is already in your library.", 
                        "Duplicate", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Add book to library
                PreparedStatement psInsert = conn.prepareStatement(
                    "INSERT INTO books (user_id, title, author, cover_url, genres, rating) VALUES (?, ?, ?, ?, ?, ?)"
                );
                psInsert.setInt(1, userId);
                psInsert.setString(2, selected.title);
                psInsert.setString(3, selected.author);
                psInsert.setString(4, selected.coverUrl);
                psInsert.setString(5, selected.genres);
                psInsert.setInt(6, 0); // No rating initially
                psInsert.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Added '" + selected.title + "' to your library!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh view panel
                if (viewPanel != null) {
                    viewPanel.refresh();
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add book: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(70, 96, 118));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Inner class for book recommendations
    public static class BookRecommendation {
        public final String title;
        public final String author;
        public final String coverUrl;
        public final String genres;
        public final int rating;

        public BookRecommendation(String title, String author, String coverUrl, String genres, int rating) {
            this.title = title;
            this.author = author;
            this.coverUrl = coverUrl;
            this.genres = genres;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return title + " — " + author;
        }
    }

    // Custom renderer for book recommendations
    private static class BookRecommendationRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                     boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof BookRecommendation) {
                BookRecommendation rec = (BookRecommendation) value;
                
                // Create HTML text with title, author, and genres
                StringBuilder html = new StringBuilder("<html><body style='width: 400px; padding: 5px;'>");
                html.append("<b>").append(escapeHtml(rec.title)).append("</b><br>");
                html.append("<i>by ").append(escapeHtml(rec.author)).append("</i><br>");
                
                if (rec.genres != null && !rec.genres.isEmpty()) {
                    html.append("<small style='color: #666;'>").append(escapeHtml(rec.genres)).append("</small>");
                }
                
                if (rec.rating > 0) {
                    html.append("<br><small style='color: #FFA500;'>");
                    for (int i = 0; i < rec.rating; i++) {
                        html.append("★");
                    }
                    html.append("</small>");
                }
                
                html.append("</body></html>");
                
                setText(html.toString());
                setVerticalTextPosition(SwingConstants.TOP);
                setIconTextGap(10);
            }
            
            return this;
        }
        
        private String escapeHtml(String text) {
            if (text == null) return "";
            return text.replace("&", "&amp;")
                      .replace("<", "&lt;")
                      .replace(">", "&gt;")
                      .replace("\"", "&quot;")
                      .replace("'", "&#39;");
        }
    }
}
