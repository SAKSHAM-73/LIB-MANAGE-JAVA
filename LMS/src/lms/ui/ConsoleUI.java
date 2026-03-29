package lms.ui;

import lms.dao.DBConnection;
import lms.dao.LibraryService;
import lms.exceptions.LMSException;
import lms.model.Book;
import lms.model.BorrowRecord;
import lms.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleUI — Terminal-based menu interface.
 * Single-terminal, single-user-at-a-time system.
 */
public class ConsoleUI {

    private final LibraryService service;
    private final Scanner sc = new Scanner(System.in);

    public ConsoleUI(LibraryService service) {
        this.service = service;
    }

    // ════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ════════════════════════════════════════════════════════
    public void start() {
        printBanner();
        boolean running = true;

        while (running) {
            if (service.getCurrentUser() == null) {
                running = showGuestMenu();
            } else if (service.getCurrentUser().isAdmin()) {
                running = showAdminMenu();
            } else {
                running = showUserMenu();
            }
        }

        System.out.println("\n  Goodbye! Closing connection...");
        DBConnection.closeConnection();
    }

    // ════════════════════════════════════════════════════════
    //  GUEST MENU
    // ════════════════════════════════════════════════════════
    private boolean showGuestMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        GUEST MENU            ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Login                    ║");
        System.out.println("║  2. View Available Books     ║");
        System.out.println("║  0. Exit                     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("  Choice: ");

        switch (sc.nextLine().trim()) {
            case "1" -> doLogin();
            case "2" -> showAvailableBooks();
            case "0" -> { return false; }
            default  -> System.out.println("  [!] Invalid choice.");
        }
        return true;
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ════════════════════════════════════════════════════════
    private boolean showAdminMenu() {
        User u = service.getCurrentUser();
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║  ADMIN MENU — " + pad(u.getName(), 14) + "║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Add Book                 ║");
        System.out.println("║  2. Add User                 ║");
        System.out.println("║  3. List All Books           ║");
        System.out.println("║  4. List All Users           ║");
        System.out.println("║  5. Search Books             ║");
        System.out.println("║  6. Logout                   ║");
        System.out.println("║  0. Exit                     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("  Choice: ");

        switch (sc.nextLine().trim()) {
            case "1" -> doAddBook();
            case "2" -> doAddUser();
            case "3" -> showAllBooks();
            case "4" -> doListUsers();
            case "5" -> doSearchBooks();
            case "6" -> { service.logout(); System.out.println("  Logged out."); }
            case "0" -> { return false; }
            default  -> System.out.println("  [!] Invalid choice.");
        }
        return true;
    }

    // ════════════════════════════════════════════════════════
    //  USER MENU
    // ════════════════════════════════════════════════════════
    private boolean showUserMenu() {
        User u = service.getCurrentUser();
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║  USER MENU — " + pad(u.getName(), 15) + "║");
        System.out.printf ("║  Balance: ₹%-18.2f║%n", u.getBalance());
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. View Available Books     ║");
        System.out.println("║  2. Borrow a Book            ║");
        System.out.println("║  3. Return a Book            ║");
        System.out.println("║  4. My Borrow History        ║");
        System.out.println("║  5. Add Balance              ║");
        System.out.println("║  6. Search Books             ║");
        System.out.println("║  7. Logout                   ║");
        System.out.println("║  0. Exit                     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("  Choice: ");

        switch (sc.nextLine().trim()) {
            case "1" -> showAvailableBooks();
            case "2" -> doBorrowBook();
            case "3" -> doReturnBook();
            case "4" -> doShowHistory();
            case "5" -> doAddBalance();
            case "6" -> doSearchBooks();
            case "7" -> { service.logout(); System.out.println("  Logged out."); }
            case "0" -> { return false; }
            default  -> System.out.println("  [!] Invalid choice.");
        }
        return true;
    }

    // ════════════════════════════════════════════════════════
    //  ACTION HANDLERS
    // ════════════════════════════════════════════════════════

    private void doLogin() {
        System.out.print("  Email   : "); String email = sc.nextLine().trim();
        System.out.print("  Password: "); String pass  = sc.nextLine().trim();
        try {
            User u = service.login(email, pass);
            System.out.println("  [OK] Welcome, " + u.getName()
                               + " (" + u.getRole() + ")");
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doAddBook() {
        try {
            System.out.print("  Book ID      : "); String id     = sc.nextLine().trim();
            System.out.print("  Title        : "); String title  = sc.nextLine().trim();
            System.out.print("  Author       : "); String author = sc.nextLine().trim();
            System.out.print("  Genre        : "); String genre  = sc.nextLine().trim();
            System.out.print("  Copies       : "); int copies    = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Price/day ₹  : "); double price  = Double.parseDouble(sc.nextLine().trim());
            service.addBook(id, title, author, genre, copies, price);
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("  [ERR] Invalid number format.");
        }
    }

    private void doAddUser() {
        try {
            System.out.print("  User ID   : "); String id    = sc.nextLine().trim();
            System.out.print("  Name      : "); String name  = sc.nextLine().trim();
            System.out.print("  Email     : "); String email = sc.nextLine().trim();
            System.out.print("  Password  : "); String pass  = sc.nextLine().trim();
            service.addUser(id, name, email, pass);
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void showAllBooks() {
        try {
            List<Book> books = service.getAllBooks();
            printBookTable(books, "ALL BOOKS");
        } catch (SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void showAvailableBooks() {
        try {
            List<Book> books = service.getAvailableBooks();
            printBookTable(books, "AVAILABLE BOOKS");
        } catch (SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doListUsers() {
        try {
            List<User> users = service.listAllUsers();
            System.out.println("\n  ── USER LIST ──────────────────────────────────");
            System.out.printf("  %-12s %-20s %-25s %10s%n",
                "ID","Name","Email","Balance");
            System.out.println("  " + "─".repeat(70));
            for (User u : users) {
                System.out.printf("  %-12s %-20s %-25s %10.2f%n",
                    u.getUserId(), u.getName(), u.getEmail(), u.getBalance());
            }
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doBorrowBook() {
        System.out.print("  Book ID to borrow: ");
        String bookId = sc.nextLine().trim();
        try {
            service.borrowBook(bookId);
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doReturnBook() {
        System.out.print("  Book ID to return: ");
        String bookId = sc.nextLine().trim();
        try {
            service.returnBook(bookId);
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doShowHistory() {
        try {
            List<BorrowRecord> records = service.getMyHistory();
            System.out.println("\n  ── BORROW HISTORY ─────────────────────────────");
            System.out.printf("  %-6s %-10s %-12s %-12s %-10s %8s%n",
                "ID","Book","Borrowed","Due","Status","Fine");
            System.out.println("  " + "─".repeat(62));
            for (BorrowRecord r : records) {
                System.out.printf("  %-6d %-10s %-12s %-12s %-10s %8.2f%n",
                    r.getBorrowId(), r.getBookId(),
                    r.getBorrowDate(), r.getDueDate(),
                    r.getStatus(), r.getFineAmount());
            }
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    private void doAddBalance() {
        System.out.print("  Amount to add ₹: ");
        try {
            double amount = Double.parseDouble(sc.nextLine().trim());
            service.addBalance(amount);
        } catch (LMSException | SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("  [ERR] Invalid amount.");
        }
    }

    private void doSearchBooks() {
        System.out.print("  Search title keyword: ");
        String keyword = sc.nextLine().trim();
        try {
            List<Book> books = service.searchBooks(keyword);
            printBookTable(books, "SEARCH RESULTS");
        } catch (SQLException e) {
            System.out.println("  [ERR] " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════

    private void printBookTable(List<Book> books, String header) {
        System.out.println("\n  ── " + header + " ──────────────────────────────────────");
        if (books.isEmpty()) { System.out.println("  (none)"); return; }
        System.out.printf("  %-10s %-30s %-20s %-12s %6s %8s%n",
            "ID","Title","Author","Genre","Avail","₹/day");
        System.out.println("  " + "─".repeat(90));
        for (Book b : books) {
            System.out.printf("  %-10s %-30s %-20s %-12s %6s %8.2f%n",
                b.getBookId(),
                truncate(b.getTitle(), 29),
                truncate(b.getAuthor(), 19),
                b.getGenre(),
                b.getAvailableCopies() + "/" + b.getTotalCopies(),
                b.getPricePerDay());
        }
    }

    private void printBanner() {
        System.out.println("""
            \n
            ██╗     ███╗   ███╗███████╗
            ██║     ████╗ ████║██╔════╝
            ██║     ██╔████╔██║███████╗
            ██║     ██║╚██╔╝██║╚════██║
            ███████╗██║ ╚═╝ ██║███████║
            ╚══════╝╚═╝     ╚═╝╚══════╝
              Library Management System
            """);
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private String pad(String s, int len) {
        return String.format("%-" + len + "s", s.length() > len ? s.substring(0, len) : s);
    }
}
