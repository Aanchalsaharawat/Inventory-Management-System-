import firebase.FirebaseHelper;
import firebase.FirebaseConfig;

/**
 * DataSeeder – Seeds dummy data into Firebase for testing.
 * Run this ONCE before launching the main app.
 *
 * Data inserted:
 *   /users       – 1 admin account
 *   /suppliers   – 3 suppliers
 *   /products    – 6 products (one with low stock)
 *   /sales       – 4 sample sale records
 */
public class DataSeeder {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   IMS Firebase Data Seeder               ║");
        System.out.println("║   Target: " + FirebaseConfig.DATABASE_URL);
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ── 1. admin Users ──────────────────────────────────────────────────────────────
        System.out.println("── Seeding admin Users ──");
        put("/users/admin001",
                "{\"userId\":\"admin001\",\"username\":\"admin\",\"password\":\"admin123\",\"role\":\"admin\"}");
        // Normal user
        put("/users/user001",
                "{\"userId\":\"user001\",\"username\":\"user\",\"password\":\"user123\",\"role\":\"user\"}");

        // ── 2. Suppliers ──────────────────────────────────────────────────────────
        System.out.println("\n── Seeding Suppliers ──");
        put("/suppliers/S001",
            "{\"supplierId\":\"S001\",\"name\":\"TechZone Electronics\",\"contact\":\"+91-9876543210\"}");
        put("/suppliers/S002",
            "{\"supplierId\":\"S002\",\"name\":\"Global Stationary Co.\",\"contact\":\"+91-9123456789\"}");
        put("/suppliers/S003",
            "{\"supplierId\":\"S003\",\"name\":\"HomeComfort Supplies\",\"contact\":\"+91-9988776655\"}");

        // ── 3. Products ───────────────────────────────────────────────────────────
        System.out.println("\n── Seeding Products ──");
        put("/products/P001",
            "{\"productId\":\"P001\",\"name\":\"Wireless Mouse\",\"quantity\":25,\"price\":599.00,\"supplierId\":\"S001\"}");
        put("/products/P002",
            "{\"productId\":\"P002\",\"name\":\"USB Keyboard\",\"quantity\":18,\"price\":899.00,\"supplierId\":\"S001\"}");
        put("/products/P003",
            "{\"productId\":\"P003\",\"name\":\"HDMI Cable (2m)\",\"quantity\":3,\"price\":249.00,\"supplierId\":\"S001\"}");  // LOW STOCK
        put("/products/P004",
            "{\"productId\":\"P004\",\"name\":\"A4 Paper Ream\",\"quantity\":50,\"price\":350.00,\"supplierId\":\"S002\"}");
        put("/products/P005",
            "{\"productId\":\"P005\",\"name\":\"Ball Pen (Pack-10)\",\"quantity\":120,\"price\":55.00,\"supplierId\":\"S002\"}");
        put("/products/P006",
            "{\"productId\":\"P006\",\"name\":\"Office Chair\",\"quantity\":8,\"price\":4999.00,\"supplierId\":\"S003\"}");

        // ── 4. Sales ──────────────────────────────────────────────────────────────
        System.out.println("\n── Seeding Sales ──");
        String today = java.time.LocalDate.now().toString();
        put("/sales/SALE001",
            "{\"saleId\":\"SALE001\",\"productId\":\"P001\",\"productName\":\"Wireless Mouse\"," +
            "\"quantity\":2,\"totalPrice\":1198.00,\"date\":\"" + today + " 10:30:00\"}");
        put("/sales/SALE002",
            "{\"saleId\":\"SALE002\",\"productId\":\"P004\",\"productName\":\"A4 Paper Ream\"," +
            "\"quantity\":5,\"totalPrice\":1750.00,\"date\":\"" + today + " 11:15:00\"}");
        put("/sales/SALE003",
            "{\"saleId\":\"SALE003\",\"productId\":\"P005\",\"productName\":\"Ball Pen (Pack-10)\"," +
            "\"quantity\":10,\"totalPrice\":550.00,\"date\":\"" + today + " 13:00:00\"}");
        put("/sales/SALE004",
            "{\"saleId\":\"SALE004\",\"productId\":\"P002\",\"productName\":\"USB Keyboard\"," +
            "\"quantity\":1,\"totalPrice\":899.00,\"date\":\"2026-05-02 09:45:00\"}");  // yesterday

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  ✅  Seeding Complete!                    ║");
        System.out.println("║                                          ║");
        System.out.println("║  Login: admin / admin123                 ║");
        System.out.println("║  Products: 6  (1 low-stock)              ║");
        System.out.println("║  Suppliers: 3                            ║");
        System.out.println("║  Sales: 4  (3 today)                     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    private static void put(String path, String json) {
        System.out.print("  PUT " + path + "  →  ");
        String result = FirebaseHelper.put(path, json);
        if (result != null) {
            System.out.println("✅ OK");
        } else {
            System.out.println("❌ FAILED  ← check Firebase URL & rules");
        }
    }
}
