package firebase;

/**
 * FirebaseConfig - Holds Firebase project configuration.
 * Update DATABASE_URL with your own Firebase project URL.
 */
public class FirebaseConfig {

    // ─── REPLACE with your Firebase Realtime Database URL ───────────────────────
    public static final String DATABASE_URL = "https://email-sender-36d90-default-rtdb.firebaseio.com";
    // ────────────────────────────────────────────────────────────────────────────

    // Endpoint paths
    public static final String PRODUCTS_PATH   = "/products";
    public static final String SUPPLIERS_PATH  = "/suppliers";
    public static final String SALES_PATH      = "/sales";
    public static final String USERS_PATH      = "/users";

    // Timeout (ms)
    public static final int CONNECT_TIMEOUT = 10_000;
    public static final int READ_TIMEOUT    = 15_000;

    private FirebaseConfig() { /* no-instantiation */ }
}
