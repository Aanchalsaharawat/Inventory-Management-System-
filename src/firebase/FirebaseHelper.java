package firebase;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

/**
 * FirebaseHelper
 * --------------
 * Handles all Firebase Realtime Database REST API calls.
 * Supports GET, POST, PUT, PATCH, DELETE.
 *
 * Firebase REST URL format:
 *   {DATABASE_URL}/{path}.json
 */
public class FirebaseHelper {

    private static final Logger LOGGER = Logger.getLogger(FirebaseHelper.class.getName());

    // ── Generic HTTP request ────────────────────────────────────────────────────

    /**
     * Sends an HTTP request to Firebase.
     *
     * @param path   Firebase path (e.g. "/products/p001")
     * @param method HTTP method: GET | POST | PUT | PATCH | DELETE
     * @param body   JSON body (null for GET/DELETE)
     * @return       JSON response string, or null on error
     */
    public static String sendRequest(String path, String method, String body) {
        String urlStr = FirebaseConfig.DATABASE_URL + path + ".json";
        LOGGER.info("[Firebase] " + method + " " + urlStr);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            // Java's HttpURLConnection does NOT support PATCH natively.
            // Firebase honours POST + X-HTTP-Method-Override: PATCH identically.
            if ("PATCH".equals(method)) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                conn.setRequestMethod(method);
            }

            conn.setConnectTimeout(FirebaseConfig.CONNECT_TIMEOUT);
            conn.setReadTimeout(FirebaseConfig.READ_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Write body for methods that require it
            if (body != null && (method.equals("POST") || method.equals("PUT")
                    || method.equals("PATCH"))) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes("UTF-8"));
                }
            }

            int responseCode = conn.getResponseCode();
            LOGGER.info("[Firebase] Response Code: " + responseCode);

            // Read response
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is == null) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            String response = sb.toString();
            LOGGER.info("[Firebase] Response: " + response);
            return response;

        } catch (Exception e) {
            LOGGER.severe("[Firebase] Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ── Convenience methods ─────────────────────────────────────────────────────

    /** GET  – fetch data at path */
    public static String get(String path) {
        return sendRequest(path, "GET", null);
    }

    /** PUT  – create/replace data at path */
    public static String put(String path, String json) {
        return sendRequest(path, "PUT", json);
    }

    /** POST – push new child at path, Firebase generates key */
    public static String post(String path, String json) {
        return sendRequest(path, "POST", json);
    }

    /** PATCH – update specific fields at path */
    public static String patch(String path, String json) {
        return sendRequest(path, "PATCH", json);
    }

    /** DELETE – remove data at path */
    public static String delete(String path) {
        return sendRequest(path, "DELETE", null);
    }

    // ── Connectivity check ──────────────────────────────────────────────────────

    /** Returns true if Firebase is reachable. */
    public static boolean isConnected() {
        try {
            String response = get("");   // root check
            return response != null;
        } catch (Exception e) {
            return false;
        }
    }
}
