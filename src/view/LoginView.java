package view;

import controller.LoginController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * LoginView – Redesigned with a modern Split-Pane layout.
 * Simplified as per user request (removed remember me / forgot pass).
 */
public class LoginView extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JLabel         statusLabel;

    private final LoginController loginController = new LoginController();

    public LoginView() {
        initUI();
        seedAdmin();
    }

    private void initUI() {
        setTitle("IMS | Secure Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        // ── Root ────────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // ── Left Side ───────────────────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(UITheme.BG_PANEL);
        
        JLabel imageLabel = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/resources/login_banner.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(500, 650, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        // ── Right Side ──────────────────────────────────────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UITheme.BG_DARK);
        
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBackground(UITheme.BG_DARK);
        formWrapper.setMaximumSize(new Dimension(380, 520));
        formWrapper.setPreferredSize(new Dimension(380, 520));

        JLabel logo = new JLabel("📦");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(UITheme.TEXT_PRIMARY);
        
        JLabel subtitle = new JLabel("Enter your credentials to manage inventory");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        Dimension fieldSize = new Dimension(350, 45);
        JLabel userLbl = UITheme.headerLabel("Username");
        usernameField = UITheme.styledField("Enter your username");
        usernameField.setMaximumSize(fieldSize);

        JLabel passLbl = UITheme.headerLabel("Password");
        passwordField = UITheme.styledPasswordField();
        passwordField.setMaximumSize(fieldSize);

        loginButton = UITheme.primaryButton("SIGN IN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setMaximumSize(fieldSize);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);

        // Exit Button
        JButton exitBtn = new JButton("✕");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        exitBtn.setForeground(UITheme.TEXT_SECONDARY);
        exitBtn.setBorder(null);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> System.exit(0));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(UITheme.BG_DARK);
        topBar.add(exitBtn);

        // Align all to left
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formWrapper.add(logo);
        formWrapper.add(Box.createVerticalStrut(15));
        formWrapper.add(title);
        formWrapper.add(Box.createVerticalStrut(5));
        formWrapper.add(subtitle);
        formWrapper.add(Box.createVerticalStrut(50));
        formWrapper.add(userLbl);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(usernameField);
        formWrapper.add(Box.createVerticalStrut(25));
        formWrapper.add(passLbl);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(passwordField);
        formWrapper.add(Box.createVerticalStrut(45));
        formWrapper.add(loginButton);
        formWrapper.add(Box.createVerticalStrut(20));
        formWrapper.add(statusLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.anchor = GridBagConstraints.NORTHEAST;
        rightPanel.add(topBar, gbc);

        gbc.gridy = 1; gbc.weighty = 1; gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(formWrapper, gbc);

        root.add(leftPanel);
        root.add(rightPanel);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        
        MouseAdapter ma = new MouseAdapter() {
            private Point mouseDownCompCoords = null;
            @Override
            public void mouseReleased(MouseEvent e) { mouseDownCompCoords = null; }
            @Override
            public void mousePressed(MouseEvent e) { mouseDownCompCoords = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        statusLabel.setText(" ");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() { return loginController.authenticate(username, password); }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        new DashboardView(user).setVisible(true);
                    } else {
                        statusLabel.setText("Invalid username or password.");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Connection failed.");
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                }
            }
        };
        worker.execute();
    }

    private void seedAdmin() {
        new Thread(() -> loginController.seedDefaultAdmin()).start();
    }
}
