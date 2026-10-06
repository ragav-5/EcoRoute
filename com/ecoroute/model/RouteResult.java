package com.ecoroute.model;

import java.util.Collections;
import java.util.List;

/**
 * Holds the result of a Dijkstra energy-optimal route computation.
 */
public class RouteResult {

    private final List<String> path;
    private final double totalDistanceKm;
    private final double totalEnergyCost;
    private final boolean reachable;

    public RouteResult(List<String> path, double totalDistanceKm, double totalEnergyCost, boolean reachable) {
        this.path = path;
        this.totalDistanceKm = totalDistanceKm;
        this.totalEnergyCost = totalEnergyCost;
        this.reachable = reachable;
    }

    public static RouteResult unreachable() {
        return new RouteResult(Collections.emptyList(), 0.0, 0.0, false);
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public double getTotalEnergyCost() {
        return totalEnergyCost;
    }

    public boolean isReachable() {
        return reachable;
    }
}
