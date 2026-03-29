package lms.dao;

import lms.exceptions.BookNotFoundException;
import lms.exceptions.DuplicateEntryException;
import lms.exceptions.LMSException;
import lms.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book operations — MySQL 8.0
 */
public class BookDAO {

    // ── ADD BOOK ────────────────────────────────────────────
    public void addBook(Book book) throws LMSException, SQLException {
        if (findById(book.getBookId()) != null) {
            throw new DuplicateEntryException("Book", book.getBookId());
        }
        String sql = "INSERT INTO lms_books " +
                     "(book_id, title, author, genre, total_copies, available_copies, price_per_day) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getGenre());
            ps.setInt   (5, book.getTotalCopies());
            ps.setInt   (6, book.getAvailableCopies());
            ps.setDouble(7, book.getPricePerDay());
            ps.executeUpdate();
        }
    }

    // ── FIND BY ID ──────────────────────────────────────────
    public Book findById(String bookId) throws SQLException {
        String sql = "SELECT * FROM lms_books WHERE book_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── FIND BY ID — throws if missing ──────────────────────
    public Book findByIdOrThrow(String bookId) throws LMSException, SQLException {
        Book b = findById(bookId);
        if (b == null) throw new BookNotFoundException(bookId);
        return b;
    }

    // ── ALL BOOKS ───────────────────────────────────────────
    public List<Book> getAllBooks() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM lms_books ORDER BY title";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── AVAILABLE BOOKS ─────────────────────────────────────
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM lms_books WHERE available_copies > 0 ORDER BY title";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── DECREMENT (borrow) ──────────────────────────────────
    public void decrementCopies(String bookId) throws SQLException {
        String sql = "UPDATE lms_books SET available_copies = available_copies - 1 " +
                     "WHERE book_id = ? AND available_copies > 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.executeUpdate();
        }
    }

    // ── INCREMENT (return) ──────────────────────────────────
    public void incrementCopies(String bookId) throws SQLException {
        String sql = "UPDATE lms_books SET available_copies = available_copies + 1 " +
                     "WHERE book_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.executeUpdate();
        }
    }

    // ── SEARCH BY TITLE ─────────────────────────────────────
    public List<Book> searchByTitle(String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM lms_books WHERE UPPER(title) LIKE UPPER(?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── MAP ROW ─────────────────────────────────────────────
    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId         (rs.getString("book_id"));
        b.setTitle          (rs.getString("title"));
        b.setAuthor         (rs.getString("author"));
        b.setGenre          (rs.getString("genre"));
        b.setTotalCopies    (rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setPricePerDay    (rs.getDouble("price_per_day"));
        return b;
    }
}
