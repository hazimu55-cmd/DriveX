package com.carrental.models;

import java.time.LocalDateTime;

/**
 * Abstract base class demonstrating OOP Abstraction & Inheritance.
 * All person-type entities extend this class.
 */
public abstract class Person {

    private final Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private final LocalDateTime createdAt;

    protected Person(Long id, String firstName, String lastName, String email, String phone) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.phone     = phone;
        this.createdAt = LocalDateTime.now();
    }

    // ── Abstract method: each subclass defines its role label ─────────────────
    public abstract String getRole();

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()              { return id; }
    public String getFirstName()     { return firstName; }
    public String getLastName()      { return lastName; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getFullName()      { return firstName + " " + lastName; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName  = lastName; }
    public void setEmail(String email)         { this.email     = email; }
    public void setPhone(String phone)         { this.phone     = phone; }

    @Override
    public String toString() {
        return String.format("[%s] id=%d | %s | %s | %s",
                getRole(), id, getFullName(), email, phone);
    }
}
