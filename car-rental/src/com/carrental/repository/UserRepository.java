package com.carrental.repository;

import com.carrental.models.User;
import com.carrental.enums.Role;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory User store. Simulates a database using HashMap.
 * Singleton pattern — one instance shared across the app.
 */
public class UserRepository {

    private static UserRepository instance;
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<User> findByPhone(String phone) {
        return store.values().stream()
                .filter(u -> u.getPhone().equals(phone))
                .findFirst();
    }

    public Optional<User> findByLicenseNumber(String licenseNumber) {
        return store.values().stream()
                .filter(u -> u.getLicenseNumber().equalsIgnoreCase(licenseNumber))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByPhone(String phone) {
        return store.values().stream().anyMatch(u -> u.getPhone().equals(phone));
    }

    public boolean existsByLicenseNumber(String license) {
        return store.values().stream().anyMatch(u -> u.getLicenseNumber().equalsIgnoreCase(license));
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<User> findByRole(Role role) {
        return store.values().stream()
                .filter(u -> u.getRoleEnum() == role)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public int count() {
        return store.size();
    }
}
