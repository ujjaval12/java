<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<!DOCTYPE html>
<html>
<head>
    <title>PetFestHub - Event</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f2f4f5;
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

        nav a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            font-size: 14px;
        }

        .hero {
            text-align: center;
            padding: 40px 20px 10px;
        }

        .hero img {
            width: 100%;
            max-width: 900px;
            height: 200px;
            background: #666;
            object-fit: cover;
            border-radius: 8px;
        }

        .hero h2 {
            margin-top: 20px;
            font-size: 20px;
            color: #444;
        }


        @keyframes slide {
            0% { margin-left: 0%; }
            33% { margin-left: 0%; }
            36% { margin-left: -100%; }
            66% { margin-left: -100%; }
            69% { margin-left: -200%; }
            99% { margin-left: -200%; }
            100% { margin-left: 0%; }
        }

        .section-title {
            text-align: center;
            font-size: 28px;
            font-weight: bold;
            margin: 40px 0 20px 0;
            position: relative;
            color: #333;
        }

        .section-title::after {
            content: "";
            display: block;
            width: 80px;
            height: 3px;
            background-color: #8a2be2;
            margin: 10px auto 0;
            border-radius: 2px;
        }

.cards {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 60px; /* Increased gap from 20px to 40px */
    padding: 0 20px 60px 20px;
    max-width: 1000px;
    margin: 0 auto;
}

.card {
    flex: 0 1 calc(33.33% - 40px); /* Adjust width based on new gap */
    box-sizing: border-box;
    background: white;
    border-radius: 8px;
    box-shadow: 0 0 5px rgba(0,0,0,0.1);
    overflow: hidden;
    margin: 50px auto;
    margin-bottom: 0px; /* Extra space between rows */
    margin-top: 40px;
    padding: 40px;
}
        .card img {
            width: 100%;
            height: 150px;
            object-fit: cover;
        }

        .card-content {
            padding: 15px;
        }

        .card-title {
            font-weight: bold;
            margin-bottom: 5px;
        }

        .card-date {
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;
        }

        .card a {
            text-decoration: none;
            background: #8a2be2;
            color: white;
            padding: 8px 12px;
            display: inline-block;
            border-radius: 4px;
            font-size: 14px;
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





<!-- Event Cards -->
<div class="cards">
    <div class="card">
        <img src="Screenshot 2025-04-10 145925.png" alt="Doggy Day Out">
        <div class="card-content">
            <div class="card-title">Doggy Day Out</div>
            <div class="card-date">2023-07-15</div>
            <a href="#">Learn More</a>
        </div>
    </div>

    <div class="card">
        <img src="images.jpg" alt="Cat Fashion Show">
        <div class="card-content">
            <div class="card-title">Cat Fashion Show</div>
            <div class="card-date">2023-07-22</div>
            <a href="#">Learn More</a>
        </div>
    </div>

    <div class="card">
        <img src="Screenshot 2025-04-10 145925.png" alt="Pet Adoption Fair">
        <div class="card-content">
            <div class="card-title">Pet Adoption Fair</div>
            <div class="card-date">2023-07-29</div>
            <a href="#">Learn More</a>
        </div>
    </div>
      <div class="card">
        <img src="images.jpg" alt="Pet Adoption Fair">
        <div class="card-content">
            <div class="card-title">Pet Adoption Fair</div>
            <div class="card-date">2023-07-29</div>
            <a href="#">Learn More</a>
        </div>
    </div>
      <div class="card">
        <img src="Screenshot 2025-04-10 145925.png" alt="Pet Adoption Fair">
        <div class="card-content">
            <div class="card-title">Pet Adoption Fair</div>
            <div class="card-date">2023-07-29</div>
            <a href="#">Learn More</a>
        </div>
    </div>
      <div class="card">
        <img src="images.jpg" alt="Pet Adoption Fair">
        <div class="card-content">
            <div class="card-title">Pet Adoption Fair</div>
            <div class="card-date">2023-07-29</div>
            <a href="#">Learn More</a>
        </div>
    </div>
</div>

</body>
</html>

