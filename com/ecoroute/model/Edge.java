package com.ecoroute.model;

/**
 * Represents a weighted edge (physical pathway) between two campus locations.
 * Stores raw distance and a terrain slope factor which increases energy burn
 * on inclines, ramps, or rough terrain.
 */
public class Edge {

    private final String targetLocationId;
    private final double distanceKm;
    private final double terrainSlopeFactor; // 0.0 (flat) - 1.0 (steep)

    public Edge(String targetLocationId, double distanceKm, double terrainSlopeFactor) {
        this.targetLocationId = targetLocationId;
        this.distanceKm = distanceKm;
        this.terrainSlopeFactor = terrainSlopeFactor;
    }

    public String getTargetLocationId() {
        return targetLocationId;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getTerrainSlopeFactor() {
        return terrainSlopeFactor;
    }

    /**
     * Base energy cost of traversing this edge, before vehicle-specific
     * consumption factors are applied.
     */
    public double baseEnergyCost() {
        return distanceKm * (1.0 + terrainSlopeFactor);
    }
}
