package com.ecoroute.model;

/**
 * Type of micro-mobility device. Each type has its own energy consumption
 * multiplier used when computing the Dijkstra-driven energy-optimal route.
 */
public enum VehicleType {
    E_BIKE("E-Bike", 1.0, 6.0),
    E_SCOOTER("E-Scooter", 1.4, 4.5);

    private final String label;
    private final double energyConsumptionFactor;
    private final double batteryUnitsPerKm;

    VehicleType(String label, double energyConsumptionFactor, double batteryUnitsPerKm) {
        this.label = label;
        this.energyConsumptionFactor = energyConsumptionFactor;
        this.batteryUnitsPerKm = batteryUnitsPerKm;
    }

    public String getLabel() {
        return label;
    }

    /** Multiplier applied on top of edge base energy cost. */
    public double getEnergyConsumptionFactor() {
        return energyConsumptionFactor;
    }

    /** Approx % battery drained per km travelled (before terrain factor). */
    public double getBatteryUnitsPerKm() {
        return batteryUnitsPerKm;
    }
}
