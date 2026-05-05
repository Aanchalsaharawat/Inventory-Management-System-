package view;

import controller.SupplierController;
import model.Supplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * SupplierView – Add and view suppliers.
 */
public class SupplierView extends JPanel {

    private final SupplierController supplierCtrl = new SupplierController();

    private JTextField idField, nameField, contactField;
    private JButton addBtn, deleteBtn, clearBtn, refreshBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public SupplierView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        loadSuppliers();
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
        wrapper.add(UITheme.titleLabel("🏭  Supplier Management"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_PANEL);
        form.setBorder(new EmptyBorder(12, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField      = UITheme.styledField("Auto-generated");
        nameField    = UITheme.styledField("Supplier name");
        contactField = UITheme.styledField("Phone / Email");

        addRow(form, gbc, 0, "Supplier ID:", idField);
        addRow(form, gbc, 1, "Name:",         nameField);
        addRow(form, gbc, 2, "Contact:",      contactField);

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

        addBtn     = UITheme.primaryButton("Add Supplier");
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
        refreshBtn.addActionListener(e -> loadSuppliers());
        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Supplier ID", "Name", "Contact"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                idField.setText((String) tableModel.getValueAt(row, 0));
                nameField.setText((String) tableModel.getValueAt(row, 1));
                contactField.setText((String) tableModel.getValueAt(row, 2));
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
        String name    = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty()) { setStatus("Supplier name is required.", UITheme.WARNING); return; }

        Supplier supplier = new Supplier("", name, contact);
        setStatus("Adding supplier…", UITheme.TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() { return supplierCtrl.addSupplier(supplier); }
            protected void done() {
                try {
                    if (get()) { setStatus("Supplier added!", UITheme.SUCCESS); loadSuppliers(); clearForm(); }
                    else        setStatus("Failed to add supplier.", UITheme.DANGER);
                } catch (Exception e) { setStatus("Error.", UITheme.DANGER); e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { setStatus("Select a supplier first.", UITheme.WARNING); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() { return supplierCtrl.deleteSupplier(id); }
            protected void done() {
                try {
                    if (get()) { setStatus("Supplier deleted.", UITheme.SUCCESS); loadSuppliers(); clearForm(); }
                    else        setStatus("Failed to delete.", UITheme.DANGER);
                } catch (Exception e) { setStatus("Error.", UITheme.DANGER); }
            }
        };
        worker.execute();
    }

    private void loadSuppliers() {
        setStatus("Loading…", UITheme.TEXT_SECONDARY);
        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            protected List<Supplier> doInBackground() { return supplierCtrl.getAllSuppliers(); }
            protected void done() {
                try {
                    List<Supplier> list = get();
                    tableModel.setRowCount(0);
                    for (Supplier s : list) {
                        tableModel.addRow(new Object[]{ s.getSupplierId(), s.getName(), s.getContact() });
                    }
                    setStatus("Loaded " + list.size() + " suppliers.", UITheme.TEXT_SECONDARY);
                } catch (Exception e) { setStatus("Error loading.", UITheme.DANGER); }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        idField.setText(""); nameField.setText(""); contactField.setText("");
        table.clearSelection();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(color); });
    }
}
