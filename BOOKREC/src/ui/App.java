package ui;

import javax.swing.*;
import utils.DBInit;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            boolean dbReady;
            try {
                DBInit.initialize();
                dbReady = true;
            } catch (Exception e) {
                dbReady = false;
                e.printStackTrace();
            }

            if (!dbReady) {
                DBSetupFrame dbSetup = new DBSetupFrame();
                dbSetup.setVisible(true);
            } else {
                LoginFrame login = new LoginFrame() {
                    @Override
                    protected void onLoginSuccess() {
                        MainFrame main = new MainFrame();
                        main.setVisible(true);
                        this.dispose(); // close login frame
                    }
                };
                login.setVisible(true);
            }
        });
    }
}
