package ui;

import javax.swing.*;
import java.awt.*;

public final class UiStyles {

    // ===================== COLORS =====================
    // Main backgrounds
    public static final Color BG = new Color(225, 225, 200);         // soft beige background
    public static final Color CARD_BG = new Color(245, 245, 245);    // card/panel background
    public static final Color INPUT_BG = new Color(240, 240, 240);   // input fields

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(30, 30, 30);  // almost black
    public static final Color TEXT_SECONDARY = new Color(90, 90, 90); // grayish info text
    public static final Color TEXT_PLACEHOLDER = new Color(150, 150, 150); // placeholder style

    // Buttons
    public static final Color PRIMARY_BTN_BG = new Color(29, 185, 84); // green
    public static final Color PRIMARY_BTN_FG = Color.WHITE;
    public static final Color SECONDARY_BTN_BG = new Color(70, 96, 118); // dark blue
    public static final Color SECONDARY_BTN_FG = Color.WHITE;

    // ===================== FONTS =====================
    public static final Font HEADING1 = new Font("Georgia", Font.BOLD, 28);
    public static final Font HEADING2 = new Font("Arial", Font.PLAIN, 18);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);

    // ===================== COMPONENT STYLES =====================
    // Headings
    public static JLabel heading1(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(HEADING1);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel heading2(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(HEADING2);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    // Labels
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel secondaryLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    // Text fields
    public static JTextField textField(int columns) {
        JTextField f = new JTextField(columns);
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        f.setFont(INPUT_FONT);
        f.setForeground(TEXT_PRIMARY);
        return f;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        f.setFont(INPUT_FONT);
        f.setForeground(TEXT_PRIMARY);
        return f;
    }

    // Buttons
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PRIMARY_BTN_BG);
        b.setForeground(PRIMARY_BTN_FG);
        b.setFont(BUTTON_FONT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(SECONDARY_BTN_BG);
        b.setForeground(SECONDARY_BTN_FG);
        b.setFont(BUTTON_FONT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return b;
    }

    // Cards / Panels with rounded corners
    public static JPanel cardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        return p;
    }

    // Utility panels
    public static JPanel verticalSpacer(int height) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, height));
        return p;
    }

    public static JPanel horizontalSpacer(int width) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(width, 0));
        return p;
    }

}
