import java.sql.*;
import java.util.Scanner;

public class BookLogger {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Failed to connect to database.");
            return;
        }

        while (true) {
            System.out.println("\n1. Add Book\n2. View Books\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter author (optional): ");
                    String author = sc.nextLine();
                    System.out.print("Enter date read (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    addBook(conn, title, author, date);
                    break;
                case 2:
                    viewBooks(conn);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void addBook(Connection conn, String title, String author, String date) {
        String sql = "INSERT INTO books (title, author, read_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author.isEmpty() ? null : author);
            stmt.setDate(3, Date.valueOf(date));
            stmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
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
}

