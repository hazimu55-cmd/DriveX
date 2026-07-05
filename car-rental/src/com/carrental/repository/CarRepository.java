package com.carrental.repository;

import com.carrental.models.Car;
import com.carrental.enums.CarStatus;
import com.carrental.enums.FuelType;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JDBC-based Car repository. Persists data to MySQL database.
 * Singleton pattern — one instance shared across the app.
 */
public class CarRepository {

    private static CarRepository instance;

    private CarRepository() {}

    public static CarRepository getInstance() {
        if (instance == null) instance = new CarRepository();
        return instance;
    }

    public Car save(Car car) {
        String sql = "INSERT INTO cars (id, brand, model, year, license_plate, color, seat_capacity, " +
                     "fuel_type, transmission, daily_rate, category, description, image_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "brand = VALUES(brand), model = VALUES(model), year = VALUES(year), " +
                     "license_plate = VALUES(license_plate), color = VALUES(color), " +
                     "seat_capacity = VALUES(seat_capacity), fuel_type = VALUES(fuel_type), " +
                     "transmission = VALUES(transmission), daily_rate = VALUES(daily_rate), " +
                     "category = VALUES(category), description = VALUES(description), " +
                     "image_url = VALUES(image_url), status = VALUES(status)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, car.getId());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getLicensePlate());
            stmt.setString(6, car.getColor());
            stmt.setInt(7, car.getSeatCapacity());
            stmt.setString(8, car.getFuelType().name());
            stmt.setString(9, car.getTransmission());
            stmt.setBigDecimal(10, car.getDailyRate());
            stmt.setString(11, car.getCategory());
            stmt.setString(12, car.getDescription());
            stmt.setString(13, car.getImageUrl());
            stmt.setString(14, car.getStatus().name());

            stmt.executeUpdate();
            System.out.println("✅ Car saved to database: " + car.getLicensePlate());
            return car;

        } catch (SQLException e) {
            System.err.println("Error saving car: " + e.getMessage());
            throw new RuntimeException("Failed to save car", e);
        }
    }

    public Optional<Car> findById(Long id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCar(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error finding car by id: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Car> findByLicensePlate(String plate) {
        String sql = "SELECT * FROM cars WHERE license_plate = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCar(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error finding car by license plate: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existsByLicensePlate(String plate) {
        String sql = "SELECT COUNT(*) FROM cars WHERE license_plate = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plate);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error checking license plate existence: " + e.getMessage());
            return false;
        }
    }

    public List<Car> findAll() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(mapRowToCar(rs));
            }
            return cars;

        } catch (SQLException e) {
            System.err.println("Error finding all cars: " + e.getMessage());
            return cars;
        }
    }

    public List<Car> findByStatus(CarStatus status) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cars.add(mapRowToCar(rs));
            }
            return cars;

        } catch (SQLException e) {
            System.err.println("Error finding cars by status: " + e.getMessage());
            return cars;
        }
    }

    public List<Car> findByCategory(String category) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cars.add(mapRowToCar(rs));
            }
            return cars;

        } catch (SQLException e) {
            System.err.println("Error finding cars by category: " + e.getMessage());
            return cars;
        }
    }

    /**
     * Returns AVAILABLE cars not booked during the requested date range.
     * Uses BookingRepository to check conflicts — dependency resolved at runtime
     * to avoid circular dependency at construction time.
     */
    public List<Car> findAvailableForDateRange(LocalDate startDate, LocalDate endDate) {
        BookingRepository bookingRepo = BookingRepository.getInstance();
        Set<Long> bookedCarIds = bookingRepo.findBookedCarIdsBetween(startDate, endDate);

        List<Car> allAvailableCars = findByStatus(CarStatus.AVAILABLE);
        return allAvailableCars.stream()
                .filter(c -> !bookedCarIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    public List<Car> findAvailableForDateRangeAndCategory(LocalDate startDate, LocalDate endDate, String category) {
        return findAvailableForDateRange(startDate, endDate).stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Car deleted from database: " + id);

        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            throw new RuntimeException("Failed to delete car", e);
        }
    }

    public long countByStatus(CarStatus status) {
        String sql = "SELECT COUNT(*) FROM cars WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            System.err.println("Error counting cars by status: " + e.getMessage());
            return 0;
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM cars";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Error counting cars: " + e.getMessage());
            return 0;
        }
    }

    private Car mapRowToCar(ResultSet rs) throws SQLException {
        return new Car(
            rs.getLong("id"),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getString("license_plate"),
            rs.getString("color"),
            rs.getInt("seat_capacity"),
            FuelType.valueOf(rs.getString("fuel_type")),
            rs.getString("transmission"),
            rs.getBigDecimal("daily_rate"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("image_url")
        );
    }
}
