package com.aipaca.rumbo.platform.vehicles.domain.model;

import jakarta.persistence.*;
import java.util.Locale;

@Entity
@Table(name = "vc_vehicles", uniqueConstraints = @UniqueConstraint(
        name = "uk_vc_vehicles_plate", columnNames = "license_plate"))
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String brand;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 40)
    private String status = "PENDING_VERIFICATION";

    protected Vehicle() {}

    public Vehicle(String brand, String model, String licensePlate, Integer capacity, Integer year) {
        update(brand, model, licensePlate, capacity, year);
    }

    public void update(String brand, String model, String licensePlate, Integer capacity, Integer year) {
        this.brand = requireText(brand, "Brand", 80);
        this.model = requireText(model, "Model", 80);
        this.licensePlate = normalizePlate(licensePlate);
        if (capacity == null || capacity < 1 || capacity > 80)
            throw new IllegalArgumentException("Capacity must be between 1 and 80");
        if (year == null || year < 1980 || year > 2100)
            throw new IllegalArgumentException("Year must be between 1980 and 2100");
        this.capacity = capacity;
        this.year = year;
        // Registration does not prove ownership or certify the vehicle.
        if (this.status == null) this.status = "PENDING_VERIFICATION";
    }

    public static String normalizePlate(String value) {
        return requireText(value, "License plate", 20)
                .replace(" ", "").toUpperCase(Locale.ROOT);
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.trim().length() > maxLength)
            throw new IllegalArgumentException(field + " is required and cannot exceed " + maxLength + " characters");
        return value.trim();
    }

    public Long getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getLicensePlate() { return licensePlate; }
    public Integer getCapacity() { return capacity; }
    public Integer getYear() { return year; }
    public String getStatus() { return status; }
}
