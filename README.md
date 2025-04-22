# Hotel Reservation System – Java Desktop App

This project is a **Java-based desktop application** developed for a hotel to streamline the room reservation and billing process. Designed using the **Model-View-Controller (MVC)** architecture, the system supports both **admin** and **kiosk (guest)** operations, complete with database integration and multithreaded communication.

## 🛠 Features

- 📋 **Room Reservation** – Book rooms based on number of guests and room type.
- 💳 **Billing System** – Automatically generate bills with support for admin-applied discounts.
- 🔐 **Admin Portal** – Secure login for staff to manage bookings, search guests, and handle checkouts.
- 🧾 **Booking Confirmation** – Displays full booking summary including taxes and pricing before confirmation.
- 🧑‍💻 **Self-Booking Kiosk** – Contactless guest check-in through a guided GUI flow.
- 💾 **Data Persistence** – Guest info, reservations, and billing data stored in a MySQL database.
- 🚫 **Validation** – All user input is strictly validated for correctness (e.g., dates, email, numeric fields).

## 📷 Screenshot

> *(Add a screenshot of your application UI here if available)*

## 🧱 Tech Stack

- **Java** – Core language
- **JavaFX / Swing** – UI framework
- **MySQL** – Database
- **JDBC** – Database connection
- **Multithreading** – For concurrent admin and kiosk support
- **Eclipse IDE** – Development environment

## 📂 Folder Structure

```plaintext
HotelReservationSystem/
│
├── src/                # Java source files
├── assets/             # Icons and GUI assets
├── database/           # MySQL schema and sample data
├── README.md
└── hotel-reservation.jar (optional executable)
