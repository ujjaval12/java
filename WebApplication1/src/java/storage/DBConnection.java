package storage; // Assuming this is the correct package based on your structure

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    // Make the connection instance variable, not static, if each request should manage its own.
    // Or keep static but handle initialization robustly. Sticking with static for now based on original code.
    private static Connection connection = null;
    private static final String DB_URL_SERVER = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL_APP = "jdbc:mysql://localhost:3306/webappdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Consider externalizing credentials
    private static volatile boolean initialized = false; // Flag to ensure init runs only once

    public static Connection getConnection() throws SQLException { // Propagate SQLException
        if (!initialized) {
            synchronized (DBConnection.class) { // Synchronize initialization block
                if (!initialized) { // Double-checked locking
                    try {
                        initializeDatabase();
                        initialized = true;
                    } catch (ClassNotFoundException | SQLException e) {
                        // Log error more formally (using java.util.logging or SLF4j)
                        System.err.println("FATAL: Database initialization failed!");
                        e.printStackTrace();
                        // Re-throw as a runtime exception or keep SQLException signature
                        throw new SQLException("Database initialization failed", e);
                    }
                }
            }
        }

        // If initialization succeeded, 'connection' should be non-null.
        // If it failed, an exception was thrown.
        // Check if connection is still valid (optional, can be complex)
        if (connection == null || connection.isClosed()) {
             // Maybe re-initialize or throw error? For simplicity, just reconnect:
             System.out.println("Re-establishing connection to webappdb");
             connection = DriverManager.getConnection(DB_URL_APP, DB_USER, DB_PASSWORD);
        }
        
        return connection;
    }

    private static void initializeDatabase() throws ClassNotFoundException, SQLException {
        System.out.println("Attempting database initialization...");
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Step 1: Connect without database to create database if not exists
        // Use try-with-resources for automatic closing
        try (Connection tempConn = DriverManager.getConnection(DB_URL_SERVER, DB_USER, DB_PASSWORD);
             Statement stmt = tempConn.createStatement()) {

            System.out.println("Executing: CREATE DATABASE IF NOT EXISTS webappdb");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS webappdb");
            System.out.println("Database check/create finished.");
        } // tempConn and stmt are automatically closed here

        // Step 2: Connect to webappdb (assign to static variable)
        System.out.println("Connecting to specific database: " + DB_URL_APP);
        connection = DriverManager.getConnection(DB_URL_APP, DB_USER, DB_PASSWORD);
        System.out.println("Connected to webappdb.");

        // Step 3: Create user table if not exists
        // Use try-with-resources
        try (Statement stmt2 = connection.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS user (" +
                                 "id INT AUTO_INCREMENT PRIMARY KEY," +
                                 "name VARCHAR(100) NOT NULL," +
                                 "email VARCHAR(100) NOT NULL UNIQUE," +
                                 "password VARCHAR(255) NOT NULL" + // Consider hashing passwords!
                                 ")";
            System.out.println("Executing: CREATE TABLE IF NOT EXISTS user");
            stmt2.executeUpdate(createTable);
            System.out.println("User table check/create finished.");
        } // stmt2 is automatically closed here
        System.out.println("Database initialization complete.");
    }

    // Optional: Method to close connection on application shutdown
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null; // Reset for potential restart
                initialized = false; // Allow re-initialization if needed
            }
        }
    }
}