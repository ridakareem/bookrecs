package servlets;

import utils.GoogleBooksAPI;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class SearchBookServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String jsonResult = GoogleBooksAPI.searchBooks(query);

        request.setAttribute("json", jsonResult);
        RequestDispatcher rd = request.getRequestDispatcher("book_results.jsp");
        rd.forward(request, response);
    }
}
