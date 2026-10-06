package com.ecoroute.model;

/**
 * Represents a student ride request. Students with imminent exams/labs are
 * given higher priority in the scheduling queue than casual transit users.
 */
public class Student implements Comparable<Student> {

    private static int sequenceCounter = 0;

    private final String id;
    private final String name;
    private final String currentLocationId;
    private final String destinationLocationId;
    private final int minutesToExam; // Integer.MAX_VALUE => casual user, no deadline
    private final VehicleType preferredType;
    private final int sequence; // preserves FIFO order among equal priorities

    public Student(String id, String name, String currentLocationId, String destinationLocationId,
                   int minutesToExam, VehicleType preferredType) {
        this.id = id;
        this.name = name;
        this.currentLocationId = currentLocationId;
        this.destinationLocationId = destinationLocationId;
        this.minutesToExam = minutesToExam;
        this.preferredType = preferredType;
        this.sequence = sequenceCounter++;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public String getDestinationLocationId() {
        return destinationLocationId;
    }

    public int getMinutesToExam() {
        return minutesToExam;
    }

    public VehicleType getPreferredType() {
        return preferredType;
    }

    public boolean hasDeadline() {
        return minutesToExam < Integer.MAX_VALUE;
    }

    /**
     * Priority ordering: fewer minutes-to-exam means more urgent (comes first).
     * Ties are broken by request arrival order (FIFO).
     */
    @Override
    public int compareTo(Student other) {
        int cmp = Integer.compare(this.minutesToExam, other.minutesToExam);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.sequence, other.sequence);
    }

    @Override
    public String toString() {
        String deadline = hasDeadline() ? (minutesToExam + " min") : "none (casual)";
        return String.format("%-4s | %-10s | %-4s -> %-4s | Deadline: %-12s | Pref: %s",
                id, name, currentLocationId, destinationLocationId, deadline, preferredType.getLabel());
    }
}
