package ui;

import javax.swing.*;
import utils.DBInit;

public class App {
    public static void main(String[] args) {
        boolean dbReady = true;
        try {
            DBInit.initialize();
        } catch (Exception e) {
            dbReady = false;
        }

        SwingUtilities.invokeLater(() -> {
            if (!dbReady) new DBSetupFrame().setVisible(true);
            else {
                LoginFrame login = new LoginFrame() {
                    @Override
                    protected void onLoginSuccess() {
                        MainFrame main = new MainFrame();
                        main.setVisible(true);
                        this.dispose(); // close login
                    }
                };
                login.setVisible(true);
            }
        });
    }
}
