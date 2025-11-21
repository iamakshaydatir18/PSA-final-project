package com.psa.taskmanager.datastructures;

import java.util.Comparator;
import java.util.List;

/**
 * Priority queue abstract data type.
 *
 * @param <T> element type
 */
public interface PriorityQueue<T> {

    /**
     * Inserts an element into the priority queue.
     *
     * @param element element to insert
     */
    void insert(T element);

    /**
     * @return element with the highest priority without removing it
     */
    T peek();

    /**
     * Removes and returns the element with the highest priority.
     *
     * @return element with highest priority or null if empty
     */
    T extract();

    /**
     * @return number of elements stored
     */
    int size();

    /**
     * @return true if no elements are present
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Removes all elements.
     */
    void clear();

    /**
     * @return comparator used to order elements, may be null if natural ordering is used
     */
    Comparator<T> comparator();

    /**
     * Creates a snapshot list of the elements ordered by priority without modifying the queue.
     *
     * @return list of elements sorted in priority order
     */
    List<T> toList();
}

