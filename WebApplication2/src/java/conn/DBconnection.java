package conn; // Package name matching your structure

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

// Class name matching your filename (DBconnection)
public class DBconnection {

    // --- Database Credentials ---
    private static final String DB_USER = "root"; // CHANGE IF YOUR USER IS DIFFERENT
    private static final String DB_PASSWORD = "root"; // CHANGE IF YOUR PASSWORD IS DIFFERENT

    // --- JDBC URLs (Using sslMode=DISABLED) ---
    private static final String DB_NAME = "webappdb2"; // Your desired DB name
    private static final String DB_URL_SERVER = "jdbc:mysql://localhost:3306/?user=" + DB_USER + "&password=" + DB_PASSWORD + "&sslMode=DISABLED";
    private static final String DB_URL_APP = "jdbc:mysql://localhost:3306/" + DB_NAME + "?user=" + DB_USER + "&password=" + DB_PASSWORD + "&sslMode=DISABLED";


    // --- Logger ---
    private static final Logger LOGGER = Logger.getLogger(DBconnection.class.getName());

    // --- Connection instance ---
    private static volatile Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (DBconnection.class) {
                if (connection == null) {
                    try {
                        LOGGER.info("Attempting database initialization for " + DB_NAME + "...");
                        initializeDatabase();
                        LOGGER.info("Database connection established successfully to " + DB_NAME + ".");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "FATAL: Database initialization failed for " + DB_NAME + "!", e);
                        return null; // Indicate failure
                    }
                }
            }
        }
        return connection;
    }


    private static void initializeDatabase() throws ClassNotFoundException, SQLException {
        LOGGER.info("Loading MySQL JDBC driver (com.mysql.cj.jdbc.Driver)...");
        Class.forName("com.mysql.cj.jdbc.Driver");
        LOGGER.info("Driver loaded.");

        // Step 1: Connect to server to create database if needed
        LOGGER.info("Connecting to MySQL server to check/create database...");
        try (Connection tempConn = DriverManager.getConnection(DB_URL_SERVER);
             Statement stmt = tempConn.createStatement()) {
            LOGGER.info("Executing: CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            LOGGER.info("Database '" + DB_NAME + "' checked/created.");
        } // tempConn and stmt automatically closed

        // Step 2: Connect to the specific application database
        LOGGER.info("Connecting to specific database: " + DB_NAME + "...");
        connection = DriverManager.getConnection(DB_URL_APP);
        LOGGER.info("Connected to " + DB_NAME + ".");

        // Step 3: Create user table if needed **** CORRECTED DEFINITION ****
        LOGGER.info("Checking/Creating 'user' table...");
        try (Statement stmtUser = connection.createStatement()) {
            // --- THIS IS THE CORRECTED PART ---
            String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                                     "name VARCHAR(100) NOT NULL," +
                                     "email VARCHAR(100) NOT NULL UNIQUE," +
                                     "password VARCHAR(255) NOT NULL" + // Storing plain text for now
                                     ")";
            // --- END CORRECTION ---
            stmtUser.executeUpdate(createUserTable);
            LOGGER.info("'user' table checked/created.");
        }

        // Step 4: Create/Update events table
        LOGGER.info("Checking/Creating/Updating 'events' table...");
        try (Statement stmtEvent = connection.createStatement()) {
            try {
                // Attempt to add location column (best effort)
                stmtEvent.executeUpdate("ALTER TABLE events ADD COLUMN location VARCHAR(255) NULL");
                LOGGER.info("Column 'location' potentially added to 'events' table.");
            } catch (SQLException ignored) {
                 // Ignore error code 1060 (Duplicate column name)
                 if (ignored.getErrorCode() == 1060) {
                    LOGGER.fine("Ignoring ALTER TABLE error for 'location' (column already exists).");
                 } else {
                    // Log other potential ALTER errors if needed
                    LOGGER.log(Level.WARNING, "ALTER TABLE error for 'location'", ignored);
                 }
            }

            // Create table if it doesn't exist, including the new column
            String createEventsTable = "CREATE TABLE IF NOT EXISTS events (" +
                                       "event_id INT AUTO_INCREMENT PRIMARY KEY," +
                                       "title VARCHAR(255) NOT NULL," +
                                       "event_date DATE," +
                                       "location VARCHAR(255)," + // Location column included
                                       "description TEXT," +
                                       "image_path VARCHAR(500)," +
                                       "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                       ")";
            stmtEvent.executeUpdate(createEventsTable);
            LOGGER.info("'events' table checked/created/updated.");
        }

        // Step 5: Create event_requests table
        LOGGER.info("Checking/Creating 'event_requests' table...");
        try (Statement stmtRequest = connection.createStatement()) {
            String createRequestTable = "CREATE TABLE IF NOT EXISTS event_requests (" +
                                        "request_id INT AUTO_INCREMENT PRIMARY KEY," +
                                        "event_name VARCHAR(255) NOT NULL," +
                                        "location VARCHAR(255)," +
                                        "requested_date DATE," +
                                        "description TEXT," +
                                        "status VARCHAR(20) DEFAULT 'PENDING'," +
                                        "requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                        ")";
            stmtRequest.executeUpdate(createRequestTable);
            LOGGER.info("'event_requests' table checked/created.");
        }
    } // End initializeDatabase


    // Method to close connection
    public static void closeConnection() {
        synchronized (DBconnection.class) {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null; // Reset the static variable
                    LOGGER.info("Database connection closed.");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing database connection", e);
                }
            }
        }
    }
} // End class DBconnection