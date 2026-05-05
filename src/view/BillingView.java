package view;

import controller.BillingController;
import controller.ProductController;
import model.Product;
import model.Sale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * BillingView – Select product, enter quantity, generate bill.
 * Demonstrates OOP: Abstraction (delegates to BillingController).
 */
public class BillingView extends JPanel {

    private final ProductController  productCtrl  = new ProductController();
    private final BillingController  billingCtrl  = new BillingController();

    private JComboBox<String> productCombo;
    private JTextField qtyField, unitPriceField, totalField;
    private JButton calculateBtn, confirmBtn, refreshBtn;
    private JTextArea receiptArea;
    private JLabel statusLabel, stockLabel;

    private List<Product> productList;
    private Product selectedProduct;

    public BillingView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        loadProducts();
    }

    private void buildUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildReceiptPanel());
        split.setDividerLocation(480);
        split.setBackground(UITheme.BG_DARK);
        split.setBorder(null);

        add(UITheme.titleLabel("🛒  Billing & Sales"), BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Left Panel (Form) ─────────────────────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_PANEL);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Product selector
        JPanel productRow = new JPanel(new GridBagLayout());
        productRow.setBackground(UITheme.BG_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productCombo = new JComboBox<>();
        productCombo.setBackground(UITheme.BG_DARK);
        productCombo.setForeground(UITheme.TEXT_PRIMARY);
        productCombo.setFont(UITheme.FONT_INPUT);

        unitPriceField = UITheme.styledField("0.00");
        unitPriceField.setEditable(false);

        stockLabel = UITheme.bodyLabel("Stock: —");
        stockLabel.setForeground(UITheme.TEXT_SECONDARY);

        qtyField  = UITheme.styledField("1");
        qtyField.setHorizontalAlignment(JTextField.CENTER);
        qtyField.setPreferredSize(new Dimension(60, UITheme.INPUT_HEIGHT));

        JButton minusBtn = new JButton("−");
        JButton plusBtn  = new JButton("+");
        for (JButton b : new JButton[]{minusBtn, plusBtn}) {
            b.setBackground(UITheme.ACCENT);
            b.setForeground(UITheme.TEXT_PRIMARY);
            b.setFont(UITheme.FONT_BODY);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(50, UITheme.INPUT_HEIGHT));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        qtyPanel.setBackground(UITheme.BG_PANEL);
        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyField);
        qtyPanel.add(plusBtn);

        totalField = UITheme.styledField("0.00");
        totalField.setEditable(false);
        totalField.setForeground(UITheme.SUCCESS);

        addRow(productRow, gbc, 0, "Product:",    productCombo);
        addRow(productRow, gbc, 1, "Unit Price:", unitPriceField);
        addRow(productRow, gbc, 2, "Stock:",      stockLabel);
        addRow(productRow, gbc, 3, "Quantity:",   qtyPanel);
        addRow(productRow, gbc, 4, "Total (₹):",  totalField);

        // Buttons
        calculateBtn = UITheme.primaryButton("Calculate");
        confirmBtn   = UITheme.successButton("Confirm Sale");
        refreshBtn   = UITheme.primaryButton("🔄 Reload");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(UITheme.BG_PANEL);
        btnRow.add(calculateBtn);
        btnRow.add(confirmBtn);
        btnRow.add(refreshBtn);

        panel.add(productRow, BorderLayout.CENTER);
        panel.add(btnRow, BorderLayout.SOUTH);

        // Events
        productCombo.addActionListener(e -> onProductSelected());
        calculateBtn.addActionListener(e -> calculate());
        confirmBtn.addActionListener(e -> confirmSale());
        refreshBtn.addActionListener(e -> resetBilling());
        minusBtn.addActionListener(e -> adjustQty(-1));
        plusBtn.addActionListener(e -> adjustQty(+1));

        return panel;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = UITheme.headerLabel(label);
        lbl.setPreferredSize(new Dimension(110, 28));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    // ── Receipt Panel ─────────────────────────────────────────────────────────────

    private JPanel buildReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_PANEL);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel heading = UITheme.titleLabel("🧾  Receipt");
        panel.add(heading, BorderLayout.NORTH);

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setBackground(UITheme.BG_DARK);
        receiptArea.setForeground(UITheme.TEXT_PRIMARY);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        receiptArea.setText(emptyReceipt());

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        statusLabel = UITheme.bodyLabel("Select a product to begin.");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(UITheme.BG_DARK);
        panel.add(statusLabel);
        return panel;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────────

    private void loadProducts() {
        loadProducts(null);
    }

    private void loadProducts(Runnable onComplete) {
        setStatus("Loading products…", UITheme.TEXT_SECONDARY);
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            protected List<Product> doInBackground() { return productCtrl.getAllProducts(); }
            protected void done() {
                try {
                    productList = get();
                    productCombo.removeAllItems();
                    productCombo.addItem("-- Select Product --");
                    for (Product p : productList) {
                        productCombo.addItem(p.getProductId() + " | " + p.getName() + " (Stock: " + p.getQuantity() + ")");
                    }
                    setStatus("Products loaded.", UITheme.TEXT_SECONDARY);
                    if (onComplete != null) onComplete.run();
                } catch (Exception e) { setStatus("Error loading products.", UITheme.DANGER); }
            }
        };
        worker.execute();
    }

    private void onProductSelected() {
        int idx = productCombo.getSelectedIndex() - 1;
        if (productList == null || idx < 0 || idx >= productList.size()) {
            selectedProduct = null;
            unitPriceField.setText("0.00");
            stockLabel.setText("Stock: —");
            return;
        }
        selectedProduct = productList.get(idx);
        unitPriceField.setText(String.format("%.2f", selectedProduct.getPrice()));
        stockLabel.setText("Stock: " + selectedProduct.getQuantity());
        stockLabel.setForeground(selectedProduct.isLowStock() ? UITheme.WARNING : UITheme.SUCCESS);
        totalField.setText("0.00");
        qtyField.setText("1");
    }

    private void adjustQty(int delta) {
        int qty;
        try { qty = Integer.parseInt(qtyField.getText().trim()); }
        catch (NumberFormatException e) { qty = 1; }
        qty = Math.max(1, qty + delta);
        qtyField.setText(String.valueOf(qty));
    }

    private void calculate() {
        if (selectedProduct == null) { setStatus("Select a product first.", UITheme.WARNING); return; }

        int qty;
        try { qty = Integer.parseInt(qtyField.getText().trim()); }
        catch (NumberFormatException e) { setStatus("Quantity must be a whole number.", UITheme.WARNING); return; }

        if (qty <= 0) { setStatus("Quantity must be > 0.", UITheme.WARNING); return; }
        if (qty > selectedProduct.getQuantity()) {
            setStatus("Insufficient stock! Available: " + selectedProduct.getQuantity(), UITheme.DANGER);
            return;
        }

        double total = billingCtrl.calculateTotal(selectedProduct.getPrice(), qty);
        totalField.setText(String.format("%.2f", total));
        setStatus("Total calculated. Click 'Confirm Sale' to proceed.", UITheme.SUCCESS);
    }

    private void confirmSale() {
        if (selectedProduct == null) { setStatus("Select a product first.", UITheme.WARNING); return; }

        int qty;
        try { qty = Integer.parseInt(qtyField.getText().trim()); }
        catch (NumberFormatException e) { setStatus("Invalid quantity.", UITheme.WARNING); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm sale of " + qty + " x " + selectedProduct.getName() + "?",
            "Confirm Sale", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        confirmBtn.setEnabled(false);
        setStatus("Processing sale…", UITheme.TEXT_SECONDARY);

        final int finalQty = qty;
        SwingWorker<Sale, Void> worker = new SwingWorker<>() {
            protected Sale doInBackground() {
                return billingCtrl.processSale(selectedProduct, finalQty);
            }
            protected void done() {
                try {
                    Sale sale = get();
                    if (sale != null) {
                        receiptArea.setText(generateReceipt(sale));
                        setStatus("Sale completed! Receipt generated.", UITheme.SUCCESS);
                    } else {
                        setStatus("Sale failed. Check stock or Firebase connection.", UITheme.DANGER);
                    }
                } catch (Exception e) {
                    setStatus("Error processing sale.", UITheme.DANGER);
                    e.printStackTrace();
                } finally {
                    confirmBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    // ── Receipt Generation ─────────────────────────────────────────────────────────

    private String generateReceipt(Sale sale) {
        String line = "─".repeat(42);
        return  "      INVENTORY MANAGEMENT SYSTEM\n" +
                "           SALES RECEIPT\n" +
                line + "\n" +
                String.format("Sale ID    : %s%n", sale.getSaleId()) +
                String.format("Date       : %s%n", sale.getDate()) +
                line + "\n" +
                String.format("Product    : %s%n", sale.getProductName()) +
                String.format("Product ID : %s%n", sale.getProductId()) +
                String.format("Quantity   : %d%n", sale.getQuantity()) +
                String.format("Unit Price : ₹ %.2f%n", sale.getTotalPrice() / sale.getQuantity()) +
                line + "\n" +
                String.format("TOTAL      : ₹ %.2f%n", sale.getTotalPrice()) +
                line + "\n" +
                "     Thank you for your purchase!\n";
    }

    private String emptyReceipt() {
        return  "      INVENTORY MANAGEMENT SYSTEM\n" +
                "           SALES RECEIPT\n\n" +
                "  Select a product, enter quantity,\n" +
                "  click Calculate, then Confirm Sale\n" +
                "  to generate a receipt here.\n";
    }

    private void resetBilling() {
        receiptArea.setText(emptyReceipt());
        loadProducts(() -> {
            selectedProduct = null;
            qtyField.setText("1");
            totalField.setText("0.00");
            unitPriceField.setText("0.00");
            stockLabel.setText("Stock: —");
        });
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(color); });
    }
}
