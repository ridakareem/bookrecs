package servlets;
import utils.DBConnection;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AddBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String username = (String) request.getSession().getAttribute("username");

        try (Connection conn = DBConnection.getConnection()) {
            // Get user id
            PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            psUser.setString(1, username);
            ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                PreparedStatement ps = conn.prepareStatement("INSERT INTO books (user_id, title) VALUES (?, ?)");
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.executeUpdate();
            }
            response.sendRedirect("viewBooks");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=add");
        }
    }
}
