package com.carrental.service;

import com.carrental.enums.BookingStatus;
import com.carrental.enums.CarStatus;
import com.carrental.exception.BookingConflictException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.exception.ValidationException;
import com.carrental.models.Booking;
import com.carrental.models.Car;
import com.carrental.models.User;
import com.carrental.repository.BookingRepository;
import com.carrental.util.IdGenerator;
import com.carrental.util.Validator;

import java.time.LocalDate;
import java.util.List;

/**
 * Booking lifecycle: create (→ ACTIVE) → done (→ DONE) → cancel (→ CANCELLED).
 * No pending, no confirm, no payment steps.
 */
public class BookingService {

    private static BookingService instance;
    private final BookingRepository bookingRepo = BookingRepository.getInstance();
    private final CarService carService = CarService.getInstance();

    private BookingService() {}

    public static BookingService getInstance() {
        if (instance == null) instance = new BookingService();
        return instance;
    }

    /** Create a booking — car is marked RENTED immediately */
    public Booking createBooking(User customer, Long carId,
                                 LocalDate startDate, LocalDate endDate,
                                 String notes, String pickupLocation, String dropoffLocation) {
        Validator.validateDates(startDate, endDate);

        Car car = carService.getById(carId);

        if (car.getStatus() != CarStatus.AVAILABLE)
            throw new ValidationException("Car is not available (status: " + car.getStatus() + ")");

        List<Booking> conflicts = bookingRepo.findConflicting(carId, startDate, endDate);
        if (!conflicts.isEmpty())
            throw new BookingConflictException(
                    "Car already booked from " + startDate + " to " + endDate);

        Booking booking = new Booking(
                IdGenerator.nextBookingId(),
                customer, car, startDate, endDate,
                notes, pickupLocation, dropoffLocation);

        // Car is taken as soon as booking is created
        car.setStatus(CarStatus.RENTED);

        bookingRepo.save(booking);
        System.out.printf("Booking created: %s | Car: %s | Rs.%.2f%n",
                booking.getBookingReference(), car.getDisplayName(), booking.getTotalAmount());
        return booking;
    }

    /** Admin marks booking as done — car becomes available again */
    public Booking markDone(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking.getStatus() != BookingStatus.ACTIVE)
            throw new ValidationException("Only ACTIVE bookings can be marked done");
        booking.setStatus(BookingStatus.DONE);
        booking.getCar().setStatus(CarStatus.AVAILABLE);
        bookingRepo.save(booking);
        System.out.println("Booking done — car returned: " + booking.getBookingReference());
        return booking;
    }

    /** Customer or admin cancels a booking */
    public Booking cancelBooking(Long bookingId, User requestingUser) {
        Booking booking = getById(bookingId);

        if (!requestingUser.isAdmin() &&
                !booking.getCustomer().getId().equals(requestingUser.getId()))
            throw new ValidationException("You can only cancel your own bookings");

        if (booking.getStatus() == BookingStatus.DONE)
            throw new ValidationException("Cannot cancel a booking that is already done");

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new ValidationException("Booking is already cancelled");

        if (booking.getStatus() == BookingStatus.ACTIVE)
            booking.getCar().setStatus(CarStatus.AVAILABLE);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
        System.out.println("Booking cancelled: " + booking.getBookingReference());
        return booking;
    }

    public Booking getById(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    public List<Booking> getMyBookings(User customer) {
        return bookingRepo.findByCustomerId(customer.getId());
    }
}
