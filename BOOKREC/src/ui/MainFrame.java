package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel content;

    public MainFrame() {
        super("The Book Shelf");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Navigation toolbar
        JToolBar nav = new JToolBar();
        nav.setFloatable(false);
        nav.setBackground(new Color(230, 230, 216));
        nav.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton addBtn = createStyledButton("Add Book");
        JButton viewBtn = createStyledButton("View Books");
        JButton searchBtn = createStyledButton("Search Online");
        JButton recommendBtn = createStyledButton("Get Recommendations");

        nav.add(addBtn);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(viewBtn);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(searchBtn);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(recommendBtn);
        add(nav, BorderLayout.NORTH);

        // Panels
        cardLayout = new CardLayout();
        content = new JPanel(cardLayout);

        // Create panels
        ViewBooksPanel viewPanel = new ViewBooksPanel();
        AddBookPanel addPanel = new AddBookPanel(viewPanel);
        SearchBooksPanel searchPanel = new SearchBooksPanel(viewPanel); // pass viewPanel
        RecommendationPanel recommendPanel = new RecommendationPanel(viewPanel);

        // Add panels to card layout
        content.add(addPanel, "ADD");
        content.add(viewPanel, "VIEW");
        content.add(searchPanel, "SEARCH");
        content.add(recommendPanel, "RECOMMEND");

        add(content, BorderLayout.CENTER);

        // Button actions
        addBtn.addActionListener(e -> cardLayout.show(content, "ADD"));
        viewBtn.addActionListener(e -> {
            viewPanel.refresh(); // refresh when opening view
            cardLayout.show(content, "VIEW");
        });
        searchBtn.addActionListener(e -> cardLayout.show(content, "SEARCH"));
        recommendBtn.addActionListener(e -> cardLayout.show(content, "RECOMMEND"));
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setBackground(new Color(70, 96, 118));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        return btn;
    }
}
