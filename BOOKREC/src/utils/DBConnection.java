package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	public static Connection getConnection() {
		try {
			DBConfig.Values v = DBConfig.loadOrDefaults();
			Class.forName(v.driver);
			return DriverManager.getConnection(v.url, v.user, v.pass);
		} catch (Exception e) {
			throw new RuntimeException("Failed to obtain DB connection: " + e.getMessage(), e);
		}
	}
}


