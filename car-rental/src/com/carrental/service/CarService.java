package com.carrental.service;

import com.carrental.enums.CarStatus;
import com.carrental.enums.FuelType;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.exception.ValidationException;
import com.carrental.models.Car;
import com.carrental.repository.CarRepository;
import com.carrental.util.IdGenerator;
import com.carrental.util.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Core car management logic — add, update, search, status changes.
 */
public class CarService {

    private static CarService instance;
    private final CarRepository carRepo = CarRepository.getInstance();

    private CarService() {}

    public static CarService getInstance() {
        if (instance == null) instance = new CarService();
        return instance;
    }

    public Car addCar(String brand, String model, int year, String licensePlate,
                      String color, int seatCapacity, FuelType fuelType,
                      String transmission, BigDecimal dailyRate,
                      String category, String description, String imageUrl) {

        Validator.requireNonBlank(brand, "Brand");
        Validator.requireNonBlank(model, "Model");
        Validator.validateYear(year);
        Validator.requireNonBlank(licensePlate, "License plate");
        Validator.validatePositive(dailyRate.doubleValue(), "Daily rate");
        Validator.requireNonBlank(category, "Category");

        if (carRepo.existsByLicensePlate(licensePlate))
            throw new ValidationException("Car with license plate already exists: " + licensePlate);

        Car car = new Car(IdGenerator.nextCarId(), brand, model, year,
                licensePlate.toUpperCase(), color, seatCapacity,
                fuelType, transmission, dailyRate, category, description, imageUrl);

        carRepo.save(car);
        System.out.println("✅ Car added: " + car.getDisplayName() + " [" + car.getLicensePlate() + "]");
        return car;
    }

    public Car getById(Long id) {
        return carRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", id));
    }

    public List<Car> getAllCars() {
        return carRepo.findAll();
    }

    public List<Car> getAvailableCars() {
        return carRepo.findByStatus(CarStatus.AVAILABLE);
    }

    public List<Car> searchAvailable(LocalDate startDate, LocalDate endDate, String category) {
        Validator.validateDates(startDate, endDate);
        if (category != null && !category.isBlank()) {
            return carRepo.findAvailableForDateRangeAndCategory(startDate, endDate, category);
        }
        return carRepo.findAvailableForDateRange(startDate, endDate);
    }

    public List<Car> getByCategory(String category) {
        return carRepo.findByCategory(category);
    }

    public Car updateCar(Long id, String brand, String model, int year,
                         String color, int seatCapacity, FuelType fuelType,
                         String transmission, BigDecimal dailyRate,
                         String category, String description) {
        Car car = getById(id);
        if (brand       != null) car.setBrand(brand);
        if (model       != null) car.setModel(model);
        if (year        > 0)     car.setYear(year);
        if (color       != null) car.setColor(color);
        if (seatCapacity > 0)    car.setSeatCapacity(seatCapacity);
        if (fuelType    != null) car.setFuelType(fuelType);
        if (transmission!= null) car.setTransmission(transmission);
        if (dailyRate   != null) car.setDailyRate(dailyRate);
        if (category    != null) car.setCategory(category);
        if (description != null) car.setDescription(description);
        carRepo.save(car);
        System.out.println("✅ Car updated: " + car.getDisplayName());
        return car;
    }

    public Car updateStatus(Long id, CarStatus status) {
        Car car = getById(id);
        car.setStatus(status);
        carRepo.save(car);
        System.out.println("✅ Car [" + car.getLicensePlate() + "] status → " + status);
        return car;
    }

    public void deleteCar(Long id) {
        Car car = getById(id);
        if (car.getStatus() == CarStatus.RENTED)
            throw new ValidationException("Cannot delete a currently rented car");
        carRepo.delete(id);
        System.out.println("✅ Car deleted: " + car.getDisplayName());
    }
}
