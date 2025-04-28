package management;

import storage.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManagement {

public static boolean validateUser(String email, String password) {
    boolean status = false;
    Connection conn = null; // Declare outside try
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        System.out.println("LoginManagement: Attempting to get DB connection..."); // Add logging
        conn = DBConnection.getConnection();
        System.out.println("LoginManagement: Connection received: " + (conn != null)); // Add logging

        if (conn == null) {
             System.err.println("LoginManagement: Received null connection!");
             return false; // Or throw an exception
        }

        String sql = "SELECT * FROM user WHERE email = ? AND password = ?"; // WARNING: Storing plain passwords is bad practice!
        ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, password); // Compare hashed passwords instead

        rs = ps.executeQuery();
        status = rs.next(); // true if a matching user was found
        System.out.println("LoginManagement: User validation status for " + email + ": " + status); // Add logging

    } catch (SQLException e) {
        // Log the exception properly (using java.util.logging or SLF4j is better)
        System.err.println("LoginManagement: SQL Error during user validation!");
        e.printStackTrace(); // Keep this for detailed logs
        // Optionally re-throw as a ServletException to signal a bigger problem
        // throw new ServletException("Database error during login validation", e);
        status = false; // Ensure status is false on error
    } catch (Exception e) { // Catch other potential runtime errors
         System.err.println("LoginManagement: Unexpected Error during user validation!");
         e.printStackTrace();
         status = false;
    } finally {
        // Close resources in finally block
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        // Avoid closing the static connection here unless you manage it differently
        // try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
    return status;
}

    public static boolean validateAdmin(String adminEmail, String adminPassword) {
        // Hardcoded Admin Login
        String correctAdminEmail = "admin@gmail.com";
        String correctAdminPassword = "admin123";

        return adminEmail.equals(correctAdminEmail) && adminPassword.equals(correctAdminPassword);
    }
}
