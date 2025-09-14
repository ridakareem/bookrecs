import java.sql.*;
import java.util.Scanner;

public class Login {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Failed to connect to database.");
            return;
        }

        while (true) {
            System.out.println("\n1. Sign In\n2. Sign Up\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: // sign in
                    System.out.print("Enter username: ");
                    String loginUser = sc.nextLine();
                    System.out.print("Enter password: ");
                    String loginPass = sc.nextLine();

                    if (signIn(conn, loginUser, loginPass)) {
                        System.out.println("✅ Login successful!");
                    } else {
                        System.out.println("❌ Invalid username or password.");
                    }
                    break;

                case 2: // sign up
                    System.out.print("Choose a username: ");
                    String newUser = sc.nextLine();
                    System.out.print("Choose a password: ");
                    String newPass = sc.nextLine();

                    if (signUp(conn, newUser, newPass)) {
                        System.out.println("✅ User registered successfully!");
                    } else {
                        System.out.println("❌ Username already exists.");
                    }
                    break;

                case 3: // exit
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Sign Up
    public static boolean signUp(Connection conn, String username, String password) {
        String sql = "INSERT INTO userlogin (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // username already exists
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sign In
    public static boolean signIn(Connection conn, String username, String password) {
        String sql = "SELECT * FROM userlogin WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if match found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
