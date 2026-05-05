package view;

import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardView – Main navigation hub after login.
 * Redesigned with a professional Sidebar and CardLayout for modern look.
 */
public class DashboardView extends JFrame {

    private final User currentUser;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String currentView = "Dashboard";

    public DashboardView(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("IMS Dashboard | " + currentUser.getUsername());
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 750));

        // ── Header ───────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 65, 85)));
        header.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel logo = new JLabel("    📦  IMS | Inventory Management System");
        logo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        logo.setForeground(UITheme.ACCENT);

        JLabel userInfo = new JLabel("👤 " + currentUser.getUsername() + "   ");
        userInfo.setFont(UITheme.FONT_HEADER);
        userInfo.setForeground(UITheme.TEXT_SECONDARY);

        JButton logoutBtn = UITheme.dangerButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 34));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightPanel.setBackground(UITheme.BG_PANEL);
        rightPanel.add(userInfo);
        rightPanel.add(logoutBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        // ── Sidebar ──────────────────────────────────────────────────────────────
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.BG_PANEL);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)));

        JLabel navTitle = new JLabel("   MAIN NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(new Color(100, 116, 139));
        navTitle.setBorder(new EmptyBorder(25, 0, 15, 0));

        sidebar.add(navTitle);
        addNavButton("Dashboard", "🏠");
        addNavButton("Products", "📦");
        addNavButton("Suppliers", "🏭");
        addNavButton("Billing", "🛒");
        addNavButton("Stock", "📊");
        addNavButton("Reports", "📑");
        addNavButton("Users", "👤");

        applyRoleControl();

        // ── Main Content Area (CardLayout) ──────────────────────────────────────
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_DARK);

        cardPanel.add(buildHomePanel(), "Dashboard");
        cardPanel.add(new ProductView(), "Products");
        cardPanel.add(new SupplierView(), "Suppliers");
        cardPanel.add(new BillingView(), "Billing");
        cardPanel.add(new StockView(), "Stock");
        cardPanel.add(new ReportsView(), "Reports");
        cardPanel.add(new UserManagementView(), "Users");

        // ── Root ────────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(sidebar, BorderLayout.WEST);
        root.add(cardPanel, BorderLayout.CENTER);
        setContentPane(root);

        setActiveNav("Dashboard");
    }

    private void addNavButton(String name, String icon) {
        // Use HTML to force color emoji font on Windows and NOBR to prevent wrapping
        JButton btn = new JButton("<html><nobr><font face='Segoe UI Emoji'>" + icon + "</font> &nbsp;&nbsp;" + name + "</nobr></html>");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setBackground(UITheme.BG_PANEL);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(240, 50));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!name.equals(currentView)) {
                    btn.setBackground(new Color(45, 55, 75));
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!name.equals(currentView)) {
                    btn.setBackground(UITheme.BG_PANEL);
                    btn.setForeground(UITheme.TEXT_SECONDARY);
                }
            }
        });

        btn.addActionListener(e -> setActiveNav(name));

        sidebar.add(btn);
        navButtons.put(name, btn);
    }

    private void setActiveNav(String name) {
        currentView = name;
        cardLayout.show(cardPanel, name);

        // Update button styles
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton b = entry.getValue();
            if (entry.getKey().equals(name)) {
                b.setBackground(new Color(30, 41, 59));
                b.setForeground(UITheme.ACCENT);
                // Compound border: Indigo line (4px) + Padding (20px)
                b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT),
                    new EmptyBorder(12, 16, 12, 20)
                ));
            } else {
                b.setBackground(UITheme.BG_PANEL);
                b.setForeground(UITheme.TEXT_SECONDARY);
                b.setBorder(new EmptyBorder(12, 20, 12, 20));
            }
        }
    }

    private void applyRoleControl() {
        String role = currentUser.getRole();
        if ("admin".equals(role)) return;

        // Hide non-admin buttons
        String[] restricted = {"Suppliers", "Stock", "Reports", "Users"};
        for (String r : restricted) {
            JButton b = navButtons.get(r);
            if (b != null) b.setVisible(false);
        }
    }

    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BG_DARK);
        content.setBorder(new EmptyBorder(60, 60, 60, 60));

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getUsername() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(UITheme.TEXT_PRIMARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Everything looks good. Here are your quick actions.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sub.setForeground(UITheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(0, 3, 30, 30));
        grid.setBackground(UITheme.BG_DARK);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(1000, 600));

        boolean isAdmin = "admin".equals(currentUser.getRole());
        
        grid.add(navCard("📦", "Products", "Manage Inventory", "Products"));
        if (isAdmin) grid.add(navCard("🏭", "Suppliers", "Vendor List", "Suppliers"));
        grid.add(navCard("🛒", "Billing", "New Invoice", "Billing"));
        if (isAdmin) {
            grid.add(navCard("📊", "Stock", "Check Status", "Stock"));
            grid.add(navCard("📑", "Reports", "Analytics", "Reports"));
            grid.add(navCard("👤", "Users", "Permissions", "Users"));
        }

        content.add(welcome);
        content.add(Box.createVerticalStrut(12));
        content.add(sub);
        content.add(Box.createVerticalStrut(60));
        content.add(grid);

        panel.add(content, BorderLayout.NORTH);
        return panel;
    }

    private JPanel navCard(String emoji, String title, String desc, String targetView) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(UITheme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Use HTML to force color emoji font on Windows
        JLabel emojiLbl = new JLabel("<html><font face='Segoe UI Emoji'>" + emoji + "</font></html>");
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLbl.setForeground(UITheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UITheme.BG_PANEL);
        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(descLbl);

        card.add(emojiLbl, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(UITheme.BG_CARD);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.ACCENT, 1),
                    new EmptyBorder(30, 30, 30, 30)
                ));
                textPanel.setBackground(UITheme.BG_CARD);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(UITheme.BG_PANEL);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                    new EmptyBorder(30, 30, 30, 30)
                ));
                textPanel.setBackground(UITheme.BG_PANEL);
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setActiveNav(targetView);
            }
        });

        return card;
    }
}
