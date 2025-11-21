package com.psa.taskmanager.datastructures;

import java.util.Iterator;

/**
 * A simple Bag abstract data type that permits duplicate elements
 * and does not enforce ordering.
 *
 * @param <T> element type
 */
public interface Bag<T> extends Iterable<T> {

    /**
     * Adds an element to the bag.
     *
     * @param element element to add
     * @return true if the bag accepted the element
     */
    boolean add(T element);

    /**
     * Removes a single occurrence of the specified element from the bag.
     *
     * @param element element to remove
     * @return true if an element was removed
     */
    boolean remove(T element);

    /**
     * @param element element to check
     * @return true if the element exists in the bag
     */
    boolean contains(T element);

    /**
     * @return number of elements currently stored
     */
    int size();

    /**
     * @return true if the bag has no elements
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Removes all elements from the bag.
     */
    void clear();

    /**
     * Provides an iterator over the bag's elements. Iteration order
     * is unspecified.
     *
     * @return iterator over elements
     */
    @Override
    Iterator<T> iterator();
}

