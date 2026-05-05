package view;

import controller.ReportsController;
import model.Product;
import model.Sale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.Timer;

/**
 * ReportsView – Daily sales and full inventory report.
 * Demonstrates OOP: Polymorphism (JTabbedPane switching report panels).
 */
public class ReportsView extends JPanel {

    private final ReportsController reportsCtrl = new ReportsController();

    // Sales tab
    private DefaultTableModel salesModel;
    private JLabel            dailyRevenueLabel;

    // Inventory tab
    private DefaultTableModel inventoryModel;
    private JLabel            totalItemsLabel;

    private JLabel statusLabel;

    public ReportsView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        // Delay initial load slightly so the Dashboard and Products tabs
        // get their Firebase calls in first (avoids connection pool exhaustion).
        Timer delay = new Timer(1500, e -> loadAll());
        delay.setRepeats(false);
        delay.start();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.add(UITheme.titleLabel("📑  Reports"), BorderLayout.WEST);

        JButton refreshBtn = UITheme.primaryButton("🔄 Refresh All");
        refreshBtn.addActionListener(e -> loadAll());
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_PANEL);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_HEADER);

        tabs.addTab("📅 Daily Sales",   buildSalesPanel());
        tabs.addTab("📦 Inventory Status", buildInventoryPanel());

        add(tabs, BorderLayout.CENTER);

        statusLabel = UITheme.bodyLabel("Loading reports…");
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(UITheme.BG_DARK);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ── Sales Panel ───────────────────────────────────────────────────────────────

    private JPanel buildSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Revenue summary card
        JPanel summaryCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        summaryCard.setBackground(UITheme.BG_CARD);
        summaryCard.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));

        dailyRevenueLabel = new JLabel("Today's Revenue: ₹ 0.00");
        dailyRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dailyRevenueLabel.setForeground(UITheme.SUCCESS);
        summaryCard.add(dailyRevenueLabel);

        // Table
        String[] cols = {"Sale ID", "Product", "Product ID", "Qty", "Total (₹)", "Date"};
        salesModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable salesTable = new JTable(salesModel);
        UITheme.styleTable(salesTable);

        JScrollPane scroll = new JScrollPane(salesTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));

        panel.add(summaryCard, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Inventory Panel ───────────────────────────────────────────────────────────

    private JPanel buildInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel summaryCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        summaryCard.setBackground(UITheme.BG_CARD);
        summaryCard.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));

        totalItemsLabel = new JLabel("Total Products: 0  |  Low Stock: 0");
        totalItemsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalItemsLabel.setForeground(UITheme.TEXT_PRIMARY);
        summaryCard.add(totalItemsLabel);

        String[] cols = {"Product ID", "Name", "Quantity", "Price (₹)", "Supplier ID", "Status"};
        inventoryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable invTable = new JTable(inventoryModel);
        UITheme.styleTable(invTable);

        JScrollPane scroll = new JScrollPane(invTable);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100)));

        panel.add(summaryCard, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────────

    private void loadAll() {
        setStatus("Loading reports…", UITheme.TEXT_SECONDARY);
        // Run sequentially: sales first, then inventory after sales done
        // to avoid hammering Firebase with parallel connections.
        loadSalesReport();
    }

    private void loadSalesReport() {
        SwingWorker<List<Sale>, Void> worker = new SwingWorker<>() {
            protected List<Sale> doInBackground() { return reportsCtrl.getDailySales(); }
            protected void done() {
                try {
                    List<Sale> sales = get();
                    salesModel.setRowCount(0);
                    double revenue = 0;
                    for (Sale s : sales) {
                        salesModel.addRow(new Object[]{
                            s.getSaleId(), s.getProductName(), s.getProductId(),
                            s.getQuantity(), String.format("%.2f", s.getTotalPrice()), s.getDate()
                        });
                        revenue += s.getTotalPrice();
                    }
                    final double rev = revenue;
                    SwingUtilities.invokeLater(() ->
                        dailyRevenueLabel.setText(String.format("Today's Revenue: ₹ %.2f  (%d sales)", rev, sales.size())));
                    setStatus("Sales loaded. Loading inventory…", UITheme.TEXT_SECONDARY);
                } catch (Exception e) {
                    setStatus("Error loading sales.", UITheme.DANGER);
                    e.printStackTrace();
                } finally {
                    // Always chain to inventory load after sales finishes
                    loadInventoryReport();
                }
            }
        };
        worker.execute();
    }

    private void loadInventoryReport() {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            protected List<Product> doInBackground() { return reportsCtrl.getInventoryStatus(); }
            protected void done() {
                try {
                    List<Product> products = get();
                    inventoryModel.setRowCount(0);
                    int lowCount = 0;
                    for (Product p : products) {
                        String status = p.isLowStock() ? "⚠ Low Stock" : "✓ OK";
                        if (p.isLowStock()) lowCount++;
                        inventoryModel.addRow(new Object[]{
                            p.getProductId(), p.getName(), p.getQuantity(),
                            String.format("%.2f", p.getPrice()), p.getSupplierId(), status
                        });
                    }
                    final int low = lowCount;
                    final int total = products.size();
                    SwingUtilities.invokeLater(() ->
                        totalItemsLabel.setText("Total Products: " + total + "  |  Low Stock: " + low));
                } catch (Exception e) {
                    setStatus("Error loading inventory.", UITheme.DANGER);
                }
            }
        };
        worker.execute();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> { statusLabel.setText(msg); statusLabel.setForeground(color); });
    }
}
