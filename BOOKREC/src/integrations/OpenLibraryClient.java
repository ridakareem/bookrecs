package integrations;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenLibraryClient {
    public String findCoverUrl(String title, String author) throws Exception {
        String qTitle = title == null ? "" : URLEncoder.encode(title, StandardCharsets.UTF_8);
        String qAuthor = author == null ? "" : URLEncoder.encode(author, StandardCharsets.UTF_8);
        String endpoint = "https://openlibrary.org/search.json?title=" + qTitle + (qAuthor.isEmpty() ? "" : "&author=" + qAuthor) + "&limit=1";
        URL url = new URL(endpoint);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("User-Agent", "BookRec/1.0");
        if (conn.getResponseCode() != 200) return null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line; while ((line = br.readLine()) != null) sb.append(line);
            String json = sb.toString();
            // Find first docs object
            int docsIdx = json.indexOf("\"docs\"");
            if (docsIdx < 0) return null;
            int arrStart = json.indexOf('[', docsIdx);
            int arrEnd = json.indexOf(']', arrStart);
            if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart) return null;
            String firstDoc = extractFirstObject(json.substring(arrStart + 1, arrEnd));
            if (firstDoc == null) return null;
            // Prefer cover_i
            Integer coverId = extractInt(firstDoc, "cover_i");
            if (coverId != null) {
                return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
            }
            // Fallback to first ISBN
            String isbn = extractFirstFromArray(firstDoc, "isbn");
            if (isbn != null && !isbn.isEmpty()) {
                return "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
            }
            return null;
        }
    }

    private String extractFirstObject(String arraySlice) {
        int brace = arraySlice.indexOf('{');
        if (brace < 0) return null;
        int depth = 0;
        for (int i = brace; i < arraySlice.length(); i++) {
            char c = arraySlice.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') { depth--; if (depth == 0) return arraySlice.substring(brace, i + 1); }
        }
        return null;
    }

    private Integer extractInt(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int colon = json.indexOf(':', k);
        if (colon < 0) return null;
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        int start = i;
        while (i < json.length() && Character.isDigit(json.charAt(i))) i++;
        if (start == i) return null;
        try { return Integer.parseInt(json.substring(start, i)); } catch (Exception e) { return null; }
    }

    private String extractFirstFromArray(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int aStart = json.indexOf('[', k);
        int aEnd = json.indexOf(']', aStart);
        if (aStart < 0 || aEnd < 0 || aEnd <= aStart) return null;
        String arr = json.substring(aStart + 1, aEnd);
        int q1 = arr.indexOf('"');
        int q2 = q1 >= 0 ? arr.indexOf('"', q1 + 1) : -1;
        if (q1 >= 0 && q2 > q1) return arr.substring(q1 + 1, q2);
        return null;
    }
}


