package com.carrental.util;

import com.carrental.exception.ValidationException;
import java.time.LocalDate;

/**
 * Central validation utility.
 * Keeps validation logic separate from business logic — SRP principle.
 */
public final class Validator {

    private Validator() {}

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public static void validateEmail(String email) {
        requireNonBlank(email, "Email");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validatePhone(String phone) {
        requireNonBlank(phone, "Phone");
        if (!phone.matches("^[+]?[0-9]{10,15}$")) {
            throw new ValidationException("Invalid phone number: " + phone);
        }
    }

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        requireNonNull(startDate, "Start date");
        requireNonNull(endDate, "End date");
        if (!startDate.isBefore(endDate)) {
            throw new ValidationException("End date must be after start date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }
    }

    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    public static void validateYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 1990 || year > currentYear + 1) {
            throw new ValidationException("Car year must be between 1990 and " + (currentYear + 1));
        }
    }
}
