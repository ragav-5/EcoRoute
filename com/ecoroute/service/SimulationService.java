package com.ecoroute.service;

import com.ecoroute.model.Location;
import com.ecoroute.model.Student;
import com.ecoroute.model.Vehicle;
import com.ecoroute.model.VehicleType;
import com.ecoroute.repository.LocationRepository;
import com.ecoroute.repository.VehicleRepository;

import java.util.List;
import java.util.Random;

/**
 * Active Simulator Dashboard support: creates dynamic demand surges
 * (e.g. classroom dismissal, exam rushes) to stress-test the Dijkstra
 * routing and priority scheduling queue mechanisms, and randomly ages
 * fleet battery levels to mimic real operational wear.
 */
public class SimulationService {

    private final LocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final SchedulingQueueService queueService;
    private final Random random = new Random();

    private static final String[] FIRST_NAMES = {
            "Arun", "Priya", "Karthik", "Divya", "Sanjay", "Meena",
            "Vikram", "Anjali", "Rahul", "Sneha", "Kiran", "Deepa"
    };

    public SimulationService(LocationRepository locationRepository,
                              VehicleRepository vehicleRepository,
                              SchedulingQueueService queueService) {
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.queueService = queueService;
    }

    /** Generates a burst of ride requests, e.g. simulating a classroom dismissal. */
    public int generateDemandSurge(int numberOfStudents) {
        List<Location> allLocations = locationRepository.getAllLocations();
        int generated = 0;

        for (int i = 0; i < numberOfStudents; i++) {
            Location origin = allLocations.get(random.nextInt(allLocations.size()));
            Location destination = allLocations.get(random.nextInt(allLocations.size()));
            int attempts = 0;
            while (destination.getId().equals(origin.getId()) && attempts < 5) {
                destination = allLocations.get(random.nextInt(allLocations.size()));
                attempts++;
            }

            // 40% chance of being an urgent exam/lab student, 60% casual.
            boolean urgent = random.nextDouble() < 0.4;
            int minutesToExam = urgent ? (5 + random.nextInt(26)) : Integer.MAX_VALUE;
            VehicleType preferredType = random.nextBoolean() ? VehicleType.E_BIKE : VehicleType.E_SCOOTER;
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String studentId = "S" + (int) (System.nanoTime() % 100000);

            Student student = new Student(studentId, name, origin.getId(), destination.getId(),
                    minutesToExam, preferredType);
            queueService.submitRequest(student);
            generated++;
        }
        return generated;
    }

    /** Randomly ages fleet battery levels to mimic operational wear between simulation ticks. */
    public void simulateBatteryWear() {
        for (Vehicle vehicle : vehicleRepository.getAllVehicles()) {
            double wear = 1.0 + random.nextDouble() * 4.0; // 1% - 5% random drain
            vehicle.drainBattery(wear);
        }
    }
}
