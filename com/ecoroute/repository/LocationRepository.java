package com.ecoroute.repository;

import com.ecoroute.model.Edge;
import com.ecoroute.model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory repository holding the weighted campus graph:
 * vertex data (Location) and adjacency lists (Edge) keyed by location id.
 */
public class LocationRepository {

    private final Map<String, Location> locations = new LinkedHashMap<>();
    private final Map<String, List<Edge>> adjacencyList = new LinkedHashMap<>();

    public void addLocation(Location location) {
        locations.put(location.getId(), location);
        adjacencyList.putIfAbsent(location.getId(), new ArrayList<>());
    }

    /** Adds a bidirectional weighted edge between two existing locations. */
    public void addBidirectionalEdge(String locationIdA, String locationIdB, double distanceKm, double terrainSlopeFactor) {
        if (!locations.containsKey(locationIdA) || !locations.containsKey(locationIdB)) {
            throw new IllegalArgumentException("Both locations must exist before creating an edge.");
        }
        adjacencyList.get(locationIdA).add(new Edge(locationIdB, distanceKm, terrainSlopeFactor));
        adjacencyList.get(locationIdB).add(new Edge(locationIdA, distanceKm, terrainSlopeFactor));
    }

    public Location getLocation(String id) {
        return locations.get(id);
    }

    public boolean exists(String id) {
        return locations.containsKey(id);
    }

    public List<Location> getAllLocations() {
        return new ArrayList<>(locations.values());
    }

    public List<Edge> getEdgesFrom(String locationId) {
        return Collections.unmodifiableList(adjacencyList.getOrDefault(locationId, new ArrayList<>()));
    }

    public int locationCount() {
        return locations.size();
    }
}
