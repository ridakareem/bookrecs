package utils;

import integrations.GoogleBooksClient;
import integrations.OpenLibraryClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public final class CoverBackfill {
	private CoverBackfill() {}

	public static int backfillMissingCoversForUser(String username, int maxUpdates) {
		int updated = 0;
		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null) return 0;
			PreparedStatement ps = conn.prepareStatement(
					"SELECT b.id, b.title, COALESCE(b.author,'') FROM books b JOIN users u ON b.user_id=u.id " +
					"WHERE u.username=? AND (b.cover_url IS NULL OR b.cover_url='') LIMIT 50");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
            GoogleBooksClient gb = new GoogleBooksClient();
            OpenLibraryClient ol = new OpenLibraryClient();
			while (rs.next() && updated < maxUpdates) {
				int id = rs.getInt(1);
				String title = rs.getString(2);
				String author = rs.getString(3);
				String query = author == null || author.isEmpty() ? title : (title + " " + author);
                try {
                    // Try OpenLibrary first
                    String thumb = ol.findCoverUrl(title, author);
                    if (thumb == null || thumb.isEmpty()) {
                        // Fallback to Google Books
                        List<GoogleBooksClient.Volume> vols = gb.search(query, 1);
                        if (!vols.isEmpty()) thumb = vols.get(0).thumbnail;
                    }
                    if (thumb != null && !thumb.isEmpty()) {
                        PreparedStatement up = conn.prepareStatement("UPDATE books SET cover_url=? WHERE id=?");
                        up.setString(1, thumb);
                        up.setInt(2, id);
                        up.executeUpdate();
                        updated++;
                    }
                } catch (Exception ignored) {}
			}
		} catch (Exception ignored) {}
		return updated;
	}
}


