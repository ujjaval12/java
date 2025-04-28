<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - PetFestHub</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f2f4f5;
            display: flex;
            flex-direction: column;
            height: 100vh;
        }

        header {
            background-color: #8a2be2;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header .logo {
            font-size: 20px;
            font-weight: bold;
        }

        nav {
            display: flex;
        }

        nav a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            font-size: 14px;
        }

        .main-content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 30px;
            flex-wrap: wrap;
            padding: 20px;
        }

        .login-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 40px 30px;
            max-width: 400px;
            width: 100%;
            text-align: center;
        }

        .login-container h2 {
            margin-bottom: 20px;
            color: #444;
        }

        .login-container input[type="email"],
        .login-container input[type="password"] {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }

        .login-container button {
            background: #8a2be2;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            width: 100%;
        }

        .login-container button:hover {
            background: #7a1da3;
        }

        .signup-link {
            margin-top: 15px;
            font-size: 14px;
        }

        .signup-link a {
            color: #8a2be2;
            text-decoration: none;
        }
    </style>
</head>
<body>

<header>
    <div class="logo">PetFestHub</div>
    <nav>
        <a href="index.html">Home</a>
        <a href="events.jsp">Events</a>
        <a href="gallery.jsp">Gallery</a>
        <a href="create-event.jsp">Create Event</a>
        <a href="login.jsp">Login/Signup</a>
    </nav>
</header>

<%-- Display error message if present --%>
<%
    String error = request.getParameter("error");
    if (error != null) {
%>
    <div style="color: red; text-align: center; margin-top: 10px;">
        <%= error %>
    </div>
<%
    }
%>

<div class="main-content">
    <!-- User Login -->
    <div class="login-container">
        <h2>User Login</h2>
        <form action="UserLoginServlet" method="post">
            <input type="email" name="email" placeholder="User Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
        <div class="signup-link">
            Don’t have an account? <a href="signup.jsp">Sign Up</a>
        </div>
    </div>

    <!-- Admin Login -->
    <div class="login-container">
        <h2>Admin Login</h2>
        <form action="AdminLoginServlet" method="post">
            <input type="email" name="adminEmail" placeholder="Admin Email" required>
            <input type="password" name="adminPassword" placeholder="Password" required>
            <button type="submit">Login as Admin</button>
        </form>
    </div>
</div>

</body>
</html>
