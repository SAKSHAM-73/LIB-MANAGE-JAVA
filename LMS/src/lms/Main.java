package lms;

import lms.dao.DBConnection;
import lms.dao.LibraryService;
import lms.ui.ConsoleUI;

import java.sql.SQLException;

/**
 * ╔══════════════════════════════════════════════════════════╗
 *  Library Management System — Entry Point
 *  Author : Saksham (23053073, KIIT SCE)
 *  Stack  : Java 17 + JDBC + Oracle 21c
 * ╚══════════════════════════════════════════════════════════╝
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Eagerly establish DB connection
            DBConnection.getConnection();

            // Bootstrap service (pre-loads books into hash table)
            LibraryService service = new LibraryService();

            // Launch terminal UI
            ConsoleUI ui = new ConsoleUI(service);
            ui.start();

        } catch (SQLException e) {
            System.err.println("[FATAL] Database connection failed: " + e.getMessage());
            System.err.println("  Make sure Oracle 21c is running and ojdbc11.jar is on classpath.");
            System.exit(1);
        }
    }
}
