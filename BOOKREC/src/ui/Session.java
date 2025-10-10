package ui;

public final class Session {
	private static volatile String currentUsername;

	private Session() {}

	public static void setCurrentUsername(String username) {
		currentUsername = username;
	}

	public static String getCurrentUsername() {
		return currentUsername;
	}
}


