package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;

public class RemoveBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = DBConnection.getConnection();

            // Get all books to display
            String sql = "SELECT id, title, author FROM books ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<Integer> bookIds = new ArrayList<>();
            out.println("<h2>Books:</h2>");
            out.println("<form method='post'>");
            int count = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                bookIds.add(id);
                out.println("<input type='radio' name='bookId' value='" + id + "'> " +
                        rs.getString("title") + " by " + rs.getString("author") + "<br>");
                count++;
            }

            if (bookIds.isEmpty()) {
                out.println("<p>No books to remove.</p>");
            } else {
                out.println("<br><input type='submit' value='Remove Book'>");
            }
            out.println("</form>");
            out.println("<a href='index.jsp'>Back</a>");

            // If user submitted a bookId, delete it
            String bookIdParam = request.getParameter("bookId");
            if (bookIdParam != null) {
                int bookId = Integer.parseInt(bookIdParam);
                String deleteSql = "DELETE FROM books WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, bookId);
                    deleteStmt.executeUpdate();
                    out.println("<p>Book removed successfully!</p>");
                }
            }

        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
