package com.carrental;

import com.carrental.exception.CarRentalException;
import com.carrental.enums.CarStatus;
import com.carrental.enums.FuelType;
import com.carrental.models.*;
import com.carrental.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final AuthService    authService    = AuthService.getInstance();
    private static final CarService     carService     = CarService.getInstance();
    private static final BookingService bookingService = BookingService.getInstance();

    private static User currentUser = null;

    public static void main(String[] args) {
        line();
        System.out.println("        CAR RENTAL SYSTEM");
        line();

        while (true) {
            if (currentUser == null)        showGuestMenu();
            else if (currentUser.isAdmin()) showAdminMenu();
            else                            showCustomerMenu();
        }
    }

    // ── MENUS ─────────────────────────────────────────────────────────────────

    static void showGuestMenu() {
        System.out.println("\n1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> doRegister();
            case "2" -> doLogin();
            case "3" -> { System.out.println("Goodbye!"); System.exit(0); }
            default  -> System.out.println("Invalid choice.");
        }
    }

    static void showCustomerMenu() {
        System.out.println("\nWelcome, " + currentUser.getFirstName() + "!");
        System.out.println("1. View available cars");
        System.out.println("2. Search cars by date & get quote");
        System.out.println("3. Book a car");
        System.out.println("4. My bookings");
        System.out.println("5. Cancel a booking");
        System.out.println("6. Logout");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> listAvailableCars();
            case "2" -> searchByDate();
            case "3" -> doBookCar();
            case "4" -> myBookings();
            case "5" -> doCancelBooking();
            case "6" -> { currentUser = null; System.out.println("Logged out."); }
            default  -> System.out.println("Invalid choice.");
        }
    }

    static void showAdminMenu() {
        System.out.println("\nAdmin: " + currentUser.getFirstName());
        System.out.println("1. Add a car");
        System.out.println("2. View all cars");
        System.out.println("3. View all bookings");
        System.out.println("4. Mark booking as done (car returned)");
        System.out.println("5. Cancel a booking");
        System.out.println("6. Logout");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> doAddCar();
            case "2" -> listAllCars();
            case "3" -> listAllBookings();
            case "4" -> doMarkDone();
            case "5" -> doAdminCancel();
            case "6" -> { currentUser = null; System.out.println("Logged out."); }
            default  -> System.out.println("Invalid choice.");
        }
    }

    // ── AUTH ──────────────────────────────────────────────────────────────────

    static void doRegister() {
        line();
        System.out.println("REGISTER");
        line();
        try {
            System.out.print("First name     : "); String fn  = input();
            System.out.print("Last name      : "); String ln  = input();
            System.out.print("Email          : "); String em  = input();
            System.out.print("Password       : "); String pw  = input();
            System.out.print("Phone          : "); String ph  = input();
            System.out.print("License number : "); String lic = input();

            System.out.println("Register as: 1) Customer  2) Admin");
            System.out.print("Choice: ");
            User user = "2".equals(input())
                    ? authService.registerAdmin(fn, ln, em, pw, ph, lic)
                    : authService.register(fn, ln, em, pw, ph, lic);

            System.out.println("Registered as " + user.getRole() + "!");
        } catch (CarRentalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void doLogin() {
        line();
        System.out.println("LOGIN");
        line();
        try {
            System.out.print("Email    : "); String em = input();
            System.out.print("Password : "); String pw = input();
            currentUser = authService.login(em, pw);
            System.out.println("Logged in as " + currentUser.getFullName()
                    + " [" + currentUser.getRole() + "]");
        } catch (CarRentalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── CARS ──────────────────────────────────────────────────────────────────

    static void listAvailableCars() {
        List<Car> cars = carService.getAvailableCars();
        if (cars.isEmpty()) { System.out.println("No available cars right now."); return; }
        line();
        System.out.printf("%-4s %-16s %-13s %-6s %-10s %-6s %-12s%n",
                "ID", "Car", "Plate", "Year", "Category", "Seats", "Rate/Day");
        line();
        for (Car c : cars) {
            System.out.printf("%-4d %-16s %-13s %-6d %-10s %-6d Rs.%-9.0f%n",
                    c.getId(), c.getDisplayName(), c.getLicensePlate(),
                    c.getYear(), c.getCategory(), c.getSeatCapacity(), c.getDailyRate());
        }
        line();
    }

    static void searchByDate() {
        try {
            System.out.print("Start date (dd-MM-yyyy): "); LocalDate s = parseDate(input());
            System.out.print("End date   (dd-MM-yyyy): "); LocalDate e = parseDate(input());
            List<Car> cars = carService.searchAvailable(s, e, null);
            if (cars.isEmpty()) { System.out.println("No cars available for those dates."); return; }
            long days = e.toEpochDay() - s.toEpochDay();
            line();
            System.out.printf("%-4s %-16s %-13s %-10s %-12s %-12s%n",
                    "ID", "Car", "Plate", "Category", "Rate/Day", "TOTAL (" + days + "d)");
            line();
            for (Car c : cars) {
                BigDecimal total = c.getDailyRate().multiply(BigDecimal.valueOf(days));
                System.out.printf("%-4d %-16s %-13s %-10s Rs.%-9.0f Rs. %.2f%n",
                        c.getId(), c.getDisplayName(), c.getLicensePlate(),
                        c.getCategory(), c.getDailyRate(), total);
            }
            line();
        } catch (CarRentalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void doAddCar() {
        line();
        System.out.println("ADD CAR");
        line();
        try {
            System.out.print("Brand                            : "); String brand = input();
            System.out.print("Model                            : "); String model = input();
            System.out.print("Year                             : "); int year  = Integer.parseInt(input());
            System.out.print("License plate                    : "); String plate = input();
            System.out.print("Color                            : "); String color = input();
            System.out.print("Number of seats                  : "); int seats = Integer.parseInt(input());
            System.out.println("Fuel: 1)PETROL  2)DIESEL  3)ELECTRIC  4)HYBRID  5)CNG");
            System.out.print("Choice                           : ");
            FuelType fuel = switch (input()) {
                case "2" -> FuelType.DIESEL;
                case "3" -> FuelType.ELECTRIC;
                case "4" -> FuelType.HYBRID;
                case "5" -> FuelType.CNG;
                default  -> FuelType.PETROL;
            };
            System.out.print("Transmission (MANUAL/AUTOMATIC)  : "); String trans = input();
            System.out.print("Daily rate (Rs.)                 : "); BigDecimal rate = new BigDecimal(input());
            System.out.print("Category (ECONOMY/SUV/LUXURY/VAN): "); String cat  = input();
            System.out.print("Description                      : "); String desc = input();

            Car car = carService.addCar(brand, model, year, plate, color,
                    seats, fuel, trans, rate, cat, desc, null);
            System.out.println("Car added! ID = " + car.getId());
        } catch (CarRentalException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void listAllCars() {
        List<Car> cars = carService.getAllCars();
        if (cars.isEmpty()) { System.out.println("No cars in fleet."); return; }
        line();
        System.out.printf("%-4s %-16s %-13s %-10s %-12s %-12s%n",
                "ID", "Car", "Plate", "Category", "Rate/Day", "Status");
        line();
        for (Car c : cars) {
            System.out.printf("%-4d %-16s %-13s %-10s Rs.%-9.0f %-12s%n",
                    c.getId(), c.getDisplayName(), c.getLicensePlate(),
                    c.getCategory(), c.getDailyRate(), c.getStatus());
        }
        line();
    }

    // ── BOOKINGS ──────────────────────────────────────────────────────────────

    static void doBookCar() {
        listAvailableCars();
        try {
            System.out.print("Car ID to book           : "); Long carId  = Long.parseLong(input());
            System.out.print("Start date (dd-MM-yyyy)  : "); LocalDate s = parseDate(input());
            System.out.print("End date   (dd-MM-yyyy)  : "); LocalDate e = parseDate(input());
            System.out.print("Pickup location          : "); String pickup  = input();
            System.out.print("Dropoff location         : "); String dropoff = input();
            System.out.print("Notes (Enter to skip)    : "); String notes   = input();

            Booking booking = bookingService.createBooking(
                    currentUser, carId, s, e, notes, pickup, dropoff);
            printBill(booking);
        } catch (CarRentalException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void myBookings() {
        List<Booking> bookings = bookingService.getMyBookings(currentUser);
        if (bookings.isEmpty()) { System.out.println("No bookings found."); return; }
        printBookingTable(bookings);
    }

    static void doCancelBooking() {
        myBookings();
        try {
            System.out.print("Booking ID to cancel: "); Long id = Long.parseLong(input());
            bookingService.cancelBooking(id, currentUser);
            System.out.println("Booking cancelled.");
        } catch (CarRentalException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void listAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings.isEmpty()) { System.out.println("No bookings yet."); return; }
        printBookingTable(bookings);
    }

    static void doMarkDone() {
        listAllBookings();
        try {
            System.out.print("Booking ID to mark done: "); Long id = Long.parseLong(input());
            bookingService.markDone(id);
            System.out.println("Booking marked done — car is available again.");
        } catch (CarRentalException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void doAdminCancel() {
        listAllBookings();
        try {
            System.out.print("Booking ID to cancel: "); Long id = Long.parseLong(input());
            bookingService.cancelBooking(id, currentUser);
            System.out.println("Booking cancelled.");
        } catch (CarRentalException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── BILL ──────────────────────────────────────────────────────────────────

    static void printBill(Booking b) {
        line();
        System.out.println("              BOOKING BILL");
        line();
        System.out.printf("  Booking Ref  : %s%n",  b.getBookingReference());
        System.out.printf("  Customer     : %s%n",  b.getCustomer().getFullName());
        System.out.printf("  Car          : %s (%s)%n",
                b.getCar().getDisplayName(), b.getCar().getLicensePlate());
        System.out.printf("  Category     : %s%n",  b.getCar().getCategory());
        System.out.printf("  Pickup       : %s%n",  b.getPickupLocation());
        System.out.printf("  Dropoff      : %s%n",  b.getDropoffLocation());
        System.out.printf("  From         : %s%n",  b.getStartDate());
        System.out.printf("  To           : %s%n",  b.getEndDate());
        System.out.printf("  Duration     : %d day(s)%n", b.getNumberOfDays());
        System.out.printf("  Rate / Day   : Rs. %.2f%n", b.getCar().getDailyRate());
        line();
        System.out.printf("  TOTAL AMOUNT : Rs. %.2f%n", b.getTotalAmount());
        line();
        System.out.printf("  Status       : %s%n", b.getStatus());
        line();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    static void printBookingTable(List<Booking> bookings) {
        line();
        System.out.printf("%-4s %-26s %-14s %-12s %-12s %-10s %-10s%n",
                "ID", "Reference", "Car", "Start", "End", "Amount", "Status");
        line();
        for (Booking b : bookings) {
            System.out.printf("%-4d %-26s %-14s %-12s %-12s Rs.%-7.0f %-10s%n",
                    b.getId(), b.getBookingReference(), b.getCar().getDisplayName(),
                    b.getStartDate(), b.getEndDate(), b.getTotalAmount(), b.getStatus());
        }
        line();
    }

    static String input() { return scanner.nextLine().trim(); }

    static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new CarRentalException("Invalid date. Use dd-MM-yyyy  e.g. 20-04-2026");
        }
    }

    static void line() {
        System.out.println("--------------------------------------------------");
    }
}
