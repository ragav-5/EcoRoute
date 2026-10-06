package com.ecoroute.service;

import com.ecoroute.model.Edge;
import com.ecoroute.model.RouteResult;
import com.ecoroute.model.VehicleType;
import com.ecoroute.repository.LocationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

/**
 * Computes energy-optimal routes across the weighted campus graph using
 * Dijkstra's shortest-path algorithm. The "weight" minimized is total
 * energy cost (distance x terrain factor x vehicle consumption factor),
 * not raw distance, so scooters and bikes can be routed differently.
 */
public class GraphService {

    private final LocationRepository locationRepository;

    public GraphService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /** Internal helper node used inside the priority queue during Dijkstra. */
    private static class NodeCost implements Comparable<NodeCost> {
        final String locationId;
        final double cost;

        NodeCost(String locationId, double cost) {
            this.locationId = locationId;
            this.cost = cost;
        }

        @Override
        public int compareTo(NodeCost other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    public RouteResult findEnergyOptimalRoute(String startId, String endId, VehicleType vehicleType) {
        if (!locationRepository.exists(startId) || !locationRepository.exists(endId)) {
            return RouteResult.unreachable();
        }
        if (startId.equals(endId)) {
            List<String> path = new ArrayList<>();
            path.add(startId);
            return new RouteResult(path, 0.0, 0.0, true);
        }

        Map<String, Double> minCost = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeCost> frontier = new PriorityQueue<>();

        for (com.ecoroute.model.Location loc : locationRepository.getAllLocations()) {
            minCost.put(loc.getId(), Double.POSITIVE_INFINITY);
        }
        minCost.put(startId, 0.0);
        frontier.add(new NodeCost(startId, 0.0));

        double distanceAtEnd = 0.0;
        Map<String, Double> minDistance = new HashMap<>();
        minDistance.put(startId, 0.0);

        while (!frontier.isEmpty()) {
            NodeCost current = frontier.poll();
            if (visited.contains(current.locationId)) {
                continue;
            }
            visited.add(current.locationId);

            if (current.locationId.equals(endId)) {
                break;
            }

            for (Edge edge : locationRepository.getEdgesFrom(current.locationId)) {
                String neighbor = edge.getTargetLocationId();
                if (visited.contains(neighbor)) {
                    continue;
                }
                double edgeCost = edge.baseEnergyCost() * vehicleType.getEnergyConsumptionFactor();
                double candidateCost = minCost.get(current.locationId) + edgeCost;
                if (candidateCost < minCost.get(neighbor)) {
                    minCost.put(neighbor, candidateCost);
                    previous.put(neighbor, current.locationId);
                    minDistance.put(neighbor, minDistance.get(current.locationId) + edge.getDistanceKm());
                    frontier.add(new NodeCost(neighbor, candidateCost));
                }
            }
        }

        if (!minCost.containsKey(endId) || Double.isInfinite(minCost.get(endId))) {
            return RouteResult.unreachable();
        }

        List<String> path = new ArrayList<>();
        String step = endId;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        return new RouteResult(path, minDistance.getOrDefault(endId, 0.0), minCost.get(endId), true);
    }
}
