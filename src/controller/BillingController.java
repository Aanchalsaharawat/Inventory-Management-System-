package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.Product;
import model.Sale;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * BillingController
 * -----------------
 * Handles sale generation, stock deduction, and saving to Firebase.
 * Demonstrates OOP: Abstraction, Single Responsibility.
 */
public class BillingController {

    private static final Logger LOGGER = Logger.getLogger(BillingController.class.getName());

    private final ProductController productController = new ProductController();

    /**
     * Processes a sale:
     * 1. Verifies stock availability.
     * 2. Calculates total price.
     * 3. Deducts stock from Firebase.
     * 4. Saves sale record to Firebase.
     *
     * @param product  The product being sold
     * @param qty      Quantity to sell
     * @return         Completed Sale object, or null on failure
     */
    public Sale processSale(Product product, int qty) {
        // Validation
        if (product == null) {
            LOGGER.warning("Product is null.");
            return null;
        }
        if (qty <= 0) {
            LOGGER.warning("Invalid quantity: " + qty);
            return null;
        }
        if (product.getQuantity() < qty) {
            LOGGER.warning("Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + qty);
            return null;
        }

        // Calculate total
        double total = product.getPrice() * qty;

        // Deduct stock
        int newQty = product.getQuantity() - qty;
        boolean stockUpdated = productController.updateQuantity(product.getProductId(), newQty);
        if (!stockUpdated) {
            LOGGER.severe("Failed to update stock for product: " + product.getProductId());
            return null;
        }

        // Build sale record
        String saleId = "SALE" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String date   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Sale sale = new Sale(saleId, product.getProductId(), product.getName(), qty, total, date);

        // Save sale to Firebase
        String path   = FirebaseConfig.SALES_PATH + "/" + saleId;
        String result = FirebaseHelper.put(path, sale.toJson());

        if (result != null) {
            LOGGER.info("Sale saved: " + saleId + " | Total: " + total);
            return sale;
        } else {
            // Roll back stock
            LOGGER.severe("Failed to save sale. Rolling back stock.");
            productController.updateQuantity(product.getProductId(), product.getQuantity());
            return null;
        }
    }

    /**
     * Calculates the total price for display (before confirming sale).
     */
    public double calculateTotal(double unitPrice, int qty) {
        return unitPrice * qty;
    }
}
