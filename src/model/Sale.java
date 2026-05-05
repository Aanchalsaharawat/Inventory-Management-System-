package model;

/**
 * Sale – represents a completed sale transaction.
 * Demonstrates OOP: Encapsulation.
 */
public class Sale {

    private String saleId;
    private String productId;
    private String productName;   // denormalized for display
    private int    quantity;
    private double totalPrice;
    private String date;          // ISO-8601 date string

    // ── Constructors ────────────────────────────────────────────────────────────

    public Sale() {}

    public Sale(String saleId, String productId, String productName,
                int quantity, double totalPrice, String date) {
        this.saleId       = saleId;
        this.productId    = productId;
        this.productName  = productName;
        this.quantity     = quantity;
        this.totalPrice   = totalPrice;
        this.date         = date;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getSaleId()                    { return saleId; }
    public void   setSaleId(String saleId)       { this.saleId = saleId; }

    public String getProductId()                 { return productId; }
    public void   setProductId(String productId) { this.productId = productId; }

    public String getProductName()               { return productName; }
    public void   setProductName(String n)       { this.productName = n; }

    public int    getQuantity()                  { return quantity; }
    public void   setQuantity(int quantity)      { this.quantity = quantity; }

    public double getTotalPrice()                { return totalPrice; }
    public void   setTotalPrice(double price)    { this.totalPrice = price; }

    public String getDate()                      { return date; }
    public void   setDate(String date)           { this.date = date; }

    // ── Utility ─────────────────────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"saleId\":\"%s\",\"productId\":\"%s\",\"productName\":\"%s\"," +
            "\"quantity\":%d,\"totalPrice\":%.2f,\"date\":\"%s\"}",
            saleId, productId, productName, quantity, totalPrice, date
        );
    }

    @Override
    public String toString() {
        return "Sale{id=" + saleId + ", product=" + productId
             + ", qty=" + quantity + ", total=" + totalPrice + ", date=" + date + "}";
    }
}
