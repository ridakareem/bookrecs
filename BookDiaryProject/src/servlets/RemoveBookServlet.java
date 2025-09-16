package servlets;

import utils.DBConnection;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RemoveBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int bookId = Integer.parseInt(request.getParameter("bookId"));

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
            ps.setInt(1, bookId);
            ps.executeUpdate();
            response.sendRedirect("viewBooks");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("viewBooks?error=remove");
        }
    }
}
