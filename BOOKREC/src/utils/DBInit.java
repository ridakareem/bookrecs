package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public final class DBInit {
	private DBInit() {}

	public static void initialize() throws Exception {
		DBConfig.Values v = DBConfig.loadOrDefaults();
		Class.forName(v.driver);
		String dbName = "bookdiary"; // derive from URL if needed; keep default
		String urlRoot = v.url.replace("/" + dbName, "/");
		String user = v.user;
		String pass = v.pass;

		// 1) Ensure database exists
		try (Connection rootConn = DriverManager.getConnection(urlRoot, user, pass);
			 Statement s = rootConn.createStatement()) {
			s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		}

		// 2) Ensure tables exist in target DB
		String urlDb = v.url;
		try (Connection dbConn = DriverManager.getConnection(urlDb, user, pass);
			 Statement s = dbConn.createStatement()) {
			s.executeUpdate(
					"CREATE TABLE IF NOT EXISTS users (" +
					"id INT AUTO_INCREMENT PRIMARY KEY, " +
					"username VARCHAR(100) UNIQUE NOT NULL, " +
					"password VARCHAR(255) NOT NULL" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

			s.executeUpdate(
					"CREATE TABLE IF NOT EXISTS books (" +
					"id INT AUTO_INCREMENT PRIMARY KEY, " +
					"user_id INT NOT NULL, " +
					"title VARCHAR(255) NOT NULL, " +
					"author VARCHAR(255) NULL, " +
					"cover_url VARCHAR(500) NULL, " +
					"genres VARCHAR(500) NULL, " +
					"rating TINYINT NULL, " +
					"created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
					"FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

			// In case an existing table lacks created_at, add it
			try { s.executeUpdate("ALTER TABLE books ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"); } catch (Exception ignored) {}
			// Add optional columns if missing
			try { s.executeUpdate("ALTER TABLE books ADD COLUMN author VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { s.executeUpdate("ALTER TABLE books ADD COLUMN cover_url VARCHAR(500) NULL"); } catch (Exception ignored) {}
			try { s.executeUpdate("ALTER TABLE books ADD COLUMN genres VARCHAR(500) NULL"); } catch (Exception ignored) {}
			try { s.executeUpdate("ALTER TABLE books ADD COLUMN rating TINYINT NULL"); } catch (Exception ignored) {}
		}
	}
}


