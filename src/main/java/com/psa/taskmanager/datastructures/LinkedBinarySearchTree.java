package com.psa.taskmanager.datastructures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.psa.taskmanager.model.Task;

// BST Implementation

public class LinkedBinarySearchTree<T> implements BinarySearchTree<T> {

    // Node strucutre
    private static final class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;

        Node(E value) {
            this.value = value;
        }
    }

    private final Comparator<T> comparator;
    private Node<T> root;
    private int size;

    public LinkedBinarySearchTree() {
        this(null);
    }

    public LinkedBinarySearchTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void insert(T value) {
        Objects.requireNonNull(value, "value must not be null");
        root = insertRecursive(root, value);
    }

    // insert into tree using BST properties 
    private Node<T> insertRecursive(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }
        int cmp = compare(value, node.value);
        if (cmp < 0) {
            node.left = insertRecursive(node.left, value);
        } else if (cmp > 0) {
            node.right = insertRecursive(node.right, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public boolean contains(T value) {
        return findNode(value) != null;
    }

    @Override
    public T find(T value) {
        Node<T> node = findNode(value);
        return node == null ? null : node.value;
    }

    private Node<T> findNode(T value) {
        Objects.requireNonNull(value, "value must not be null");
        Node<T> current = root;
        while (current != null) {
            int cmp = compare(value, current.value);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    @Override
    public boolean remove(T value) {
        int initialSize = size;
        root = removeRecursive(root, value);
        return size < initialSize;
    }

    // remove node from BST, tree should be adjusted
    private Node<T> removeRecursive(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        int cmp = compare(value, node.value);
        if (cmp < 0) {
            node.left = removeRecursive(node.left, value);
        } else if (cmp > 0) {
            node.right = removeRecursive(node.right, value);
        } else {
            size--;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node<T> successor = minNode(node.right);
            node.value = successor.value;
            node.right = deleteMin(node.right);
        }
        return node;
    }

    private Node<T> minNode(Node<T> node) {
        Node<T> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private Node<T> deleteMin(Node<T> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void inOrderTraversal(List<T> results) {
        inOrder(root, results);
    }

    private void inOrder(Node<T> node, List<T> results) {
        if (node == null) {
            return;
        }
        inOrder(node.left, results);
        results.add(node.value);
        inOrder(node.right, results);
    }

    @Override
    public List<T> toInOrderList() {
        List<T> results = new ArrayList<>(size);
        inOrderTraversal(results);
        
        return List.copyOf(results);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    private int compare(T first, T second) {
    	return ((Comparable<T>) first).compareTo(second);
    }
}

