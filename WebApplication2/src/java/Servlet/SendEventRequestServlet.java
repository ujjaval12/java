package Servlet;

import conn.DBconnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/SendEventRequestServlet") // Matches form action
public class SendEventRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SendEventRequestServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String eventName = request.getParameter("eventName");
        String location = request.getParameter("location");
        String dateStr = request.getParameter("date");
        String description = request.getParameter("description");

        String redirectPage = "create-event.jsp"; // Redirect back here

        // Basic Validation
        if (eventName == null || eventName.trim().isEmpty() ||
            location == null || location.trim().isEmpty() ||
            dateStr == null || dateStr.trim().isEmpty() ||
            description == null || description.trim().isEmpty()) {
            response.sendRedirect(redirectPage + "?error=Please fill in all fields.");
            return;
        }

        java.sql.Date sqlDate = null;
        try {
            // Convert HTML date input (yyyy-MM-dd) to java.sql.Date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = format.parse(dateStr);
            sqlDate = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
             LOGGER.log(Level.WARNING, "Invalid date format received: " + dateStr, e);
             response.sendRedirect(redirectPage + "?error=Invalid date format. Please use YYYY-MM-DD.");
             return;
        }


        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBconnection.getConnection();
            if (conn == null) {
                 LOGGER.severe("Failed to get DB connection for event request.");
                 response.sendRedirect(redirectPage + "?error=Server error. Could not save request.");
                 return;
            }

            String sql = "INSERT INTO event_requests (event_name, location, requested_date, description, status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, eventName);
            pstmt.setString(2, location);
            pstmt.setDate(3, sqlDate);
            pstmt.setString(4, description);
            pstmt.setString(5, "PENDING"); // Initial status

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("New event request submitted: " + eventName);
                response.sendRedirect(redirectPage + "?message=Event request sent successfully! An admin will review it.");
            } else {
                LOGGER.warning("Event request insertion failed for: " + eventName);
                response.sendRedirect(redirectPage + "?error=Failed to send request. Please try again.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error submitting event request: " + eventName, e);
            response.sendRedirect(redirectPage + "?error=Database error submitting request.");
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", e); }
            // Not closing shared connection
        }
    }
}