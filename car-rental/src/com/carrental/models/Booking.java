package com.carrental.models;

import com.carrental.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Booking model — links a User + Car + date range.
 * Created as ACTIVE immediately (no pending/confirm steps).
 */
public class Booking {

    private final Long id;
    private final String bookingReference;
    private final User customer;
    private final Car car;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalAmount;
    private BookingStatus status;
    private String notes;
    private String pickupLocation;
    private String dropoffLocation;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Booking(Long id, User customer, Car car,
                   LocalDate startDate, LocalDate endDate,
                   String notes, String pickupLocation, String dropoffLocation) {
        this.id               = id;
        this.bookingReference = "BK-" + System.currentTimeMillis() + "-" + id;
        this.customer         = customer;
        this.car              = car;
        this.startDate        = startDate;
        this.endDate          = endDate;
        this.status           = BookingStatus.ACTIVE;   // active straight away
        this.notes            = notes;
        this.pickupLocation   = pickupLocation;
        this.dropoffLocation  = dropoffLocation;
        this.createdAt        = LocalDateTime.now();
        this.updatedAt        = LocalDateTime.now();

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        this.totalAmount = car.getDailyRate().multiply(BigDecimal.valueOf(days));
    }

    public long getNumberOfDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public void setStatus(BookingStatus status) {
        this.status    = status;
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()                 { return id; }
    public String getBookingReference() { return bookingReference; }
    public User getCustomer()           { return customer; }
    public Car getCar()                 { return car; }
    public LocalDate getStartDate()     { return startDate; }
    public LocalDate getEndDate()       { return endDate; }
    public BigDecimal getTotalAmount()  { return totalAmount; }
    public BookingStatus getStatus()    { return status; }
    public String getNotes()            { return notes; }
    public String getPickupLocation()   { return pickupLocation; }
    public String getDropoffLocation()  { return dropoffLocation; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setNotes(String notes)                 { this.notes = notes; }
    public void setPickupLocation(String p)            { this.pickupLocation = p; }
    public void setDropoffLocation(String d)           { this.dropoffLocation = d; }

    @Override
    public String toString() {
        return String.format("Booking[%s] | %s | %s | %s→%s | Rs.%.2f | %s",
                bookingReference, customer.getFullName(), car.getDisplayName(),
                startDate, endDate, totalAmount, status);
    }
}
