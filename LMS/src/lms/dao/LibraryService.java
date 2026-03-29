package lms.dao;

import lms.exceptions.*;
import lms.hash.BookHashTable;
import lms.hash.PasswordUtil;
import lms.model.Book;
import lms.model.BorrowRecord;
import lms.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * LibraryService — Business Logic Layer.
 *
 * Sits between UI and DAOs.  All user-facing operations go through here.
 * The in-memory BookHashTable caches books for O(1) lookup;
 * DB is the authoritative source and is always kept in sync.
 */
public class LibraryService {

    private static final int BORROW_DAYS       = 14;    // 2-week loan period
    private static final double FINE_PER_DAY   = 2.00;  // ₹2 per overdue day

    private final BookDAO     bookDAO     = new BookDAO();
    private final UserDAO     userDAO     = new UserDAO();
    private final BorrowDAO   borrowDAO   = new BorrowDAO();
    private final BookHashTable hashTable  = new BookHashTable();

    // Currently logged-in user
    private User currentUser = null;

    // ── CONSTRUCTOR — pre-load books into hash table ────────
    public LibraryService() throws SQLException {
        refreshHashTable();
    }

    private void refreshHashTable() throws SQLException {
        for (Book b : bookDAO.getAllBooks()) {
            hashTable.put(b);
        }
    }

    // ════════════════════════════════════════════════════════
    //  AUTH
    // ════════════════════════════════════════════════════════

    public User login(String email, String password)
            throws LMSException, SQLException {
        User user = userDAO.findByEmail(email);
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        currentUser = user;
        return user;
    }

    public void logout() { currentUser = null; }

    public User getCurrentUser() { return currentUser; }

    // ════════════════════════════════════════════════════════
    //  ADMIN OPERATIONS
    // ════════════════════════════════════════════════════════

    public void addBook(String bookId, String title, String author,
                        String genre, int copies, double pricePerDay)
            throws LMSException, SQLException {
        requireAdmin();
        Book book = new Book(bookId, title, author, genre, copies, pricePerDay);
        bookDAO.addBook(book);
        hashTable.put(book);         // keep hash table in sync
        System.out.println("[OK] Book added: " + title);
    }

    public void addUser(String userId, String name, String email, String password)
            throws LMSException, SQLException {
        requireAdmin();
        String hash = PasswordUtil.hash(password);
        User user = new User(userId, name, email, hash, User.Role.USER, 0.0);
        userDAO.addUser(user);
        System.out.println("[OK] User registered: " + name);
    }

    public List<User> listAllUsers() throws LMSException, SQLException {
        requireAdmin();
        return userDAO.getAllUsers();
    }

    // ════════════════════════════════════════════════════════
    //  USER OPERATIONS
    // ════════════════════════════════════════════════════════

    /**
     * Borrow a book.
     * 1. O(1) lookup via hash table to check availability.
     * 2. Deduct from DB + update hash table.
     * 3. Create borrow record in DB.
     */
    public void borrowBook(String bookId)
            throws LMSException, SQLException {
        requireLogin();

        // O(1) hash table lookup
        Book book = hashTable.get(bookId);
        if (book == null) throw new BookNotFoundException(bookId);
        if (!book.isAvailable()) throw new BookUnavailableException(book.getTitle());

        if (borrowDAO.hasActiveBorrow(currentUser.getUserId(), bookId)) {
            throw new LMSException("ALREADY_BORROWED",
                "You already have this book borrowed.");
        }

        // Update DB
        bookDAO.decrementCopies(bookId);

        // Sync hash table
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        hashTable.put(book);

        // Create borrow record
        LocalDate today = LocalDate.now();
        BorrowRecord record = new BorrowRecord(
            currentUser.getUserId(), bookId, today, today.plusDays(BORROW_DAYS));
        borrowDAO.createBorrow(record);

        System.out.printf("[OK] Borrowed '%s'. Due date: %s%n",
            book.getTitle(), today.plusDays(BORROW_DAYS));
    }

    /**
     * Return a book.
     * Calculates fine for overdue days, deducts from balance, updates DB.
     */
    public void returnBook(String bookId)
            throws LMSException, SQLException {
        requireLogin();

        BorrowRecord record = borrowDAO.findActiveBorrow(
            currentUser.getUserId(), bookId);
        if (record == null) {
            throw new LMSException("NOT_BORROWED",
                "No active borrow found for book: " + bookId);
        }

        LocalDate today    = LocalDate.now();
        double fine        = 0.0;
        long overdueDays   = ChronoUnit.DAYS.between(record.getDueDate(), today);

        if (overdueDays > 0) {
            fine = overdueDays * FINE_PER_DAY;
            double balance = currentUser.getBalance();
            if (balance < fine) {
                throw new InsufficientBalanceException(fine, balance);
            }
            double newBalance = balance - fine;
            userDAO.updateBalance(currentUser.getUserId(), newBalance);
            currentUser.setBalance(newBalance);
            System.out.printf("[FINE] Overdue by %d days. Fine: ₹%.2f deducted.%n",
                overdueDays, fine);
        }

        // Mark returned in DB
        borrowDAO.markReturned(record.getBorrowId(), today, fine);

        // Increment copies
        bookDAO.incrementCopies(bookId);

        // Sync hash table
        Book book = hashTable.get(bookId);
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            hashTable.put(book);
        }

        System.out.println("[OK] Book returned successfully.");
    }

    /** Top up user's wallet balance. */
    public void addBalance(double amount)
            throws LMSException, SQLException {
        requireLogin();
        if (amount <= 0) throw new LMSException("INVALID_AMT", "Amount must be positive.");
        double newBalance = currentUser.getBalance() + amount;
        userDAO.updateBalance(currentUser.getUserId(), newBalance);
        currentUser.setBalance(newBalance);
        System.out.printf("[OK] Balance updated. New balance: ₹%.2f%n", newBalance);
    }

    public List<Book> getAvailableBooks() throws SQLException {
        return bookDAO.getAvailableBooks();
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        return bookDAO.searchByTitle(keyword);
    }

    public List<BorrowRecord> getMyHistory() throws LMSException, SQLException {
        requireLogin();
        return borrowDAO.getHistoryForUser(currentUser.getUserId());
    }

    // ════════════════════════════════════════════════════════
    //  GUARDS
    // ════════════════════════════════════════════════════════

    private void requireLogin() throws LMSException {
        if (currentUser == null) {
            throw new LMSException("NOT_LOGGED_IN", "Please login first.");
        }
    }

    private void requireAdmin() throws LMSException {
        requireLogin();
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedAccessException();
        }
    }
}
