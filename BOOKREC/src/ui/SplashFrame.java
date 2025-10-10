package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

public class SplashFrame extends JFrame {
	private Timer timer;
	private int animationStep = 0;
	private final int totalSteps = 60;

	public SplashFrame() {
		super("The Book Shelf");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		setSize(400, 500);
		setLocationRelativeTo(null);
		setBackground(new Color(230, 230, 216)); // light beige

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Draw bookshelf SVG
				int centerX = getWidth() / 2;
				int shelfY = 150;
				int bookWidth = 20;
				int bookHeight = 80;

				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(Color.BLACK);

				// Books on shelf
				drawBook(g2d, centerX - 40, shelfY, bookWidth, bookHeight);
				drawBook(g2d, centerX - 20, shelfY, bookWidth, bookHeight);
				drawBook(g2d, centerX, shelfY, bookWidth, bookHeight);
				
				// Tilted books
				AffineTransform old = g2d.getTransform();
				g2d.rotate(Math.toRadians(5), centerX + 20, shelfY + bookHeight/2);
				drawBook(g2d, centerX + 10, shelfY - 5, bookWidth, bookHeight - 10);
				g2d.setTransform(old);

				g2d.rotate(Math.toRadians(-5), centerX + 40, shelfY + bookHeight/2);
				drawBook(g2d, centerX + 30, shelfY + 5, bookWidth, bookHeight + 10);
				g2d.setTransform(old);
			}

			private void drawBook(Graphics2D g2d, int x, int y, int w, int h) {
				g2d.drawRect(x, y, w, h);
				// Book spine details
				g2d.drawLine(x + w/2, y, x + w/2, y + h);
			}
		};

		panel.setBackground(new Color(230, 230, 216));
		panel.setLayout(new BorderLayout());

		// Title
		JLabel title = new JLabel("THE BOOK SHELF", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(Color.BLACK);
		title.setBorder(BorderFactory.createEmptyBorder(250, 0, 10, 0));

		JLabel subtitle = new JLabel("EST 2025", SwingConstants.CENTER);
		subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
		subtitle.setForeground(new Color(51, 51, 51));
		subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));

		panel.add(title, BorderLayout.CENTER);
		panel.add(subtitle, BorderLayout.SOUTH);

		add(panel);

		// Animation timer
		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				animationStep++;
				repaint();
				if (animationStep >= totalSteps) {
					timer.stop();
					// Transition to login after animation
					SwingUtilities.invokeLater(() -> {
						new LoginFrame().setVisible(true);
						dispose();
					});
				}
			}
		});
		timer.start();
	}
}


