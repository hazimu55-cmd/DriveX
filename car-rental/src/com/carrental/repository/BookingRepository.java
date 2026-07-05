package com.carrental.repository;

import com.carrental.models.Booking;
import com.carrental.models.Car;
import com.carrental.models.User;
import com.carrental.enums.BookingStatus;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BookingRepository {

    private static BookingRepository instance;

    private BookingRepository() {}

    public static BookingRepository getInstance() {
        if (instance == null) instance = new BookingRepository();
        return instance;
    }

    public Booking save(Booking booking) {
        String sql = "INSERT INTO bookings (id, booking_reference, customer_id, car_id, start_date, end_date, " +
                     "total_amount, status, notes, pickup_location, dropoff_location) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "status = VALUES(status), notes = VALUES(notes), " +
                     "pickup_location = VALUES(pickup_location), dropoff_location = VALUES(dropoff_location), " +
                     "updated_at = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, booking.getId());
            stmt.setString(2, booking.getBookingReference());
            stmt.setLong(3, booking.getCustomer().getId());
            stmt.setLong(4, booking.getCar().getId());
            stmt.setDate(5, Date.valueOf(booking.getStartDate()));
            stmt.setDate(6, Date.valueOf(booking.getEndDate()));
            stmt.setBigDecimal(7, booking.getTotalAmount());
            stmt.setString(8, booking.getStatus().name());
            stmt.setString(9, booking.getNotes());
            stmt.setString(10, booking.getPickupLocation());
            stmt.setString(11, booking.getDropoffLocation());

            stmt.executeUpdate();
            System.out.println("✅ Booking saved to database: " + booking.getBookingReference());
            return booking;

        } catch (SQLException e) {
            System.err.println("Error saving booking: " + e.getMessage());
            throw new RuntimeException("Failed to save booking", e);
        }
    }

    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToBooking(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error finding booking by id: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Booking> findByReference(String reference) {
        String sql = "SELECT * FROM bookings WHERE booking_reference = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reference);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToBooking(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error finding booking by reference: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
            return bookings;

        } catch (SQLException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
            return bookings;
        }
    }

    public List<Booking> findByCustomerId(Long customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
            return bookings;

        } catch (SQLException e) {
            System.err.println("Error finding bookings by customer: " + e.getMessage());
            return bookings;
        }
    }

    public List<Booking> findByStatus(BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
            return bookings;

        } catch (SQLException e) {
            System.err.println("Error finding bookings by status: " + e.getMessage());
            return bookings;
        }
    }

    /** Conflict check — only ACTIVE bookings can block a car */
    public List<Booking> findConflicting(Long carId, LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE car_id = ? AND status = 'ACTIVE' " +
                     "AND start_date < ? AND end_date > ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, carId);
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setDate(3, Date.valueOf(startDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
            return bookings;

        } catch (SQLException e) {
            System.err.println("Error finding conflicting bookings: " + e.getMessage());
            return bookings;
        }
    }

    /** Car IDs blocked by ACTIVE bookings during a date range */
    public Set<Long> findBookedCarIdsBetween(LocalDate startDate, LocalDate endDate) {
        Set<Long> carIds = new HashSet<>();
        String sql = "SELECT DISTINCT car_id FROM bookings WHERE status = 'ACTIVE' " +
                     "AND start_date < ? AND end_date > ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(endDate));
            stmt.setDate(2, Date.valueOf(startDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                carIds.add(rs.getLong("car_id"));
            }
            return carIds;

        } catch (SQLException e) {
            System.err.println("Error finding booked car IDs: " + e.getMessage());
            return carIds;
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Error counting bookings: " + e.getMessage());
            return 0;
        }
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        // Need to fetch User and Car from their repositories
        UserRepository userRepo = UserRepository.getInstance();
        CarRepository carRepo = CarRepository.getInstance();

        Long customerId = rs.getLong("customer_id");
        Long carId = rs.getLong("car_id");

        User customer = userRepo.findById(customerId).orElse(null);
        Car car = carRepo.findById(carId).orElse(null);

        if (customer == null || car == null) {
            throw new SQLException("Invalid booking: customer or car not found");
        }

        Booking booking = new Booking(
            rs.getLong("id"),
            customer,
            car,
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getString("notes"),
            rs.getString("pickup_location"),
            rs.getString("dropoff_location")
        );

        // Set status from database
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));

        return booking;
    }
}
