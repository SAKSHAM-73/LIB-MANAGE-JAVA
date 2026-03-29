package lms.model;

public class User {

    public enum Role { ADMIN, USER }

    private String userId;
    private String name;
    private String email;
    private String passwordHash;   // SHA-256 hex
    private Role role;
    private double balance;

    public User() {}

    public User(String userId, String name, String email,
                String passwordHash, Role role, double balance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.balance = balance;
    }

    // ── Getters & Setters ──────────────────────────────────
    public String getUserId()                   { return userId; }
    public void setUserId(String userId)        { this.userId = userId; }

    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }

    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }

    public String getPasswordHash()             { return passwordHash; }
    public void setPasswordHash(String hash)    { this.passwordHash = hash; }

    public Role getRole()                       { return role; }
    public void setRole(Role role)              { this.role = role; }

    public double getBalance()                  { return balance; }
    public void setBalance(double balance)      { this.balance = balance; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() {
        return String.format(
            "User{id='%s', name='%s', email='%s', role=%s, balance=%.2f}",
            userId, name, email, role, balance
        );
    }
}
