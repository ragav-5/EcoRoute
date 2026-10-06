package com.ecoroute.repository;

import com.ecoroute.model.Vehicle;
import com.ecoroute.model.VehicleStatus;
import com.ecoroute.model.VehicleType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory repository holding the micro-mobility vehicle fleet.
 */
public class VehicleRepository {

    private final Map<String, Vehicle> vehicles = new LinkedHashMap<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }

    public Vehicle getVehicle(String id) {
        return vehicles.get(id);
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    /** Returns available vehicles docked at a given location, optionally filtered by type. */
    public List<Vehicle> getAvailableAt(String locationId, VehicleType preferredType) {
        List<Vehicle> exactMatches = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (v.getCurrentLocationId().equals(locationId)
                    && v.getStatus() == VehicleStatus.AVAILABLE
                    && v.getType() == preferredType) {
                exactMatches.add(v);
            }
        }
        return exactMatches;
    }

    /** Returns any available vehicle docked at a location, regardless of type. */
    public List<Vehicle> getAnyAvailableAt(String locationId) {
        List<Vehicle> matches = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (v.getCurrentLocationId().equals(locationId) && v.getStatus() == VehicleStatus.AVAILABLE) {
                matches.add(v);
            }
        }
        return matches;
    }

    public int fleetSize() {
        return vehicles.size();
    }
}
