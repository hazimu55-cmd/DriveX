package com.carrental.repository;

import com.carrental.models.Booking;
import com.carrental.enums.BookingStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BookingRepository {

    private static BookingRepository instance;
    private final Map<Long, Booking> store = new ConcurrentHashMap<>();

    private BookingRepository() {}

    public static BookingRepository getInstance() {
        if (instance == null) instance = new BookingRepository();
        return instance;
    }

    public Booking save(Booking booking) {
        store.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Booking> findByReference(String reference) {
        return store.values().stream()
                .filter(b -> b.getBookingReference().equals(reference))
                .findFirst();
    }

    public List<Booking> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Booking> findByCustomerId(Long customerId) {
        return store.values().stream()
                .filter(b -> b.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Booking> findByStatus(BookingStatus status) {
        return store.values().stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    /** Conflict check — only ACTIVE bookings can block a car */
    public List<Booking> findConflicting(Long carId, LocalDate startDate, LocalDate endDate) {
        return store.values().stream()
                .filter(b -> b.getCar().getId().equals(carId))
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> b.getStartDate().isBefore(endDate) && b.getEndDate().isAfter(startDate))
                .collect(Collectors.toList());
    }

    /** Car IDs blocked by ACTIVE bookings during a date range */
    public Set<Long> findBookedCarIdsBetween(LocalDate startDate, LocalDate endDate) {
        return store.values().stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> b.getStartDate().isBefore(endDate) && b.getEndDate().isAfter(startDate))
                .map(b -> b.getCar().getId())
                .collect(Collectors.toSet());
    }

    public int count() { return store.size(); }
}
