package model;

/**
 * Product – represents a product in the inventory.
 * Demonstrates OOP: Encapsulation.
 */
public class Product {

    private String productId;
    private String name;
    private int    quantity;
    private double price;
    private String supplierId;

    // ── Constructors ────────────────────────────────────────────────────────────

    public Product() {}

    public Product(String productId, String name, int quantity,
                   double price, String supplierId) {
        this.productId  = productId;
        this.name       = name;
        this.quantity   = quantity;
        this.price      = price;
        this.supplierId = supplierId;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getProductId()              { return productId; }
    public void   setProductId(String id)     { this.productId = id; }

    public String getName()                   { return name; }
    public void   setName(String name)        { this.name = name; }

    public int    getQuantity()               { return quantity; }
    public void   setQuantity(int quantity)   { this.quantity = quantity; }

    public double getPrice()                  { return price; }
    public void   setPrice(double price)      { this.price = price; }

    public String getSupplierId()             { return supplierId; }
    public void   setSupplierId(String sid)   { this.supplierId = sid; }

    // ── Utility ─────────────────────────────────────────────────────────────────

    /** Returns true when stock is critically low. */
    public boolean isLowStock() {
        return quantity < 5;
    }

    /** Simple JSON serialization (no external library needed for models). */
    public String toJson() {
        return String.format(
            "{\"productId\":\"%s\",\"name\":\"%s\",\"quantity\":%d,\"price\":%.2f,\"supplierId\":\"%s\"}",
            productId, name, quantity, price, supplierId
        );
    }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", name=" + name
             + ", qty=" + quantity + ", price=" + price + "}";
    }
}
