package view;

import controller.ProductController;
import controller.SupplierController;
import model.Product;
import model.Supplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ProductView – Add, update, delete, and view products.
 * Demonstrates OOP: Separation of Concerns, Encapsulation.
 */
public class ProductView extends JPanel {

    private final ProductController  productCtrl  = new ProductController();
    private final SupplierController supplierCtrl = new SupplierController();

    // ── Form fields ──────────────────────────────────────────────────────────────
    private JTextField idField, nameField, qtyField, priceField;
    private JComboBox<String> supplierCombo;

    // ── Table ────────────────────────────────────────────────────────────────────
    private JTable           table;
    private DefaultTableModel tableModel;

    // ── Buttons ──────────────────────────────────────────────────────────────────
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, refreshBtn;

    // ── Status ───────────────────────────────────────────────────────────────────
    private JLabel statusLabel;

    public ProductView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        loadProducts();
        loadSuppliers();
    }

    // ── Build UI ─────────────────────────────────────────────────────────────────

    private void buildUI() {
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PANEL);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85)),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel title = UITheme.titleLabel("📦  Product Management");
        wrapper.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_PANEL);
        form.setBorder(new EmptyBorder(12, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField    = UITheme.styledField("Auto-generated");
        nameField  = UITheme.styledField("Product name");
        qtyField   = UITheme.styledField("0");
        priceField = UITheme.styledField("0.00");
        supplierCombo = new JComboBox<>();
        supplierCombo.setBackground(UITheme.BG_DARK);
        supplierCombo.setForeground(UITheme.TEXT_PRIMARY);
        supplierCombo.setFont(UITheme.FONT_INPUT);
        supplierCombo.setPreferredSize(new Dimension(200, UITheme.INPUT_HEIGHT));

        addFormRow(form, gbc, 0, "Product ID:", idField);
        addFormRow(form, gbc, 1, "Name:",        nameField);
        addFormRow(form, gbc, 2, "Quantity:",    qtyField);
        addFormRow(form, gbc, 3, "Price (₹):",   priceField);
        addFormRow(form, gbc, 4, "Supplier:",    supplierCombo);

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buildButtonPanel(), BorderLayout.SOUTH);
        return wrapper;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
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

        addBtn     = UITheme.primaryButton("Add Product");
        updateBtn  = UITheme.primaryButton("Update");
        deleteBtn  = UITheme.dangerButton("Delete");
        clearBtn   = UITheme.successButton("Clear");
        refreshBtn = UITheme.primaryButton("🔄 Refresh");

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(refreshBtn);

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> loadProducts());

        return panel;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Product ID", "Name", "Quantity", "Price (₹)", "Supplier ID", "Stock Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                populateFormFromTable(table.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);

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
        Product product = buildProductFromForm();
        if (product == null) return;

        product.setProductId("");  // let controller generate ID
        setStatus("Adding product…", UITheme.TEXT_SECONDARY);

        runAsync(() -> productCtrl.addProduct(product), success -> {
            if (success) { setStatus("Product added!", UITheme.SUCCESS); loadProducts(); clearForm(); }
            else          setStatus("Failed to add product.", UITheme.DANGER);
        });
    }

    private void handleUpdate() {
        if (idField.getText().trim().isEmpty()) {
            setStatus("Select a product from the table first.", UITheme.WARNING);
            return;
        }
        Product product = buildProductFromForm();
        if (product == null) return;

        runAsync(() -> productCtrl.updateProduct(product), success -> {
            if (success) { setStatus("Product updated!", UITheme.SUCCESS); loadProducts(); clearForm(); }
            else          setStatus("Failed to update product.", UITheme.DANGER);
        });
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { setStatus("Select a product first.", UITheme.WARNING); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete product " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        runAsync(() -> productCtrl.deleteProduct(id), success -> {
            if (success) { setStatus("Product deleted.", UITheme.SUCCESS); loadProducts(); clearForm(); }
            else          setStatus("Failed to delete product.", UITheme.DANGER);
        });
    }

    private void loadProducts() {
        setStatus("Loading products…", UITheme.TEXT_SECONDARY);
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            protected List<Product> doInBackground() { return productCtrl.getAllProducts(); }
            protected void done() {
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);
                    for (Product p : products) {
                        String stockStatus = p.isLowStock() ? "⚠ Low Stock" : "✓ OK";
                        tableModel.addRow(new Object[]{
                            p.getProductId(), p.getName(), p.getQuantity(),
                            String.format("%.2f", p.getPrice()), p.getSupplierId(), stockStatus
                        });
                    }
                    setStatus("Loaded " + products.size() + " products.", UITheme.TEXT_SECONDARY);
                } catch (Exception e) {
                    setStatus("Error loading products.", UITheme.DANGER);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadSuppliers() {
        new Thread(() -> {
            List<Supplier> suppliers = supplierCtrl.getAllSuppliers();
            SwingUtilities.invokeLater(() -> {
                supplierCombo.removeAllItems();
                supplierCombo.addItem("-- Select Supplier --");
                for (Supplier s : suppliers) {
                    supplierCombo.addItem(s.getSupplierId() + " | " + s.getName());
                }
            });
        }).start();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private Product buildProductFromForm() {
        String name  = nameField.getText().trim();
        String qtyS  = qtyField.getText().trim();
        String priceS = priceField.getText().trim();

        if (name.isEmpty()) { setStatus("Name is required.", UITheme.WARNING); return null; }

        int qty;
        double price;
        try { qty   = Integer.parseInt(qtyS); }
        catch (NumberFormatException e) { setStatus("Quantity must be a number.", UITheme.WARNING); return null; }
        try { price = Double.parseDouble(priceS); }
        catch (NumberFormatException e) { setStatus("Price must be a number.", UITheme.WARNING); return null; }

        String supplierId = "";
        if (supplierCombo.getSelectedIndex() > 0) {
            String selected = (String) supplierCombo.getSelectedItem();
            supplierId = selected != null ? selected.split(" \\| ")[0] : "";
        }

        return new Product(idField.getText().trim(), name, qty, price, supplierId);
    }

    private void populateFormFromTable(int row) {
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        qtyField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        priceField.setText((String) tableModel.getValueAt(row, 3));

        // Sync supplier combo to the product's supplier
        String supplierId = (String) tableModel.getValueAt(row, 4);
        if (supplierId != null && !supplierId.isEmpty()) {
            for (int i = 1; i < supplierCombo.getItemCount(); i++) {
                String item = supplierCombo.getItemAt(i);
                if (item != null && item.startsWith(supplierId + " |")) {
                    supplierCombo.setSelectedIndex(i);
                    return;
                }
            }
        }
        supplierCombo.setSelectedIndex(0);
    }

    private void clearForm() {
        idField.setText(""); nameField.setText("");
        qtyField.setText("0"); priceField.setText("0.00");
        supplierCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setForeground(color);
        });
    }

    /** Runs a boolean task async; calls callback on EDT with result. */
    private void runAsync(java.util.concurrent.Callable<Boolean> task,
                          java.util.function.Consumer<Boolean> callback) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            protected Boolean doInBackground() throws Exception { return task.call(); }
            protected void done() {
                try { callback.accept(get()); }
                catch (Exception e) { callback.accept(false); e.printStackTrace(); }
            }
        };
        worker.execute();
    }
}
