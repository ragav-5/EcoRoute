package com.ecoroute.service;

import com.ecoroute.model.Student;
import com.ecoroute.repository.StudentQueueRepository;

import java.util.List;

/**
 * Automated schedule-based queue: leverages a Java PriorityQueue backend
 * (via StudentQueueRepository) to dynamically bump priority ranks of
 * students with imminent exam start times or lab sessions ahead of
 * casual transit users.
 */
public class SchedulingQueueService {

    private final StudentQueueRepository queueRepository;

    public SchedulingQueueService(StudentQueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public void submitRequest(Student student) {
        queueRepository.enqueue(student);
    }

    public Student pullNextForProcessing() {
        return queueRepository.dequeueNext();
    }

    public boolean hasPendingRequests() {
        return !queueRepository.isEmpty();
    }

    public int pendingCount() {
        return queueRepository.size();
    }

    public List<Student> viewOrderedQueue() {
        return queueRepository.snapshotOrdered();
    }

    public List<Student> viewHistory() {
        return queueRepository.getProcessedHistory();
    }
}
