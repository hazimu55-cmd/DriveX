package com.carrental.service;

import com.carrental.enums.Role;
import com.carrental.exception.AuthException;
import com.carrental.exception.ValidationException;
import com.carrental.models.User;
import com.carrental.repository.UserRepository;
import com.carrental.util.IdGenerator;
import com.carrental.util.PasswordUtil;
import com.carrental.util.Validator;

/**
 * Handles registration and login logic.
 */
public class AuthService {

    private static AuthService instance;
    private final UserRepository userRepo = UserRepository.getInstance();

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public User register(String firstName, String lastName, String email,
                         String password, String phone, String licenseNumber) {
        // Validate inputs
        Validator.requireNonBlank(firstName, "First name");
        Validator.requireNonBlank(lastName, "Last name");
        Validator.validateEmail(email);
        Validator.requireNonBlank(password, "Password");
        if (password.length() < 6)
            throw new ValidationException("Password must be at least 6 characters");
        Validator.validatePhone(phone);
        Validator.requireNonBlank(licenseNumber, "License number");

        // Uniqueness checks
        if (userRepo.existsByEmail(email))
            throw new ValidationException("Email already registered: " + email);
        if (userRepo.existsByPhone(phone))
            throw new ValidationException("Phone already registered: " + phone);
        if (userRepo.existsByLicenseNumber(licenseNumber))
            throw new ValidationException("License number already registered: " + licenseNumber);

        User user = new User(
                IdGenerator.nextUserId(),
                firstName, lastName, email, phone,
                licenseNumber,
                PasswordUtil.hashPassword(password),
                Role.CUSTOMER
        );
        userRepo.save(user);
        System.out.println("✅ Registered: " + user.getFullName() + " (" + user.getEmail() + ")");
        return user;
    }

    public User registerAdmin(String firstName, String lastName, String email,
                              String password, String phone, String licenseNumber) {
        Validator.validateEmail(email);
        if (userRepo.existsByEmail(email))
            throw new ValidationException("Email already registered: " + email);

        User admin = new User(
                IdGenerator.nextUserId(),
                firstName, lastName, email, phone,
                licenseNumber,
                PasswordUtil.hashPassword(password),
                Role.ADMIN
        );
        userRepo.save(admin);
        System.out.println("✅ Admin registered: " + admin.getFullName());
        return admin;
    }

    public User login(String email, String password) {
        Validator.validateEmail(email);
        Validator.requireNonBlank(password, "Password");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AuthException("No account found with email: " + email));

        if (!user.isEnabled())
            throw new AuthException("Account is disabled. Contact admin.");

        if (!PasswordUtil.verifyPassword(password, user.getHashedPassword()))
            throw new AuthException("Incorrect password");

        System.out.println("✅ Login successful: " + user.getFullName() + " [" + user.getRole() + "]");
        return user;
    }
}
