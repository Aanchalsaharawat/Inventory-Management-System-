package model;

/**
 * User – represents a system user (admin).
 * Demonstrates OOP: Encapsulation.
 */
public class User {
    private String role;   // "admin" or "user"
    private String userId;
    private String username;
    private String password;   // stored as plain text in Firebase during dev

    // ── Constructors ────────────────────────────────────────────────────────────

    public User() {}

    public User(String userId, String username, String password) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getUserId()                { return userId; }
    public void   setUserId(String userId)   { this.userId = userId; }
    public String getRole()                { return role; }
    public void   setRole(String role)     { this.role = role; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    // ── Utility ─────────────────────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"userId\":\"%s\",\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
            userId, username, password, role != null ? role : "user"
        );
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username=" + username + "}";
    }
}
