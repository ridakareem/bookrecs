package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageLoader {
	private static final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

	private ImageLoader() {}

	public static ImageIcon loadScaled(String urlString, int width, int height) throws Exception {
		if (urlString == null || urlString.isEmpty()) return null;
		ImageIcon cached = cache.get(urlString);
		if (cached != null) return cached;
		URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(8000);
		conn.setReadTimeout(8000);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (BookRec/1.0)");
		try (InputStream in = conn.getInputStream()) {
			BufferedImage src = ImageIO.read(in);
			if (src == null) return null;
			Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(scaled);
			cache.put(urlString, icon);
			return icon;
		}
	}
}


