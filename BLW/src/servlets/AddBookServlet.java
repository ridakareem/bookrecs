package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.net.ssl.HttpsURLConnection;  // ✅ correct import
import java.net.*;
import org.json.*;
import java.sql.*;

public class AddBookServlet extends HttpServlet {

    // ✅ Optional: trust all certificates (safe for dev, remove in prod)
    static {
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("query");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // ✅ Use HttpsURLConnection
            String urlStr = "https://www.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(query, "UTF-8");
            URL url = new URL(urlStr);
            HttpsURLConnection apiConn = (HttpsURLConnection) url.openConnection();
            apiConn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(apiConn.getInputStream()));
            StringBuilder responseJson = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) responseJson.append(line);
            br.close();

            JSONObject json = new JSONObject(responseJson.toString());
            JSONArray items = json.getJSONArray("items");

            // Take the first book
            JSONObject firstBook = items.getJSONObject(0).getJSONObject("volumeInfo");
            String title = firstBook.optString("title", "Unknown");
            JSONArray authors = firstBook.optJSONArray("authors");

            StringBuilder authorList = new StringBuilder();
            if (authors != null) {
                for (int i = 0; i < authors.length(); i++) {
                    if (i > 0) authorList.append(", ");
                    authorList.append(authors.getString(i));
                }
            }

            String today = java.time.LocalDate.now().toString();

            // Save to DB
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO books (title, author, read_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, authorList.toString());
                stmt.setDate(3, Date.valueOf(today));
                stmt.executeUpdate();
            }

            out.println("<h3>Book added:</h3>");
            out.println("<p>" + title + " by " + authorList + "</p>");
            out.println("<a href='index.jsp'>Back</a>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}
