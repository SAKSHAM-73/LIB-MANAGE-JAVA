package lms.dao;

import lms.model.BorrowRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Borrow/Return records — MySQL 8.0
 * Uses AUTO_INCREMENT for borrow_id (no Oracle IDENTITY needed).
 */
public class BorrowDAO {

    // ── CREATE BORROW RECORD ────────────────────────────────
    public void createBorrow(BorrowRecord record) throws SQLException {
        String sql = "INSERT INTO lms_borrows(user_id, book_id, borrow_date, due_date, status) " +
                     "VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, record.getUserId());
            ps.setString(2, record.getBookId());
            ps.setDate  (3, Date.valueOf(record.getBorrowDate()));
            ps.setDate  (4, Date.valueOf(record.getDueDate()));
            ps.executeUpdate();
        }
    }

    // ── FIND ACTIVE BORROW for (user, book) ─────────────────
    public BorrowRecord findActiveBorrow(String userId, String bookId) throws SQLException {
        String sql = "SELECT * FROM lms_borrows " +
                     "WHERE user_id = ? AND book_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── MARK RETURNED ───────────────────────────────────────
    public void markReturned(int borrowId, LocalDate returnDate,
                             double fineAmount) throws SQLException {
        String sql = "UPDATE lms_borrows " +
                     "SET status = 'RETURNED', return_date = ?, fine_amount = ? " +
                     "WHERE borrow_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate  (1, Date.valueOf(returnDate));
            ps.setDouble(2, fineAmount);
            ps.setInt   (3, borrowId);
            ps.executeUpdate();
        }
    }

    // ── HISTORY FOR USER ────────────────────────────────────
    public List<BorrowRecord> getHistoryForUser(String userId) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM lms_borrows WHERE user_id = ? ORDER BY borrow_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── CHECK ACTIVE BORROW EXISTS ──────────────────────────
    public boolean hasActiveBorrow(String userId, String bookId) throws SQLException {
        return findActiveBorrow(userId, bookId) != null;
    }

    // ── MAP ROW ─────────────────────────────────────────────
    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        BorrowRecord r = new BorrowRecord();
        r.setBorrowId  (rs.getInt("borrow_id"));
        r.setUserId    (rs.getString("user_id"));
        r.setBookId    (rs.getString("book_id"));
        r.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        r.setDueDate   (rs.getDate("due_date").toLocalDate());
        Date ret = rs.getDate("return_date");
        if (ret != null) r.setReturnDate(ret.toLocalDate());
        r.setFineAmount(rs.getDouble("fine_amount"));
        r.setStatus    (rs.getString("status"));
        return r;
    }
}
