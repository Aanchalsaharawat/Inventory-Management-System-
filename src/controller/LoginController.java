package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.User;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * LoginController
 * ---------------
 * Validates user credentials against Firebase /users node.
 * Demonstrates OOP: Abstraction (hides Firebase details from View).
 */
public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    /**
     * Authenticates a user.
     *
     * @param username entered username
     * @param password entered password
     * @return User object if valid, null otherwise
     */
    public User authenticate(String username, String password) {
        // Basic validation
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;

        LOGGER.info("Attempting login for: " + username);

        String response = FirebaseHelper.get(FirebaseConfig.USERS_PATH);
        if (response == null || response.equals("null")) {
            LOGGER.warning("No users found in Firebase.");
            return null;
        }

        try {
            JSONObject usersJson = new JSONObject(response);
            for (String key : usersJson.keySet()) {
                JSONObject u = usersJson.getJSONObject(key);
                String dbUser = u.optString("username", "");
                String dbPass = u.optString("password", "");

                if (dbUser.equals(username) && dbPass.equals(password)) {
                    LOGGER.info("Login successful for: " + username);
                    User user = new User(key, dbUser, dbPass);
                    user.setRole(u.optString("role", "user"));
                    return user;
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing users JSON: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.warning("Login failed for: " + username);
        return null;
    }

    /**
     * Seeds a default admin user into Firebase if /users is empty.
     * Run this once during initial setup.
     */
    public void seedDefaultAdmin() {
        String response = FirebaseHelper.get(FirebaseConfig.USERS_PATH);
        if (response == null || response.equals("null")) {
            String json = "{\"username\":\"admin\",\"password\":\"admin123\",\"role\":\"admin\"}";
            String result = FirebaseHelper.put(FirebaseConfig.USERS_PATH + "/admin001", json);
            LOGGER.info("Default admin seeded: " + result);
            return;
        }

        // Ensure existing admin has a role; patch if missing
        try {
            JSONObject usersJson = new JSONObject(response);
            for (String key : usersJson.keySet()) {
                JSONObject u = usersJson.getJSONObject(key);
                if ("admin".equals(u.optString("username", ""))) {
                    if (!u.has("role") || u.optString("role", "").isEmpty()) {
                        String patch = "{\"role\":\"admin\"}";
                        FirebaseHelper.patch(FirebaseConfig.USERS_PATH + "/" + key, patch);
                        LOGGER.info("Patched admin role for: " + key);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error patching admin role: " + e.getMessage());
        }
    }
}
