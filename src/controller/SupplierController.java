package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.Supplier;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * SupplierController
 * ------------------
 * Handles CRUD for suppliers against Firebase /suppliers node.
 * Demonstrates OOP: Abstraction.
 */
public class SupplierController {

    private static final Logger LOGGER = Logger.getLogger(SupplierController.class.getName());

    // ── CREATE ──────────────────────────────────────────────────────────────────

    public boolean addSupplier(Supplier supplier) {
        if (!validateSupplier(supplier)) return false;

        if (supplier.getSupplierId() == null || supplier.getSupplierId().isEmpty()) {
            supplier.setSupplierId("S" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        String path   = FirebaseConfig.SUPPLIERS_PATH + "/" + supplier.getSupplierId();
        String json   = supplier.toJson();
        String result = FirebaseHelper.put(path, json);

        if (result != null) {
            LOGGER.info("Supplier added: " + supplier.getSupplierId());
            return true;
        }
        return false;
    }

    // ── READ ────────────────────────────────────────────────────────────────────

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String response = FirebaseHelper.get(FirebaseConfig.SUPPLIERS_PATH);

        if (response == null || response.equals("null")) {
            LOGGER.info("No suppliers found.");
            return list;
        }

        try {
            JSONObject json = new JSONObject(response);
            for (String key : json.keySet()) {
                JSONObject s = json.getJSONObject(key);
                Supplier supplier = parseSupplier(key, s);
                list.add(supplier);
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing suppliers: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("Fetched " + list.size() + " suppliers.");
        return list;
    }

    public Supplier getSupplierById(String supplierId) {
        String response = FirebaseHelper.get(FirebaseConfig.SUPPLIERS_PATH + "/" + supplierId);
        if (response == null || response.equals("null")) return null;
        try {
            JSONObject s = new JSONObject(response);
            return parseSupplier(supplierId, s);
        } catch (Exception e) {
            LOGGER.severe("Error: " + e.getMessage());
            return null;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────────

    public boolean deleteSupplier(String supplierId) {
        String result = FirebaseHelper.delete(FirebaseConfig.SUPPLIERS_PATH + "/" + supplierId);
        return result != null;
    }

    // ── HELPERS ─────────────────────────────────────────────────────────────────

    private Supplier parseSupplier(String key, JSONObject s) {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(s.optString("supplierId", key));
        supplier.setName(s.optString("name", ""));
        supplier.setContact(s.optString("contact", ""));
        return supplier;
    }

    private boolean validateSupplier(Supplier supplier) {
        if (supplier == null) return false;
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            LOGGER.warning("Supplier name is empty.");
            return false;
        }
        return true;
    }
}
