package servlets;

import utils.DBConnection;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

public class ViewBooksServlet extends HttpServlet {
    public static class Book {
        public int id;
        public String title;
        public Book(int id, String title) { this.id = id; this.title = title; }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        List<Book> books = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username=?");
            psUser.setString(1, username);
            ResultSet rsUser = psUser.executeQuery();
            if (rsUser.next()) {
                int userId = rsUser.getInt("id");
                PreparedStatement psBooks = conn.prepareStatement("SELECT id, title FROM books WHERE user_id=?");
                psBooks.setInt(1, userId);
                ResultSet rsBooks = psBooks.executeQuery();
                while (rsBooks.next()) {
                    books.add(new Book(rsBooks.getInt("id"), rsBooks.getString("title")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("books", books);
        RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
        rd.forward(request, response);
    }
}
