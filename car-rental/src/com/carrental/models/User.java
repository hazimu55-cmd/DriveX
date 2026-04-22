package com.carrental.models;

import com.carrental.enums.Role;

/**
 * Represents a registered user (Customer or Admin).
 * Demonstrates Inheritance from Person.
 */
public class User extends Person {

    private final String licenseNumber;
    private String hashedPassword;
    private final Role role;
    private boolean enabled;

    public User(Long id, String firstName, String lastName,
                String email, String phone,
                String licenseNumber, String hashedPassword, Role role) {
        super(id, firstName, lastName, email, phone);
        this.licenseNumber  = licenseNumber;
        this.hashedPassword = hashedPassword;
        this.role           = role;
        this.enabled        = true;
    }

    @Override
    public String getRole() { return role.name(); }

    public String getLicenseNumber()  { return licenseNumber; }
    public String getHashedPassword() { return hashedPassword; }
    public Role   getRoleEnum()       { return role; }
    public boolean isEnabled()        { return enabled; }

    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public void setEnabled(boolean enabled)              { this.enabled = enabled; }

    public boolean isAdmin()    { return role == Role.ADMIN; }
    public boolean isCustomer() { return role == Role.CUSTOMER; }

    @Override
    public String toString() {
        return super.toString() + " | License: " + licenseNumber +
               " | Active: " + enabled;
    }
}
