package integrations;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Google Books API client with retry and rate-limit handling.
 */
public class GoogleBooksClient {
    private static final String API_KEY = "AIzaSyBLVKGVL4Iw-8WcqO0p3zOYYZ1KifmNkJA"; 
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_BACKOFF_MS = 1000;
    private static final int REQUEST_DELAY_MS = 500;

    public static class Volume {
        public final String title;
        public final String authors;
        public final String thumbnail;
        public final String genres;
        public final String publishedDate; // NEW

        public Volume(String title, String authors, String thumbnail, String genres, String publishedDate) {
            this.title = title;
            this.authors = authors;
            this.thumbnail = thumbnail;
            this.genres = genres;
            this.publishedDate = publishedDate;
        }

        @Override
        public String toString() {
            return title + (authors == null || authors.isEmpty() ? "" : " — " + authors);
        }
    }

    public List<Volume> search(String query, int maxResults) throws Exception {
        String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String endpoint = "https://www.googleapis.com/books/v1/volumes?q=" + q +
                "&maxResults=" + Math.max(1, Math.min(maxResults, 40));

        if (API_KEY != null && !API_KEY.isEmpty()) {
            endpoint += "&key=" + API_KEY;
        }

        int attempt = 0;
        while (true) {
            try {
                Thread.sleep(REQUEST_DELAY_MS);
                URL url = new URL(endpoint);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                int code = conn.getResponseCode();

                if (code == 429 && attempt < MAX_RETRIES) {
                    int backoff = INITIAL_BACKOFF_MS * (int) Math.pow(2, attempt);
                    Thread.sleep(backoff);
                    attempt++;
                    continue;
                }

                if (code != 200) throw new RuntimeException("Google Books API error: HTTP " + code);

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    return parseTitles(sb.toString());
                }

            } catch (RuntimeException e) {
                if (e.getMessage() != null && e.getMessage().contains("HTTP 429") && attempt < MAX_RETRIES) {
                    int backoff = INITIAL_BACKOFF_MS * (int) Math.pow(2, attempt);
                    Thread.sleep(backoff);
                    attempt++;
                } else throw e;
            }
        }
    }

    private List<Volume> parseTitles(String json) {
        List<Volume> list = new ArrayList<>();
        int itemsIdx = json.indexOf("\"items\"");
        if (itemsIdx < 0) return list;
        int arrStart = json.indexOf('[', itemsIdx);
        int arrEnd = json.lastIndexOf(']');
        if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart) return list;
        int idx = arrStart + 1;

        while (idx < arrEnd) {
            int vi = json.indexOf("\"volumeInfo\"", idx);
            if (vi < 0 || vi > arrEnd) break;
            int braceStart = json.indexOf('{', vi);
            if (braceStart < 0) break;
            int depth = 0;
            int i = braceStart;
            for (; i <= arrEnd; i++) {
                char c = json.charAt(i);
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) { i++; break; }
                }
            }
            String viJson = json.substring(braceStart, i);
            idx = i;

            String title = extractString(viJson, "title");
            if (title == null) continue;
            String authors = extractFirstFromArray(viJson, "authors");
            String genres = extractFirstFromArray(viJson, "categories");
            String thumb = extractString(viJson, "smallThumbnail");
            if (thumb == null) thumb = extractString(viJson, "thumbnail");
            if (thumb != null && thumb.startsWith("http://")) thumb = "https://" + thumb.substring(7);
            String publishedDate = extractString(viJson, "publishedDate");

            list.add(new Volume(title,
                    authors == null ? "" : authors,
                    thumb == null ? "" : thumb,
                    genres == null ? "" : genres,
                    publishedDate));
        }
        return list;
    }

    private String extractString(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0) return null;
        int c = json.indexOf(':', k);
        if (c < 0) return null;
        int ql = json.indexOf('"', c + 1);
        if (ql < 0) return null;
        int qr = json.indexOf('"', ql + 1);
        if (qr < 0) return null;
        return json.substring(ql + 1, qr);
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
