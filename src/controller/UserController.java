package controller;

import firebase.FirebaseConfig;
import firebase.FirebaseHelper;
import model.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * UserController
 * -------------
 * Handles CRUD for users against Firebase /users node.
 */
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    // ── CREATE ──────────────────────────────────────────────────────────────────

    public boolean addUser(User user) {
        if (!validateUser(user))
            return false;

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId("U" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        String path = FirebaseConfig.USERS_PATH + "/" + user.getUserId();
        String json = user.toJson();
        String result = FirebaseHelper.put(path, json);

        if (result != null) {
            LOGGER.info("User added: " + user.getUserId());
            return true;
        }
        return false;
    }

    // ── READ ────────────────────────────────────────────────────────────────────

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String response = FirebaseHelper.get(FirebaseConfig.USERS_PATH);

        if (response == null || response.equals("null")) {
            LOGGER.info("No users found.");
            return list;
        }

        try {
            JSONObject json = new JSONObject(response);
            for (String key : json.keySet()) {
                JSONObject u = json.getJSONObject(key);
                User user = parseUser(key, u);
                list.add(user);
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing users: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("Fetched " + list.size() + " users.");
        return list;
    }

    // ── DELETE ──────────────────────────────────────────────────────────────────

    public boolean deleteUser(String userId) {
        String result = FirebaseHelper.delete(FirebaseConfig.USERS_PATH + "/" + userId);
        return result != null;
    }

    // ── HELPERS ─────────────────────────────────────────────────────────────────

    private User parseUser(String key, JSONObject u) {
        User user = new User();
        String uid = u.optString("userId", key);
        if (uid == null || uid.isEmpty())
            uid = key;
        user.setUserId(uid);
        user.setUsername(u.optString("username", ""));
        user.setPassword(u.optString("password", ""));
        user.setRole(u.optString("role", "user"));
        return user;
    }

    private boolean validateUser(User user) {
        if (user == null)
            return false;
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            LOGGER.warning("Username is empty.");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            LOGGER.warning("Password is empty.");
            return false;
        }
        return true;
    }
}
