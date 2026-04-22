package com.carrental.models;

import com.carrental.enums.FuelType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract Vehicle class demonstrating OOP Abstraction.
 * Can be extended for Car, Bike, Truck etc. in future.
 */
public abstract class Vehicle {

    private final Long id;
    private String licensePlate;
    private int year;
    private String color;
    private FuelType fuelType;
    private BigDecimal dailyRate;
    private String description;
    private final LocalDateTime createdAt;

    protected Vehicle(Long id, String licensePlate, int year,
                      String color, FuelType fuelType, BigDecimal dailyRate, String description) {
        this.id           = id;
        this.licensePlate = licensePlate;
        this.year         = year;
        this.color        = color;
        this.fuelType     = fuelType;
        this.dailyRate    = dailyRate;
        this.description  = description;
        this.createdAt    = LocalDateTime.now();
    }

    /** Each subclass specifies its vehicle type label */
    public abstract String getVehicleType();

    /** Each subclass defines how it describes itself for display */
    public abstract String getDisplayName();

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long getId()              { return id; }
    public String getLicensePlate()  { return licensePlate; }
    public int getYear()             { return year; }
    public String getColor()         { return color; }
    public FuelType getFuelType()    { return fuelType; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public String getDescription()   { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setYear(int year)                    { this.year = year; }
    public void setColor(String color)               { this.color = color; }
    public void setFuelType(FuelType fuelType)       { this.fuelType = fuelType; }
    public void setDailyRate(BigDecimal dailyRate)   { this.dailyRate = dailyRate; }
    public void setDescription(String description)   { this.description = description; }

    @Override
    public String toString() {
        return String.format("[%s] id=%d | %s | %s | %d | %s | ₹%.2f/day",
                getVehicleType(), id, getDisplayName(), licensePlate, year,
                fuelType, dailyRate);
    }
}
