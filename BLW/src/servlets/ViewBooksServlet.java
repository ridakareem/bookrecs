package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class ViewBooksServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM books ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            out.println("<h2>Your Books:</h2>");
            out.println("<ul>");
            while (rs.next()) {
                out.println("<li>" + rs.getString("title") + " by " +
                        rs.getString("author") + " - " +
                        rs.getDate("read_date") + "</li>");
            }
            out.println("</ul>");
            out.println("<a href='index.jsp'>Back</a>");
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
