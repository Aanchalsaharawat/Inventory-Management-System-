package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.Product;
import model.Sale;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * ReportsController
 * -----------------
 * Fetches daily sales and inventory status reports from Firebase.
 * Demonstrates OOP: Abstraction.
 */
public class ReportsController {

    private static final Logger LOGGER = Logger.getLogger(ReportsController.class.getName());

    private final ProductController productController = new ProductController();

    // ── SALES REPORT ────────────────────────────────────────────────────────────

    /**
     * Returns all sales records from Firebase.
     */
    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        String response = FirebaseHelper.get(FirebaseConfig.SALES_PATH);

        if (response == null || response.equals("null")) {
            LOGGER.info("No sales found.");
            return list;
        }

        try {
            JSONObject json = new JSONObject(response);
            for (String key : json.keySet()) {
                JSONObject s = json.getJSONObject(key);
                Sale sale = new Sale(
                    s.optString("saleId", key),
                    s.optString("productId", ""),
                    s.optString("productName", ""),
                    s.optInt("quantity", 0),
                    s.optDouble("totalPrice", 0.0),
                    s.optString("date", "")
                );
                list.add(sale);
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing sales: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("Fetched " + list.size() + " sales.");
        return list;
    }

    /**
     * Returns sales for today's date only.
     */
    public List<Sale> getDailySales() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<Sale> all = getAllSales();
        List<Sale> todaySales = new ArrayList<>();

        for (Sale sale : all) {
            if (sale.getDate() != null && sale.getDate().startsWith(today)) {
                todaySales.add(sale);
            }
        }

        LOGGER.info("Daily sales (" + today + "): " + todaySales.size());
        return todaySales;
    }

    /**
     * Calculates total revenue from a list of sales.
     */
    public double calculateTotalRevenue(List<Sale> sales) {
        double total = 0;
        for (Sale s : sales) {
            total += s.getTotalPrice();
        }
        return total;
    }

    // ── INVENTORY REPORT ─────────────────────────────────────────────────────────

    /**
     * Returns all products for inventory status display.
     */
    public List<Product> getInventoryStatus() {
        return productController.getAllProducts();
    }

    /**
     * Returns products with quantity < 5 (low stock alert).
     */
    public List<Product> getLowStockProducts() {
        List<Product> all  = productController.getAllProducts();
        List<Product> low  = new ArrayList<>();
        for (Product p : all) {
            if (p.isLowStock()) {
                low.add(p);
            }
        }
        LOGGER.info("Low stock products: " + low.size());
        return low;
    }
}
