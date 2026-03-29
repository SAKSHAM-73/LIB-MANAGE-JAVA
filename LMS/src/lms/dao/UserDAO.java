package lms.dao;

import lms.exceptions.DuplicateEntryException;
import lms.exceptions.LMSException;
import lms.exceptions.UserNotFoundException;
import lms.model.User;
import lms.model.User.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations — MySQL 8.0
 */
public class UserDAO {

    // ── ADD USER ────────────────────────────────────────────
    public void addUser(User user) throws LMSException, SQLException {
        if (findByEmail(user.getEmail()) != null) {
            throw new DuplicateEntryException("User", user.getEmail());
        }
        String sql = "INSERT INTO lms_users(user_id, name, email, password, role, balance) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setDouble(6, user.getBalance());
            ps.executeUpdate();
        }
    }

    // ── FIND BY ID ──────────────────────────────────────────
    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM lms_users WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── FIND BY ID — throws if missing ──────────────────────
    public User findByIdOrThrow(String userId) throws LMSException, SQLException {
        User u = findById(userId);
        if (u == null) throw new UserNotFoundException(userId);
        return u;
    }

    // ── FIND BY EMAIL ───────────────────────────────────────
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM lms_users WHERE email = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── ALL USERS (non-admin) ───────────────────────────────
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM lms_users WHERE role = 'USER' ORDER BY name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── UPDATE BALANCE ──────────────────────────────────────
    public void updateBalance(String userId, double newBalance) throws SQLException {
        String sql = "UPDATE lms_users SET balance = ? WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }

    // ── MAP ROW ─────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId      (rs.getString("user_id"));
        u.setName        (rs.getString("name"));
        u.setEmail       (rs.getString("email"));
        u.setPasswordHash(rs.getString("password"));
        u.setRole        (Role.valueOf(rs.getString("role")));
        u.setBalance     (rs.getDouble("balance"));
        return u;
    }
}
