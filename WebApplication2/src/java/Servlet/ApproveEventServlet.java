package Servlet;

import conn.DBconnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*; // Using java.sql.* for Date and Timestamp
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ApproveEventServlet")
public class ApproveEventServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ApproveEventServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Authorization Check
        if (session == null || session.getAttribute("isAdmin") == null || !((Boolean) session.getAttribute("isAdmin"))) {
            response.sendRedirect("login.jsp?adminError=Authentication required.");
            return;
        }

        String requestIdStr = request.getParameter("requestId");
        String redirectPage = "AdminDashboardServlet"; // Redirect back to dashboard

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.warning("Invalid request ID received for approval: " + requestIdStr);
            response.sendRedirect(redirectPage + "?error=Invalid request ID.");
            return;
        }

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBconnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to get DB connection for event approval.");
                response.sendRedirect(redirectPage + "?error=Database connection failed.");
                return;
            }

            // ** Important: Use transaction for atomicity **
            conn.setAutoCommit(false); // Start transaction

            // 1. Fetch the request details
            String selectSql = "SELECT event_name, location, requested_date, description FROM event_requests WHERE request_id = ? AND status = ?";
            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, requestId);
            selectStmt.setString(2, "PENDING"); // Ensure we only approve pending ones
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                String eventName = rs.getString("event_name");
                String location = rs.getString("location");
                Date eventDate = rs.getDate("requested_date");
                String description = rs.getString("description");

                // 2. Insert into the main events table
                // Note: image_path is null initially, admin might need to add it later
                String insertSql = "INSERT INTO events (title, event_date, location, description) VALUES (?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, eventName);
                insertStmt.setDate(2, eventDate);
                insertStmt.setString(3, location);
                insertStmt.setString(4, description);

                int insertRows = insertStmt.executeUpdate();

                if (insertRows > 0) {
                    // 3. Update the request status to APPROVED
                    String updateSql = "UPDATE event_requests SET status = ? WHERE request_id = ?";
                    updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, "APPROVED");
                    updateStmt.setInt(2, requestId);

                    int updateRows = updateStmt.executeUpdate();

                    if (updateRows > 0) {
                        conn.commit(); // Commit transaction if all steps succeed
                        LOGGER.info("Event request ID " + requestId + " approved and event created: " + eventName);
                        response.sendRedirect(redirectPage + "?message=Event approved successfully!");
                    } else {
                        conn.rollback(); // Rollback if update fails
                        LOGGER.severe("Failed to update request status after inserting event for request ID: " + requestId);
                        response.sendRedirect(redirectPage + "?error=Failed to update request status.");
                    }
                } else {
                     conn.rollback(); // Rollback if insert fails
                     LOGGER.severe("Failed to insert event into events table for request ID: " + requestId);
                     response.sendRedirect(redirectPage + "?error=Failed to create event from request.");
                }

            } else {
                // Request not found or not pending
                 conn.rollback(); // No changes made, but good practice
                 LOGGER.warning("Attempt to approve non-existent or already processed request ID: " + requestId);
                 response.sendRedirect(redirectPage + "?error=Request not found or already processed.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error during event approval for request ID: " + requestId, e);
             try {
                 if (conn != null) conn.rollback(); // Rollback on error
             } catch (SQLException ex) {
                 LOGGER.log(Level.SEVERE, "Failed to rollback transaction", ex);
             }
            response.sendRedirect(redirectPage + "?error=Database error during approval.");
        } finally {
            // Close all resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Log */ }
            try { if (selectStmt != null) selectStmt.close(); } catch (SQLException e) { /* Log */ }
            try { if (insertStmt != null) insertStmt.close(); } catch (SQLException e) { /* Log */ }
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) { /* Log */ }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    // Do not close the shared connection here
                }
            } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Failed to reset auto-commit", e); }
        }
    }
}