# 🚗 DriveX – Car Rental System

DriveX is a **Java-based console application** that simulates a complete car rental system.
It allows users to register, search cars, make bookings, and manage rentals, while admins can manage cars and bookings.

---

## 🎯 Key Features

### 👤 User Features (Customer)

* Register & Login system
* View available cars
* Search cars by date range
* Get pricing quote before booking
* Book cars with pickup & drop locations
* View personal bookings
* Cancel bookings

---

### 🛠️ Admin Features

* Add new cars to the system
* View all cars in fleet
* View all bookings
* Mark booking as completed (car returned)
* Cancel any booking

---

## 🧠 Core Functionalities

* 🔐 **Authentication System**

    * Role-based login (Admin / Customer)
    * Secure validation using service layer

* 🚘 **Car Management**

    * Add cars with details like:

        * Brand, Model, Year
        * Fuel Type (Petrol, Diesel, Electric, etc.)
        * Transmission
        * Category (SUV, Economy, Luxury, etc.)
        * Daily rental price

* 📅 **Booking System**

    * Date-based availability check
    * Automatic cost calculation:

        * Based on number of days
    * Booking reference generation
    * Booking status tracking

* 💰 **Billing System**

    * Displays:

        * Customer details
        * Car details
        * Duration
        * Total cost
        * Booking status

---

## 🏗️ Project Architecture

The project follows a **layered architecture**:

* **Models** → Entities like User, Car, Booking
* **Services** → Business logic (AuthService, CarService, BookingService)
* **Enums** → CarStatus, FuelType
* **Exception Handling** → Custom exception (`CarRentalException`)
* **Main Class** → CLI interface & user interaction

---

## 📁 Project Structure

```
com.carrental
│
├── models/        → User, Car, Booking
├── service/       → AuthService, CarService, BookingService
├── enums/         → CarStatus, FuelType
├── exception/     → Custom exceptions
└── Main.java      → Entry point (CLI application)
```

---

## 🚀 How to Run

1. Clone the repository:

   ```
   git clone https://github.com/hazimu55-cmd/DriveX.git
   ```

2. Open in IDE (IntelliJ / Eclipse)

3. Navigate to:

   ```
   Main.java
   ```

4. Run the application

---

## 🖥️ Sample Workflow

```
1. Register → Login
2. View cars → Search by date
3. Select car → Book
4. View booking bill
5. Cancel or complete booking
```

---

## 📅 Date Format

All date inputs must follow:

```
dd-MM-yyyy
Example: 20-04-2026
```

---

## ⚙️ Technologies Used

* Java
* OOP (Object-Oriented Programming)
* Collections Framework
* Exception Handling
* CLI-based Interface

---

## 📌 Current Status

✅ Backend (Core Logic) Completed
⏳ Frontend / UI – Planned

---

## 🚀 Future Improvements

* Web-based frontend (React / Spring Boot UI)
* Database integration (MySQL/PostgreSQL)
* JWT Authentication
* Payment integration
* Admin dashboard

---

## 👨‍💻 Author

Hazim Uddin
Aspiring Software Engineer & Entrepreneur

