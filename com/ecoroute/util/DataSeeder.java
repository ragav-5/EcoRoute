package com.ecoroute.util;

import com.ecoroute.model.Location;
import com.ecoroute.model.LocationType;
import com.ecoroute.model.Vehicle;
import com.ecoroute.model.VehicleType;
import com.ecoroute.repository.LocationRepository;
import com.ecoroute.repository.VehicleRepository;

/**
 * Seeds the repositories with a demo campus graph and starter vehicle fleet
 * so the simulator has something to route and schedule against immediately.
 */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static void seedCampusGraph(LocationRepository repo) {
        repo.addLocation(new Location("L1", "Main Gate", LocationType.PARKING));
        repo.addLocation(new Location("L2", "Library", LocationType.ACADEMIC));
        repo.addLocation(new Location("L3", "CSE Block", LocationType.ACADEMIC));
        repo.addLocation(new Location("L4", "Mech Block", LocationType.ACADEMIC));
        repo.addLocation(new Location("L5", "Admin Block", LocationType.ACADEMIC));
        repo.addLocation(new Location("L6", "Hostel A", LocationType.HOSTEL));
        repo.addLocation(new Location("L7", "Hostel B", LocationType.HOSTEL));
        repo.addLocation(new Location("L8", "Food Court", LocationType.RECREATIONAL));
        repo.addLocation(new Location("L9", "Sports Complex", LocationType.RECREATIONAL));
        repo.addLocation(new Location("L10", "Parking Zone B", LocationType.PARKING));

        repo.addBidirectionalEdge("L1", "L2", 0.5, 0.10);
        repo.addBidirectionalEdge("L1", "L6", 0.8, 0.20);
        repo.addBidirectionalEdge("L2", "L3", 0.4, 0.05);
        repo.addBidirectionalEdge("L2", "L5", 0.6, 0.15);
        repo.addBidirectionalEdge("L3", "L4", 0.3, 0.10);
        repo.addBidirectionalEdge("L3", "L7", 0.7, 0.25);
        repo.addBidirectionalEdge("L4", "L5", 0.5, 0.20);
        repo.addBidirectionalEdge("L4", "L9", 0.9, 0.30);
        repo.addBidirectionalEdge("L5", "L8", 0.4, 0.05);
        repo.addBidirectionalEdge("L6", "L7", 0.6, 0.10);
        repo.addBidirectionalEdge("L6", "L8", 0.5, 0.15);
        repo.addBidirectionalEdge("L7", "L9", 0.8, 0.20);
        repo.addBidirectionalEdge("L8", "L9", 0.6, 0.10);
        repo.addBidirectionalEdge("L8", "L10", 0.7, 0.25);
        repo.addBidirectionalEdge("L9", "L10", 0.9, 0.30);
        repo.addBidirectionalEdge("L10", "L1", 1.0, 0.20);
    }

    public static void seedFleet(VehicleRepository repo) {
        repo.addVehicle(new Vehicle("V1", VehicleType.E_BIKE, 90.0, "L1"));
        repo.addVehicle(new Vehicle("V2", VehicleType.E_SCOOTER, 75.0, "L1"));
        repo.addVehicle(new Vehicle("V3", VehicleType.E_BIKE, 60.0, "L6"));
        repo.addVehicle(new Vehicle("V4", VehicleType.E_SCOOTER, 85.0, "L7"));
        repo.addVehicle(new Vehicle("V5", VehicleType.E_BIKE, 95.0, "L8"));
        repo.addVehicle(new Vehicle("V6", VehicleType.E_SCOOTER, 40.0, "L9"));
        repo.addVehicle(new Vehicle("V7", VehicleType.E_BIKE, 70.0, "L10"));
        repo.addVehicle(new Vehicle("V8", VehicleType.E_SCOOTER, 55.0, "L2"));
    }
}
