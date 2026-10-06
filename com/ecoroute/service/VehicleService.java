package com.ecoroute.service;

import com.ecoroute.model.RouteResult;
import com.ecoroute.model.Vehicle;
import com.ecoroute.model.VehicleStatus;
import com.ecoroute.model.VehicleType;
import com.ecoroute.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

/**
 * Handles vehicle lookup, dispatch, battery drain and re-docking after a
 * trip completes. Prefers the student's requested vehicle type but falls
 * back to any available vehicle at the origin dock.
 */
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Optional<Vehicle> findVehicleForPickup(String locationId, VehicleType preferredType) {
        List<Vehicle> preferred = vehicleRepository.getAvailableAt(locationId, preferredType);
        if (!preferred.isEmpty()) {
            return Optional.of(preferred.get(0));
        }
        List<Vehicle> fallback = vehicleRepository.getAnyAvailableAt(locationId);
        if (!fallback.isEmpty()) {
            return Optional.of(fallback.get(0));
        }
        return Optional.empty();
    }

    /**
     * Dispatches the vehicle along the given route: marks it in-use,
     * drains battery proportional to distance/terrain, then docks it
     * at the destination and frees it back to AVAILABLE.
     */
    public void dispatchAlongRoute(Vehicle vehicle, RouteResult route) {
        vehicle.setStatus(VehicleStatus.IN_USE);
        double batteryDrain = route.getTotalDistanceKm() * vehicle.getType().getBatteryUnitsPerKm()
                * (1.0 + (route.getTotalEnergyCost() > 0 && route.getTotalDistanceKm() > 0
                    ? (route.getTotalEnergyCost() / route.getTotalDistanceKm()) - 1.0
                    : 0.0) * 0.3);
        vehicle.drainBattery(Math.max(0.0, batteryDrain));

        if (!route.getPath().isEmpty()) {
            String destination = route.getPath().get(route.getPath().size() - 1);
            vehicle.setCurrentLocationId(destination);
        }
        if (vehicle.getBatteryPercentage() > 5.0) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
    }

    public void rechargeAll() {
        for (Vehicle v : vehicleRepository.getAllVehicles()) {
            v.rechargeFull();
        }
    }

    public List<Vehicle> getFleet() {
        return vehicleRepository.getAllVehicles();
    }
}
