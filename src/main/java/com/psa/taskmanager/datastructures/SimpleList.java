package com.psa.taskmanager.datastructures;

import java.util.Iterator;

/**
 * Simple list abstract data type with positional access.
 * This interface defines a basic list structure that supports element insertion,
 * removal, and access by index position.
 *
 * @param <T> the type of elements held in this list
 */
public interface SimpleList<T> extends Iterable<T>{
    
    /**
     * Inserts the specified element to the end of this list.
     *
     * @param element the element to be added to this list
     */
    void add(T element);

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right.
     *
     * @param index the index at which the element is to be inserted
     * @param element the element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    void add(int index, T element);

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    T get(int index);

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index the index of the element to replace
     * @param element the element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    T set(int index, T element);

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    T remove(int index);

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present. If this list does not contain the element, it is unchanged.
     *
     * @param element the element to be removed from this list, if present
     * @return true if this list contained the specified element and it was removed
     */
    boolean remove(T element);

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    int size();

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list contains no elements, false otherwise
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Removes all elements from this list. The list will be empty after this call returns.
     */
    void clear();

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list
     */
    @Override
    Iterator<T> iterator();
}