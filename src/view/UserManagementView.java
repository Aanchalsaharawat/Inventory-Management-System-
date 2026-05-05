package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * UserManagementView – Admin-only panel to add and remove system users.
 */
public class UserManagementView extends JPanel {

    private final UserController userCtrl = new UserController();

    private JTextField idField, usernameField, passwordField;
    private JComboBox<String> roleCombo;
    private JButton addBtn, deleteBtn, clearBtn, refreshBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public UserManagementView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PANEL);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 100)),
            new EmptyBorder(16, 20, 16, 20)
        ));
        wrapper.add(UITheme.titleLabel("👤  User Management"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_PANEL);
        form.setBorder(new EmptyBorder(12, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField      = UITheme.styledField("Auto-generated");
        usernameField = UITheme.styledField("Username");
        passwordField = UITheme.styledField("Password");
        roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        roleCombo.setBackground(UITheme.BG_DARK);
        roleCombo.setForeground(UITheme.TEXT_PRIMARY);
        roleCombo.setFont(UITheme.FONT_INPUT);
        roleCombo.setPreferredSize(new Dimension(200, UITheme.INPUT_HEIGHT));

        addRow(form, gbc, 0, "User ID:",   idField);
        addRow(form, gbc, 1, "Username:",  usernameField);
        addRow(form, gbc, 2, "Password:",  passwordField);
        addRow(form, gbc, 3, "Role:",      roleCombo);

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buildButtonPanel(), BorderLayout.SOUTH);
        return wrapper;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = UITheme.headerLabel(label);
        lbl.setPreferredSize(new Dimension(100, 28));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBackground(UITheme.BG_PANEL);

        addBtn     = UITheme.primaryButton("Add User");
        deleteBtn  = UITheme.dangerButton("Delete");
        clearBtn   = UITheme.successButton("Clear");
        refreshBtn = UITheme.primaryButton("🔄 Refresh");

        panel.add(addBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(refreshBtn);

        addBtn.addActionListener(e -> handleAdd());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> loadUsers());
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"User ID", "Username", "Role"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                idField.setText((String) tableModel.getValueAt(row, 0));
                usernameField.setText((String) tableModel.getValueAt(row, 1));
                roleCombo.setSelectedItem(tableModel.getValueAt(row, 2));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        statusLabel = UITheme.bodyLabel("Ready.");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(UITheme.BG_DARK);
        panel.add(statusLabel);
        return panel;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    private void handleAdd() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = (String) roleCombo.getSelectedItem();

        if (username.isEmpty()) { setStatus("Username is required.", UITheme.WARNING); return; }
        if (password.isEmpty()) { setStatus("Password is required.", UITheme.WARNING); return; }

        String userId = idField.getText().trim();
        User user = new User(userId, username, password);
        user.setRole(role);
        setStatus("Adding user…", UITheme.TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() { return userCtrl.addUser(user); }
            protected void done() {
                try {
                    if (get()) { setStatus("User added!", UITheme.SUCCESS); loadUsers(); clearForm(); }
                    else        setStatus("Failed to add user.", UITheme.DANGER);
                } catch (Exception e) { setStatus("Error.", UITheme.DANGER); e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { setStatus("Select a user first.", UITheme.WARNING); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete user " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() { return userCtrl.deleteUser(id); }
            protected void done() {
                try {
                    if (get()) { setStatus("User deleted.", UITheme.SUCCESS); loadUsers(); clearForm(); }
                    else        setStatus("Failed to delete.", UITheme.DANGER);
                } catch (Exception e) { setStatus("Error.", UITheme.DANGER); }
            }
        };
        worker.execute();
    }

    private void loadUsers() {
        setStatus("Loading…", UITheme.TEXT_SECONDARY);
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            protected List<User> doInBackground() { return userCtrl.getAllUsers(); }
            protected void done() {
                try {
                    List<User> list = get();
                    tableModel.setRowCount(0);
                    for (User u : list) {
                        tableModel.addRow(new Object[]{ u.getUserId(), u.getUsername(), u.getRole() });
                    }
                    setStatus("Loaded " + list.size() + " users.", UITheme.TEXT_SECONDARY);
                } catch (Exception e) { setStatus("Error loading users.", UITheme.DANGER); }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        idField.setText(""); usernameField.setText(""); passwordField.setText("");
        roleCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(color); });
    }
}
