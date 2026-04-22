package com.carrental.models;

import com.carrental.enums.CarStatus;
import com.carrental.enums.FuelType;
import java.math.BigDecimal;

/**
 * Concrete Car class demonstrating Inheritance from Vehicle.
 * Encapsulates all car-specific state and behaviour.
 */
public class Car extends Vehicle {

    private String brand;
    private String model;
    private int seatCapacity;
    private String transmission;   // MANUAL / AUTOMATIC
    private String category;       // ECONOMY, SUV, LUXURY, VAN
    private CarStatus status;
    private String imageUrl;

    public Car(Long id, String brand, String model, int year,
               String licensePlate, String color, int seatCapacity,
               FuelType fuelType, String transmission, BigDecimal dailyRate,
               String category, String description, String imageUrl) {
        super(id, licensePlate, year, color, fuelType, dailyRate, description);
        this.brand        = brand;
        this.model        = model;
        this.seatCapacity = seatCapacity;
        this.transmission = transmission.toUpperCase();
        this.category     = category.toUpperCase();
        this.status       = CarStatus.AVAILABLE;
        this.imageUrl     = imageUrl;
    }

    // ── Overrides ─────────────────────────────────────────────────────────────
    @Override
    public String getVehicleType() { return "CAR"; }

    @Override
    public String getDisplayName() { return brand + " " + model; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getBrand()        { return brand; }
    public String getModel()        { return model; }
    public int getSeatCapacity()    { return seatCapacity; }
    public String getTransmission() { return transmission; }
    public String getCategory()     { return category; }
    public CarStatus getStatus()    { return status; }
    public String getImageUrl()     { return imageUrl; }
    public boolean isAvailable()    { return status == CarStatus.AVAILABLE; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setBrand(String brand)             { this.brand = brand; }
    public void setModel(String model)             { this.model = model; }
    public void setSeatCapacity(int seatCapacity)  { this.seatCapacity = seatCapacity; }
    public void setTransmission(String t)          { this.transmission = t.toUpperCase(); }
    public void setCategory(String category)       { this.category = category.toUpperCase(); }
    public void setStatus(CarStatus status)        { this.status = status; }
    public void setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %s | %s | Seats: %d | Status: %s",
                category, transmission, seatCapacity, status);
    }
}
