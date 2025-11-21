package com.psa.taskmanager.datastructures;

import java.util.List;
import java.util.function.Consumer;

/**
 * Binary search tree abstract data type.
 *
 * @param <T> element type
 */
public interface BinarySearchTree<T> {

    /**
     * Inserts a value into the tree.
     *
     * @param value value to insert
     */
    void insert(T value);

    /**
     * @param value value to search for
     * @return true if found
     */
    boolean contains(T value);

    /**
     * @param value value to search for
     * @return matching value or null if not present
     */
    T find(T value);

    /**
     * Removes a value from the tree.
     *
     * @param value value to remove
     * @return true if a node was removed
     */
    boolean remove(T value);

    /**
     * @return number of elements stored
     */
    int size();

    /**
     * @return true if no elements contained
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Performs an in-order traversal of the tree.
     *
     * @param action consumer invoked for each visited element
     */
    void inOrderTraversal(List<T> results);

    /**
     * @return immutable list of elements in sorted order
     */
    List<T> toInOrderList();

    /**
     * Removes all elements in the tree
     */
    void clear();
}

