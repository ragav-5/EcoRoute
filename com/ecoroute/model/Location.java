package com.ecoroute.model;

/**
 * Represents a vertex in the weighted campus graph
 * (an academic block, hostel, parking zone, etc.).
 */
public class Location {

    private final String id;
    private final String name;
    private final LocationType type;

    public Location(String id, String name, LocationType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%-4s | %-18s | %s", id, name, type);
    }
}
