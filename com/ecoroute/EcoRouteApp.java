
    // ---------------------------------------------------------------
    // 6. Fleet status
    // ---------------------------------------------------------------
    private void viewFleetStatus() {
        ConsoleUtil.printHeader("VEHICLE FLEET STATUS");
        for (Vehicle v : vehicleService.getFleet()) {
            System.out.println(v);
        }
    }package com.ecoroute;

import com.ecoroute.model.Edge;
import com.ecoroute.model.Location;
import com.ecoroute.model.RouteResult;
import com.ecoroute.model.Student;
import com.ecoroute.model.Vehicle;
import com.ecoroute.model.VehicleType;
import com.ecoroute.repository.LocationRepository;
import com.ecoroute.repository.StudentQueueRepository;
import com.ecoroute.repository.VehicleRepository;
import com.ecoroute.service.EcoRouteCoordinatorService;
import com.ecoroute.service.EcoRouteCoordinatorService.DispatchOutcome;
import com.ecoroute.service.GraphService;
import com.ecoroute.service.SchedulingQueueService;
import com.ecoroute.service.SimulationService;
import com.ecoroute.service.VehicleService;
import com.ecoroute.util.ConsoleUtil;
import com.ecoroute.util.DataSeeder;

import java.util.List;

    /**
     * CS5304 - Java Programming
     * EcoRoute: Campus EV & Micro-Mobility Optimizer
     *
     * Console entry point. Wires up repositories and services, seeds demo
     * campus data, and drives an interactive terminal menu.
     */
    public class EcoRouteApp {

        private final LocationRepository locationRepository = new LocationRepository();
        private final VehicleRepository vehicleRepository = new VehicleRepository();
        private final StudentQueueRepository studentQueueRepository = new StudentQueueRepository();

        private final GraphService graphService = new GraphService(locationRepository);
        private final VehicleService vehicleService = new VehicleService(vehicleRepository);
        private final SchedulingQueueService queueService = new SchedulingQueueService(studentQueueRepository);
        private final SimulationService simulationService =
                new SimulationService(locationRepository, vehicleRepository, queueService);
        private final EcoRouteCoordinatorService coordinator =
                new EcoRouteCoordinatorService(graphService, vehicleService, queueService);

        public static void main(String[] args) {
            new EcoRouteApp().run();
        }

        private void run() {
            DataSeeder.seedCampusGraph(locationRepository);
            DataSeeder.seedFleet(vehicleRepository);

            ConsoleUtil.printHeader("ECOROUTE: CAMPUS EV & MICRO-MOBILITY OPTIMIZER");
            System.out.println("CS5304 - Java Programming | Zeroth Review Simulator");
            System.out.println("Type MAP anytime you're asked for a location id to see the codes.\n");

            boolean running = true;
            while (running) {
                printMenu();
                int choice = ConsoleUtil.readInt("Select an option: ", 0, 9);
                switch (choice) {
                    case 1 -> viewCampusMap();
                    case 2 -> findOptimalRoute();
                    case 3 -> submitStudentRequest();
                    case 4 -> processQueueOnce();
                    case 5 -> processEntireQueue();
                    case 6 -> viewFleetStatus();
                    case 7 -> viewPriorityQueue();
                    case 8 -> runSimulatorDashboard();
                    case 9 -> rechargeFleet();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
                if (running) {
                    ConsoleUtil.pause();
                }
            }

            System.out.println("\nShutting down EcoRoute Coordinator. Goodbye!");
        }

        private void printMenu() {
            ConsoleUtil.printDivider();
            System.out.println("MAIN MENU");
            ConsoleUtil.printDivider();
            System.out.println(" 1. View Campus Map (locations & connections)");
            System.out.println(" 2. Find Energy-Optimal Route (Dijkstra)");
            System.out.println(" 3. Submit Student Ride Request");
            System.out.println(" 4. Process Next Request in Queue");
            System.out.println(" 5. Process Entire Queue");
            System.out.println(" 6. View Vehicle Fleet Status");
            System.out.println(" 7. View Priority Queue (schedule order)");
            System.out.println(" 8. Run Simulator Dashboard (demand surge)");
            System.out.println(" 9. Recharge Entire Fleet (maintenance reset)");
            System.out.println(" 0. Exit");
            ConsoleUtil.printDivider();
        }

        // ---------------------------------------------------------------
        // 1. Campus map
        // ---------------------------------------------------------------
        private void viewCampusMap() {
            ConsoleUtil.printHeader("CAMPUS GRAPH: LOCATIONS");
            System.out.println("ID   | Name               | Type");
            ConsoleUtil.printDivider();
            for (Location loc : locationRepository.getAllLocations()) {
                System.out.println(loc);
            }

            System.out.println();
            ConsoleUtil.printHeader("CAMPUS GRAPH: CONNECTIONS (weighted edges)");
            System.out.println("From -> To   | Distance(km) | Terrain Slope Factor");
            ConsoleUtil.printDivider();
            for (Location loc : locationRepository.getAllLocations()) {
                for (Edge edge : locationRepository.getEdgesFrom(loc.getId())) {
                    // print each undirected edge once (only when from-id < to-id lexicographically by numeric suffix)
                    if (loc.getId().compareTo(edge.getTargetLocationId()) < 0) {
                        System.out.printf("%-4s -> %-4s | %-12.2f | %.2f%n",
                                loc.getId(), edge.getTargetLocationId(), edge.getDistanceKm(), edge.getTerrainSlopeFactor());
                    }
                }
            }
        }

        // ---------------------------------------------------------------
        // 2. Route finding
        // ---------------------------------------------------------------
        private void findOptimalRoute() {
            ConsoleUtil.printHeader("FIND ENERGY-OPTIMAL ROUTE");
            maybeShowMap();
            String start = ConsoleUtil.readLocationId("Start location id: ", locationRepository);
            String end = ConsoleUtil.readLocationId("Destination location id: ", locationRepository);
            VehicleType type = chooseVehicleType();

            RouteResult result = graphService.findEnergyOptimalRoute(start, end, type);
            printRouteResult(result, type);
        }

        private void printRouteResult(RouteResult result, VehicleType type) {
            if (!result.isReachable()) {
                System.out.println("No route exists between the selected locations.");
                return;
            }
            System.out.println("\nVehicle type: " + type.getLabel());
            System.out.println("Path: " + String.join(" -> ", resolveNames(result.getPath())));
            System.out.printf("Total distance: %.2f km%n", result.getTotalDistanceKm());
            System.out.printf("Total energy cost (weighted): %.2f units%n", result.getTotalEnergyCost());
        }

        private List<String> resolveNames(List<String> ids) {
            return ids.stream().map(id -> {
                Location loc = locationRepository.getLocation(id);
                return loc != null ? loc.getId() + ":" + loc.getName() : id;
            }).toList();
        }

        // ---------------------------------------------------------------
        // 3. Submit ride request
        // ---------------------------------------------------------------
        private void submitStudentRequest() {
            ConsoleUtil.printHeader("SUBMIT STUDENT RIDE REQUEST");
            maybeShowMap();
            String name = ConsoleUtil.readNonEmptyString("Student name: ");
            String origin = ConsoleUtil.readLocationId("Current location id: ", locationRepository);
            String destination = ConsoleUtil.readLocationId("Destination location id: ", locationRepository);
            VehicleType preferred = chooseVehicleType();

            int hasExam = ConsoleUtil.readInt("Does the student have an imminent exam/lab? (1=Yes, 0=No): ", 0, 1);
            int minutesToExam = Integer.MAX_VALUE;
            if (hasExam == 1) {
                minutesToExam = ConsoleUtil.readInt("Minutes until exam/lab starts (1-180): ", 1, 180);
            }

            String studentId = "S" + (int) (System.nanoTime() % 100000);
            Student student = new Student(studentId, name, origin, destination, minutesToExam, preferred);
            queueService.submitRequest(student);

            System.out.println("\nRequest submitted and placed in the priority queue:");
            System.out.println(student);
        }

        // ---------------------------------------------------------------
        // 4 & 5. Process queue
        // ---------------------------------------------------------------
        private void processQueueOnce() {
            ConsoleUtil.printHeader("PROCESS NEXT REQUEST");
            if (!queueService.hasPendingRequests()) {
                System.out.println("Queue is empty. Nothing to process.");
                return;
            }
            DispatchOutcome outcome = coordinator.processNextInQueue();
            printOutcome(outcome);
        }

        private void processEntireQueue() {
            ConsoleUtil.printHeader("PROCESS ENTIRE QUEUE");
            if (!queueService.hasPendingRequests()) {
                System.out.println("Queue is empty. Nothing to process.");
                return;
            }
            int processed = 0;
            while (queueService.hasPendingRequests()) {
                DispatchOutcome outcome = coordinator.processNextInQueue();
                printOutcome(outcome);
                System.out.println();
                processed++;
            }
            System.out.println("Processed " + processed + " request(s).");
        }

        private void printOutcome(DispatchOutcome outcome) {
            if (outcome == null) {
                System.out.println("Queue was empty.");
                return;
            }
            System.out.println("Student: " + outcome.student);
            if (outcome.success) {
                System.out.println("Status: DISPATCHED");
                System.out.println("Vehicle assigned: " + outcome.assignedVehicle);
                printRouteResult(outcome.routeResult, outcome.student.getPreferredType());
            } else {
                System.out.println("Status: FAILED - " + outcome.message);
            }
        }


        // ---------------------------------------------------------------
    // 7. Priority queue view
    // ---------------------------------------------------------------
    private void viewPriorityQueue() {
        ConsoleUtil.printHeader("SCHEDULE-BASED PRIORITY QUEUE (current order)");
        List<Student> ordered = queueService.viewOrderedQueue();
        if (ordered.isEmpty()) {
            System.out.println("Queue is currently empty.");
            return;
        }
        int rank = 1;
        for (Student s : ordered) {
            System.out.printf("#%-3d %s%n", rank++, s);
        }
    }

    // ---------------------------------------------------------------
    // 8. Simulator dashboard
    // ---------------------------------------------------------------
    private void runSimulatorDashboard() {
        ConsoleUtil.printHeader("ACTIVE SIMULATOR DASHBOARD");
        System.out.println("Simulate a demand surge (e.g. classroom dismissal or exam rush).");
        int count = ConsoleUtil.readInt("Number of student requests to generate (1-50): ", 1, 50);
        int generated = simulationService.generateDemandSurge(count);
        System.out.println(generated + " simulated ride requests added to the priority queue.");

        int wear = ConsoleUtil.readInt("Also simulate random fleet battery wear this tick? (1=Yes, 0=No): ", 0, 1);
        if (wear == 1) {
            simulationService.simulateBatteryWear();
            System.out.println("Battery wear applied across the fleet.");
        }
        System.out.println("Tip: use option 7 to inspect queue order, then option 5 to stress-test dispatch.");
    }

    // ---------------------------------------------------------------
    // 9. Recharge
    // ---------------------------------------------------------------
    private void rechargeFleet() {
        ConsoleUtil.printHeader("FLEET MAINTENANCE: FULL RECHARGE");
        vehicleService.rechargeAll();
        System.out.println("All vehicles recharged to 100% and returned to AVAILABLE (where possible).");
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------
    private VehicleType chooseVehicleType() {
        System.out.println("Preferred vehicle type: 1) E-Bike   2) E-Scooter");
        int choice = ConsoleUtil.readInt("Choose (1-2): ", 1, 2);
        return choice == 1 ? VehicleType.E_BIKE : VehicleType.E_SCOOTER;
    }

    private void maybeShowMap() {
        // Lightweight reminder; full map viewable via menu option 1.
        System.out.println("(Locations: L1 Main Gate, L2 Library, L3 CSE Block, L4 Mech Block, L5 Admin Block,");
        System.out.println(" L6 Hostel A, L7 Hostel B, L8 Food Court, L9 Sports Complex, L10 Parking Zone B)");
    }
}
