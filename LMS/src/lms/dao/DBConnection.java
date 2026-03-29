package lms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager.
 *
 * TARGET: MySQL 8.0 running in Docker container (mysql-lab)
 *   Host : 127.0.0.1  (localhost inside WSL2)
 *   Port : 3306
 *   DB   : lms_db
 *   User : root
 *   Pass : root
 *
 * Driver: mysql-connector-j (place JAR in lib/)
 * Single-terminal system — no connection pooling needed.
 */
public class DBConnection {

    // ── MySQL Docker config ──────────────────────────────────
    private static final String URL =
        "jdbc:mysql://127.0.0.1:3306/lms_db" +
        "?useSSL=false" +
        "&allowPublicKeyRetrieval=true" +
        "&serverTimezone=Asia/Kolkata";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    private DBConnection() {}

    /**
     * Returns the single shared Connection.
     * Lazily initialises on first call; re-opens if closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                connection.setAutoCommit(true);
                System.out.println("[DB] Connected to MySQL (lms_db) successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "MySQL JDBC driver not found.\n" +
                    "Download mysql-connector-j-*.jar and place it in the lib/ folder.", e);
            }
        }
        return connection;
    }

    /** Gracefully close the connection at shutdown. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }
}
