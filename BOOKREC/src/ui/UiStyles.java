package ui;

import javax.swing.*;
import java.awt.*;

public final class UiStyles {
	public static final Color BG = new Color(230, 231, 213); // #e6e7d5
	public static final Color CARD_BG = new Color(245, 243, 243); // #f5f3f3
	public static final Color INPUT_BG = new Color(217, 216, 216); // #d9d8d8
	public static final Color BLACK = Color.BLACK;

	public static JLabel heading1(String text) {
		JLabel l = new JLabel(text, SwingConstants.CENTER);
		l.setFont(new Font("Georgia", Font.BOLD, 28));
		l.setForeground(BLACK);
		return l;
	}

	public static JLabel heading2(String text) {
		JLabel l = new JLabel(text, SwingConstants.CENTER);
		l.setFont(new Font("Arial", Font.PLAIN, 18));
		l.setForeground(BLACK);
		return l;
	}

	public static JTextField textField(int columns) {
		JTextField f = new JTextField(columns);
		f.setBackground(INPUT_BG);
		f.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		f.setFont(new Font("Arial", Font.PLAIN, 16));
		return f;
	}

	public static JPasswordField passwordField(int columns) {
		JPasswordField f = new JPasswordField(columns);
		f.setBackground(INPUT_BG);
		f.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		f.setFont(new Font("Arial", Font.PLAIN, 16));
		return f;
	}

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(CARD_BG);
        b.setForeground(BLACK);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLACK, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        return b;
    }

	public static JLabel label(String text) {
		JLabel l = new JLabel(text);
		l.setFont(new Font("Arial", Font.BOLD, 14));
		return l;
	}

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
}


