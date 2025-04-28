package Servlet; // Your package name

import conn.DBconnection;
// *** CORRECT IMPORT: Import YOUR Event bean from the model package ***
import model.Event;

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
// *** CORRECT IMPORT for List ***
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ViewEventsServlet") // Matches link in navigation/JSPs
public class ViewEventsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ViewEventsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("ViewEventsServlet processing GET request...");
        // *** CORRECT LIST TYPE: Use model.Event ***
        List<model.Event> allEvents = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String dbError = null; // Variable to hold error

        try {
            conn = DBconnection.getConnection();
            if (conn == null) {
                dbError = "Database connection failed. Cannot load events.";
                LOGGER.severe("Failed to get DB connection for ViewEventsServlet.");
            } else {
                // Fetch ALL approved events, ordered by date
                String sql = "SELECT event_id, title, event_date, location, description, image_path " +
                             "FROM events " + // Assumes events table contains only approved events
                             "ORDER BY event_date ASC"; // Or DESC for newest first

                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    // *** CORRECT BEAN INSTANTIATION: Use model.Event ***
                    model.Event event = new model.Event();
                    // *** Setter methods now belong to model.Event ***
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setLocation(rs.getString("location"));
                    event.setDescription(rs.getString("description"));
                    event.setImagePath(rs.getString("image_path"));
                    allEvents.add(event); // Add the correct type of object
                }
                LOGGER.info("Fetched " + allEvents.size() + " total events.");
            }
        } catch (SQLException e) {
            dbError = "Error fetching events from database.";
            LOGGER.log(Level.SEVERE, "SQL Error fetching all events", e);
        } catch (Exception e) {
             dbError = "An unexpected error occurred while fetching events.";
            LOGGER.log(Level.SEVERE, "Unexpected error fetching all events", e);
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close ResultSet", e); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", e); }
             // Do not close shared connection
        }

        // Set attributes for the JSP
        if (dbError != null) {
            request.setAttribute("eventsError", dbError); // Matches events.jsp
        }
        request.setAttribute("allEvents", allEvents); // Matches events.jsp

        // Forward the request to the events JSP page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/events.jsp");
        dispatcher.forward(request, response);
    }

     // Optional: doPost can simply call doGet if you don't need separate POST logic
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
     }
}