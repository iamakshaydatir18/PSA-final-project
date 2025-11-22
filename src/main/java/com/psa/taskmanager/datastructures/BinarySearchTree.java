package com.psa.taskmanager.datastructures;

import java.util.List;
import java.util.function.Consumer;

/**
 * BST  Interface
 */
public interface BinarySearchTree<T> {

    // method for inserting node into tree
    void insert(T value);

    // check if node is present in tree
    boolean contains(T value);


    // method to find node
    T find(T value);

    // methid to remove node from a tree
    boolean remove(T value);

    // returns size of tree i.e no. of nodes
    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    // Performs an in-order traversal of  tree
    void inOrderTraversal(List<T> results);

    // returns list of elements in sorted order
    List<T> toInOrderList();

    // removes all elements
    void clear();
}

