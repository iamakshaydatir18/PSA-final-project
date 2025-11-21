package com.psa.taskmanager.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Doubly linked list implementation of SimpleList.
 * This implementation provides efficient insertion and removal operations at both ends,
 * with O(n) access time for arbitrary positions.
 *
 * @param <T> the type of elements held in this list
 */
public class LinkedSimpleList<T> implements SimpleList<T>{
    
    /**
     * Node class representing an element in the doubly linked list.
     * Each node contains data and references to the previous and next nodes.
     *
     * @param <E> the type of element stored in the node
     */
    private static final class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        /**
         * Constructs a new node with the specified data.
         *
         * @param data the element to be stored in this node
         */
        Node(E data) {
            this.data = data;
        }
    }

    /** The first node in the list. */
    private Node<T> head;
    
    /** The last node in the list. */
    private Node<T> tail;
    
    /** The number of elements in the list. */
    private int numberOfEntries;

    /**
     * Inserts the specified element to the end of this list.
     *
     * @param element the element to be added
     */
    @Override
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        numberOfEntries++;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right.
     *
     * @param index the index at which the element is to be inserted
     * @param element the element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public void add(int index, T element) {
    	checkElementIndex(index, true);
        if (index == numberOfEntries) {
            add(element);
            return;
        }
        Node<T> target = node(index);
        Node<T> newNode = new Node<>(element);
        Node<T> previous = target.prev;
        newNode.next = target;
        target.prev = newNode;
        if (previous == null) {
            head = newNode;
        } else {
            previous.next = newNode;
            newNode.prev = previous;
        }
        numberOfEntries++;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public T get(int index) {
        checkElementIndex(index, false);
        return node(index).data;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index the index of the element to replace
     * @param element the element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public T set(int index, T element) {
        checkElementIndex(index, false);
        Node<T> target = node(index);
        T oldValue = target.data;
        target.data = element;
        return oldValue;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public T remove(int index) {
        checkElementIndex(index, false);
        Node<T> target = node(index);
        return unlink(target);
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present. If the list does not contain the element, it is unchanged.
     *
     * @param element the element to be removed from this list, if present
     * @return true if this list contained the specified element
     */
    @Override
    public boolean remove(T element) {
        for (Node<T> current = head; current != null; current = current.next) {
            if (Objects.equals(current.data, element)) {
                unlink(current);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return numberOfEntries;
    }

    /**
     * Removes all elements from this list. The list will be empty after this call returns.
     */
    @Override
    public void clear() {
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.prev = null;
            current.next = null;
            current.data = null;
            current = next;
        }
        head = tail = null;
        numberOfEntries = 0;
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.data;
                current = current.next;
                return value;
            }
        };
    }

    /**
     * Checks if the given index is a valid element index.
     *
     * @param index the index to check
     * @param allowEnd whether to allow index equal to size (for insertion)
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    private void checkElementIndex(int index, boolean allowEnd) {
        int upperBound = allowEnd ? numberOfEntries : numberOfEntries - 1;
        if (index < 0 || index > upperBound) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + numberOfEntries);
        }
    }


    /**
     * Returns the node at the specified position in this list.
     * Uses an optimization to traverse from the nearest end (head or tail).
     *
     * @param index the index of the node to return
     * @return the node at the specified position
     */
    private Node<T> node(int index) {
        if (index < (numberOfEntries >> 1)) {
            Node<T> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node<T> current = tail;
            for (int i = numberOfEntries - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    /**
     * Unlinks the specified node from this list.
     * Updates the references of adjacent nodes and clears the removed node.
     *
     * @param node the node to be unlinked
     * @return the element that was stored in the unlinked node
     */
    private T unlink(Node<T> node) {
        Node<T> previous = node.prev;
        Node<T> next = node.next;

        if (previous == null) {
            head = next;
        } else {
            previous.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = previous;
        } else {
            next.prev = previous;
            node.next = null;
        }

        T element = node.data;
        node.data = null;
        numberOfEntries--;
        return element;
    }
}