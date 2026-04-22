package com.carrental.repository;

import com.carrental.models.Car;
import com.carrental.enums.CarStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory Car store. Singleton pattern.
 */
public class CarRepository {

    private static CarRepository instance;
    private final Map<Long, Car> store = new ConcurrentHashMap<>();

    private CarRepository() {}

    public static CarRepository getInstance() {
        if (instance == null) instance = new CarRepository();
        return instance;
    }

    public Car save(Car car) {
        store.put(car.getId(), car);
        return car;
    }

    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Car> findByLicensePlate(String plate) {
        return store.values().stream()
                .filter(c -> c.getLicensePlate().equalsIgnoreCase(plate))
                .findFirst();
    }

    public boolean existsByLicensePlate(String plate) {
        return store.values().stream()
                .anyMatch(c -> c.getLicensePlate().equalsIgnoreCase(plate));
    }

    public List<Car> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Car> findByStatus(CarStatus status) {
        return store.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Car> findByCategory(String category) {
        return store.values().stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Returns AVAILABLE cars not booked during the requested date range.
     * Uses BookingRepository to check conflicts — dependency resolved at runtime
     * to avoid circular dependency at construction time.
     */
    public List<Car> findAvailableForDateRange(LocalDate startDate, LocalDate endDate) {
        BookingRepository bookingRepo = BookingRepository.getInstance();
        Set<Long> bookedCarIds = bookingRepo.findBookedCarIdsBetween(startDate, endDate);

        return store.values().stream()
                .filter(c -> c.getStatus() == CarStatus.AVAILABLE)
                .filter(c -> !bookedCarIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    public List<Car> findAvailableForDateRangeAndCategory(LocalDate startDate, LocalDate endDate, String category) {
        return findAvailableForDateRange(startDate, endDate).stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public long countByStatus(CarStatus status) {
        return store.values().stream().filter(c -> c.getStatus() == status).count();
    }

    public int count() {
        return store.size();
    }
}
