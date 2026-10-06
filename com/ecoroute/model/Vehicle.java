package com.ecoroute.model;

/**
 * Represents an e-bike or e-scooter tracked by the EcoRoute coordinator.
 */
public class Vehicle {

    private final String id;
    private final VehicleType type;
    private double batteryPercentage; // 0.0 - 100.0
    private String currentLocationId;
    private VehicleStatus status;

    public Vehicle(String id, VehicleType type, double batteryPercentage, String currentLocationId) {
        this.id = id;
        this.type = type;
        this.batteryPercentage = batteryPercentage;
        this.currentLocationId = currentLocationId;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public VehicleType getType() {
        return type;
    }

    public double getBatteryPercentage() {
        return batteryPercentage;
    }

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(String currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    /** Drains battery by the given amount, clamped at 0. */
    public void drainBattery(double amount) {
        this.batteryPercentage = Math.max(0.0, this.batteryPercentage - amount);
        if (this.batteryPercentage <= 5.0) {
            this.status = VehicleStatus.MAINTENANCE;
        }
    }

    public void rechargeFull() {
        this.batteryPercentage = 100.0;
        if (this.status != VehicleStatus.IN_USE) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    public boolean isOperational() {
        return status == VehicleStatus.AVAILABLE && batteryPercentage > 5.0;
    }

    @Override
    public String toString() {
        return String.format("%-4s | %-9s | Battery: %5.1f%% | At: %-4s | Status: %s",
                id, type.getLabel(), batteryPercentage, currentLocationId, status);
    }
}
