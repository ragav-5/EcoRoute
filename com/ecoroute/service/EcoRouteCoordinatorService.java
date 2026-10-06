package com.ecoroute.service;

import com.ecoroute.model.RouteResult;
import com.ecoroute.model.Student;
import com.ecoroute.model.Vehicle;

import java.util.Optional;

/**
 * Top-level orchestrator that ties the graph routing engine, the vehicle
 * fleet manager, and the schedule-based priority queue together into a
 * single "process next request" workflow for the EcoRoute coordinator.
 */
public class EcoRouteCoordinatorService {

    private final GraphService graphService;
    private final VehicleService vehicleService;
    private final SchedulingQueueService queueService;

    public EcoRouteCoordinatorService(GraphService graphService,
                                       VehicleService vehicleService,
                                       SchedulingQueueService queueService) {
        this.graphService = graphService;
        this.vehicleService = vehicleService;
        this.queueService = queueService;
    }

    /** Result of attempting to serve a single student's ride request. */
    public static class DispatchOutcome {
        public final Student student;
        public final boolean success;
        public final String message;
        public final RouteResult routeResult;
        public final Vehicle assignedVehicle;

        DispatchOutcome(Student student, boolean success, String message,
                         RouteResult routeResult, Vehicle assignedVehicle) {
            this.student = student;
            this.success = success;
            this.message = message;
            this.routeResult = routeResult;
            this.assignedVehicle = assignedVehicle;
        }
    }

    /** Pops the highest-priority student and attempts to route + dispatch a vehicle for them. */
    public DispatchOutcome processNextInQueue() {
        Student student = queueService.pullNextForProcessing();
        if (student == null) {
            return null;
        }
        return serveStudent(student);
    }

    private DispatchOutcome serveStudent(Student student) {
        RouteResult route = graphService.findEnergyOptimalRoute(
                student.getCurrentLocationId(), student.getDestinationLocationId(), student.getPreferredType());

        if (!route.isReachable()) {
            return new DispatchOutcome(student, false, "No route found between requested locations.", route, null);
        }

        Optional<Vehicle> vehicleOpt = vehicleService.findVehicleForPickup(
                student.getCurrentLocationId(), student.getPreferredType());

        if (vehicleOpt.isEmpty()) {
            return new DispatchOutcome(student, false,
                    "Route found, but no available vehicle is docked at the pickup point.", route, null);
        }

        Vehicle vehicle = vehicleOpt.get();
        vehicleService.dispatchAlongRoute(vehicle, route);

        return new DispatchOutcome(student, true, "Dispatched successfully.", route, vehicle);
    }

    public GraphService getGraphService() {
        return graphService;
    }

    public VehicleService getVehicleService() {
        return vehicleService;
    }

    public SchedulingQueueService getQueueService() {
        return queueService;
    }
}
