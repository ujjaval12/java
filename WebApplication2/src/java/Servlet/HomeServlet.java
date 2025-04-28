package Servlet; // Matches your package name

import conn.DBconnection; // Using your DBconnection class
import model.Event;      // The Event bean (Make sure you created this in the 'model' package)

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Maps requests to the context root ("/") or "/home" to this servlet
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("HomeServlet processing GET request for homepage...");
        List<Event> upcomingEvents = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String dbError = null; // Variable to hold potential DB error message

        try {
            // Use your DBconnection class name
            conn = DBconnection.getConnection();
            if (conn == null) {
                dbError = "Database connection failed. Cannot load events.";
                LOGGER.severe("Failed to get database connection for HomeServlet.");
            } else {
                // Fetch upcoming events (e.g., events with date >= today, limit to 6)
                String sql = "SELECT event_id, title, event_date, location, description, image_path " +
                             "FROM events " +
                             "WHERE event_date >= CURDATE() " + // Only show events from today onwards
                             "ORDER BY event_date ASC " +
                             "LIMIT 6"; // Limit to 6 events for the homepage

                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    // Make sure you have the model.Event class created
                    Event event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setLocation(rs.getString("location")); // Get location
                    event.setDescription(rs.getString("description"));
                    event.setImagePath(rs.getString("image_path"));
                    upcomingEvents.add(event);
                }
                LOGGER.info("Fetched " + upcomingEvents.size() + " upcoming events.");
            }

        } catch (SQLException e) {
            dbError = "Error fetching upcoming events from the database.";
            LOGGER.log(Level.SEVERE, "SQL Error fetching events for homepage", e);
        } catch(Exception e) {
            dbError = "An unexpected error occurred while fetching events.";
            LOGGER.log(Level.SEVERE, "Unexpected error fetching events for homepage", e);
        }
        finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close ResultSet", e); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", e); }
            // Do not close the shared connection from DBconnection
        }

        // Set attributes for the JSP
        if (dbError != null) {
            request.setAttribute("homeError", dbError); // Attribute name matches index.jsp
        }
        request.setAttribute("upcomingEvents", upcomingEvents); // Attribute name matches index.jsp

        // Forward the request to the JSP page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp"); // Forward to index.jsp
        dispatcher.forward(request, response);
    }

    // doPost can simply redirect to doGet for this servlet
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}