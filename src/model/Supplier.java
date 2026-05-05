package model;

/**
 * Supplier – represents a product supplier.
 * Demonstrates OOP: Encapsulation.
 */
public class Supplier {

    private String supplierId;
    private String name;
    private String contact;

    // ── Constructors ────────────────────────────────────────────────────────────

    public Supplier() {}

    public Supplier(String supplierId, String name, String contact) {
        this.supplierId = supplierId;
        this.name       = name;
        this.contact    = contact;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getSupplierId()              { return supplierId; }
    public void   setSupplierId(String id)     { this.supplierId = id; }

    public String getName()                    { return name; }
    public void   setName(String name)         { this.name = name; }

    public String getContact()                 { return contact; }
    public void   setContact(String contact)   { this.contact = contact; }

    // ── Utility ─────────────────────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"supplierId\":\"%s\",\"name\":\"%s\",\"contact\":\"%s\"}",
            supplierId, name, contact
        );
    }

    @Override
    public String toString() {
        return "Supplier{id=" + supplierId + ", name=" + name + ", contact=" + contact + "}";
    }
}
