package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.Product;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ProductController
 * -----------------
 * CRUD operations for products against Firebase /products node.
 * Demonstrates OOP: Abstraction, Encapsulation.
 */
public class ProductController {

    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    // ── CREATE ──────────────────────────────────────────────────────────────────

    /**
     * Adds a new product to Firebase.
     * Smart Logic: If a product with the same name and supplier exists, 
     * it updates the quantity of the existing one instead of creating a new ID.
     */
    public boolean addProduct(Product product) {
        if (!validateProduct(product)) return false;

        // Check for duplicates (same name and supplier)
        List<Product> existingProducts = getAllProducts();
        for (Product existing : existingProducts) {
            if (existing.getName().equalsIgnoreCase(product.getName()) && 
                existing.getSupplierId().equalsIgnoreCase(product.getSupplierId())) {
                
                LOGGER.info("Duplicate product found. Updating quantity for ID: " + existing.getProductId());
                existing.setQuantity(existing.getQuantity() + product.getQuantity());
                existing.setPrice(product.getPrice()); // Update to latest price
                return updateProduct(existing);
            }
        }

        // Truly new product
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId("P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        String path = FirebaseConfig.PRODUCTS_PATH + "/" + product.getProductId();
        String json = product.toJson();
        String result = FirebaseHelper.put(path, json);

        if (result != null) {
            LOGGER.info("New product added: " + product.getProductId());
            return true;
        }
        LOGGER.warning("Failed to add product: " + product.getProductId());
        return false;
    }

    // ── READ ────────────────────────────────────────────────────────────────────

    /** Returns all products from Firebase. */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String response = FirebaseHelper.get(FirebaseConfig.PRODUCTS_PATH);

        if (response == null || response.equals("null")) {
            LOGGER.info("No products found.");
            return list;
        }

        try {
            JSONObject json = new JSONObject(response);
            for (String key : json.keySet()) {
                JSONObject p = json.getJSONObject(key);
                Product product = parseProduct(key, p);
                list.add(product);
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing products: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("Fetched " + list.size() + " products.");
        return list;
    }

    /** Returns a single product by ID. */
    public Product getProductById(String productId) {
        String response = FirebaseHelper.get(FirebaseConfig.PRODUCTS_PATH + "/" + productId);
        if (response == null || response.equals("null")) return null;

        try {
            JSONObject p = new JSONObject(response);
            return parseProduct(productId, p);
        } catch (Exception e) {
            LOGGER.severe("Error parsing product: " + e.getMessage());
            return null;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────────

    /** Updates an existing product in Firebase. */
    public boolean updateProduct(Product product) {
        if (!validateProduct(product)) return false;

        String path   = FirebaseConfig.PRODUCTS_PATH + "/" + product.getProductId();
        String json   = product.toJson();
        String result = FirebaseHelper.put(path, json);

        if (result != null) {
            LOGGER.info("Product updated: " + product.getProductId());
            return true;
        }
        return false;
    }

    /** Updates only the quantity field (used after billing). */
    public boolean updateQuantity(String productId, int newQuantity) {
        String path   = FirebaseConfig.PRODUCTS_PATH + "/" + productId;
        String json   = "{\"quantity\":" + newQuantity + "}";
        String result = FirebaseHelper.patch(path, json);
        return result != null;
    }

    // ── DELETE ──────────────────────────────────────────────────────────────────

    /** Deletes a product from Firebase. */
    public boolean deleteProduct(String productId) {
        String result = FirebaseHelper.delete(FirebaseConfig.PRODUCTS_PATH + "/" + productId);
        if (result != null) {
            LOGGER.info("Product deleted: " + productId);
            return true;
        }
        return false;
    }

    // ── HELPERS ─────────────────────────────────────────────────────────────────

    private Product parseProduct(String key, JSONObject p) {
        Product product = new Product();
        product.setProductId(p.optString("productId", key));
        product.setName(p.optString("name", ""));
        product.setQuantity(p.optInt("quantity", 0));
        product.setPrice(p.optDouble("price", 0.0));
        product.setSupplierId(p.optString("supplierId", ""));
        return product;
    }

    private boolean validateProduct(Product product) {
        if (product == null) return false;
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            LOGGER.warning("Product name is empty.");
            return false;
        }
        if (product.getPrice() < 0) {
            LOGGER.warning("Product price is negative.");
            return false;
        }
        if (product.getQuantity() < 0) {
            LOGGER.warning("Product quantity is negative.");
            return false;
        }
        return true;
    }
}
