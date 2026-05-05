package view;

import controller.ReportsController;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.Timer;

/**
 * StockView – Current stock levels with low-stock alerting.
 */
public class StockView extends JPanel {

    private final ReportsController reportsCtrl = new ReportsController();

    private JTable           table;
    private DefaultTableModel tableModel;
    private JLabel           statusLabel, lowStockAlertLabel;
    private JButton          refreshBtn;

    public StockView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        // Stagger load to avoid simultaneous Firebase connections at startup
        Timer delay = new Timer(800, e -> loadStock());
        delay.setRepeats(false);
        delay.start();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.add(UITheme.titleLabel("📊  Stock Management"), BorderLayout.WEST);

        refreshBtn = UITheme.primaryButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadStock());
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Alert banner
        lowStockAlertLabel = new JLabel(" ");
        lowStockAlertLabel.setFont(UITheme.FONT_HEADER);
        lowStockAlertLabel.setForeground(UITheme.WARNING);
        lowStockAlertLabel.setOpaque(true);
        lowStockAlertLabel.setBackground(UITheme.BG_PANEL);
        lowStockAlertLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(234, 179, 8, 50)),
            new EmptyBorder(8, 16, 8, 16)
        ));

        // Table
        String[] cols = {"Product ID", "Name", "Quantity", "Price (₹)", "Supplier ID", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(UITheme.BG_DARK);
        center.add(lowStockAlertLabel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Status bar
        statusLabel = UITheme.bodyLabel("Loading stock…");
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(UITheme.BG_DARK);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadStock() {
        setStatus("Loading stock…", UITheme.TEXT_SECONDARY);
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            protected List<Product> doInBackground() { return reportsCtrl.getInventoryStatus(); }
            protected void done() {
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);
                    int lowCount = 0;
                    for (Product p : products) {
                        String status = p.isLowStock() ? "⚠ Low Stock" : "✓ OK";
                        if (p.isLowStock()) lowCount++;
                        tableModel.addRow(new Object[]{
                            p.getProductId(), p.getName(), p.getQuantity(),
                            String.format("%.2f", p.getPrice()), p.getSupplierId(), status
                        });
                    }
                    if (lowCount > 0) {
                        lowStockAlertLabel.setText("  ⚠  Alert: " + lowCount + " product(s) have low stock (< 5 units)!");
                    } else {
                        lowStockAlertLabel.setText(" ");
                    }
                    setStatus("Loaded " + products.size() + " products. Low stock: " + lowCount, UITheme.TEXT_SECONDARY);
                } catch (Exception e) {
                    setStatus("Error loading stock.", UITheme.DANGER);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(color); });
    }
}
