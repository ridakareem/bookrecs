package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DBConfig {
	private static final Path PROPS_PATH = Path.of("db.properties");

	public static class Values {
		public String driver;
		public String url;
		public String user;
		public String pass;
	}

	public static Values loadOrDefaults() {
		Values v = new Values();
		Properties p = new Properties();
		try {
			if (Files.exists(PROPS_PATH)) {
				try (FileInputStream in = new FileInputStream(PROPS_PATH.toFile())) {
					p.load(in);
				}
			}
		} catch (Exception ignored) {}
		v.driver = p.getProperty("driver", "com.mysql.cj.jdbc.Driver");
		v.url = p.getProperty("url", "jdbc:mysql://127.0.0.1:3306/bookdiary?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
		v.user = p.getProperty("user", "root");
		v.pass = p.getProperty("pass", "");
		return v;
	}

	public static void save(Values v) throws Exception {
		Properties p = new Properties();
		p.setProperty("driver", v.driver);
		p.setProperty("url", v.url);
		p.setProperty("user", v.user);
		p.setProperty("pass", v.pass);
		try (FileOutputStream out = new FileOutputStream(PROPS_PATH.toFile())) {
			p.store(out, "Database configuration");
		}
	}
}


