package servlet;

import management.LoginManagement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String adminEmail = request.getParameter("adminEmail");
        String adminPassword = request.getParameter("adminPassword");

        boolean isValidAdmin = LoginManagement.validateAdmin(adminEmail, adminPassword);

        if (isValidAdmin) {
            HttpSession session = request.getSession();
            session.setAttribute("adminEmail", adminEmail);
            response.sendRedirect("adminHome.jsp");
        } else {
            response.sendRedirect("login.jsp?error=Invalid Admin Credentials");
        }
    }
}
