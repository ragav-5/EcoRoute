package com.ecoroute.repository;

import com.ecoroute.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Repository wrapping the Java PriorityQueue backend used for the
 * automated schedule-based ride queue. Students with imminent exam
 * start times are bumped ahead of casual transit users automatically,
 * because Student's natural ordering (compareTo) drives queue order.
 */
public class StudentQueueRepository {

    private final PriorityQueue<Student> queue = new PriorityQueue<>();
    private final List<Student> processedHistory = new ArrayList<>();

    public void enqueue(Student student) {
        queue.add(student);
    }

    public Student dequeueNext() {
        Student next = queue.poll();
        if (next != null) {
            processedHistory.add(next);
        }
        return next;
    }

    public Student peekNext() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    /** Returns a priority-ordered snapshot without mutating the live queue. */
    public List<Student> snapshotOrdered() {
        PriorityQueue<Student> copy = new PriorityQueue<>(queue);
        List<Student> ordered = new ArrayList<>();
        while (!copy.isEmpty()) {
            ordered.add(copy.poll());
        }
        return ordered;
    }

    public List<Student> getProcessedHistory() {
        return Collections.unmodifiableList(processedHistory);
    }
}
