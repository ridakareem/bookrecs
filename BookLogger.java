import java.sql.*;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import org.json.*;

public class BookLogger {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Failed to connect to database.");
            return;
        }

        while (true) {
            System.out.println("\n1. Add Book\n2. View Books\n3. Remove Book\n4. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer
        
            switch (choice) {
                case 1:
                    addBookFromGoogle(conn, sc);
                    break;
                case 2:
                    viewBooks(conn);
                    break;
                case 3:
                    removeBook(conn, sc);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void addBookFromGoogle(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter search query: ");
            String query = sc.nextLine();
    
            // Call Google Books API
            String urlStr = "https://www.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(query, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection apiConn = (HttpURLConnection) url.openConnection();
            apiConn.setRequestMethod("GET");
    
            BufferedReader br = new BufferedReader(new InputStreamReader(apiConn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
    
            JSONObject json = new JSONObject(response.toString());
            JSONArray items = json.getJSONArray("items");
    
            // Show top 5 results
            for (int idx = 0; idx < Math.min(5, items.length()); idx++) {
                JSONObject volumeInfo = items.getJSONObject(idx).getJSONObject("volumeInfo");
                String title = volumeInfo.optString("title", "Unknown");
                JSONArray authors = volumeInfo.optJSONArray("authors");
    
                StringBuilder authorList = new StringBuilder();
                if (authors != null) {
                    for (int j = 0; j < authors.length(); j++) {
                        if (j > 0) authorList.append(", ");
                        authorList.append(authors.getString(j));
                    }
                }
    
                System.out.println((idx + 1) + ". " + title + " by " + (authorList.length() > 0 ? authorList : "Unknown"));
            }
    
            // Ask user to pick one
            System.out.print("Pick a number to save (0 to cancel): ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline
            if (choice < 1 || choice > Math.min(5, items.length())) {
                System.out.println("Cancelled.");
                return;
            }
    
            // Extract chosen book
            JSONObject chosenInfo = items.getJSONObject(choice - 1).getJSONObject("volumeInfo");
            String title = chosenInfo.optString("title", "Unknown");
            JSONArray authors = chosenInfo.optJSONArray("authors");
    
            StringBuilder authorList = new StringBuilder();
            if (authors != null) {
                for (int j = 0; j < authors.length(); j++) {
                    if (j > 0) authorList.append(", ");
                    authorList.append(authors.getString(j));
                }
            }
    
            // For simplicity, use current date
            String today = java.time.LocalDate.now().toString();
    
            // Save to DB
            String sql = "INSERT INTO books (title, author, read_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, authorList.length() > 0 ? authorList.toString() : null);
                stmt.setDate(3, Date.valueOf(today));
                stmt.executeUpdate();
                System.out.println("Book added successfully!");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewBooks(Connection conn) {
        String sql = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " +
                                   rs.getString("title") + " by " +
                                   rs.getString("author") + " - " +
                                   rs.getDate("read_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeBook(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter book ID to remove: ");
            int id = sc.nextInt();
            sc.nextLine(); // clear buffer
    
            String sql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book removed successfully!");
                } else {
                    System.out.println("No book found with that ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}

