package com.psa.taskmanager.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Doubly linked list implementation of SimpleList.
 *
 * @param <T> element type
 */

public class LinkedSimpleList<T> implements SimpleList<T>{
	private static final class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

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
        size++;
    }

    @Override
    public void add(int index, T element) {
        checkPositionIndex(index, true);
        if (index == size) {
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
        size++;
    }

    @Override
    public T get(int index) {
        checkElementIndex(index);
        return node(index).data;
    }

    @Override
    public T set(int index, T element) {
        checkElementIndex(index);
        Node<T> target = node(index);
        T oldValue = target.data;
        target.data = element;
        return oldValue;
    }

    @Override
    public T remove(int index) {
        checkElementIndex(index);
        Node<T> target = node(index);
        return unlink(target);
    }

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

    @Override
    public int size() {
        return size;
    }

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
        size = 0;
    }

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

    private void checkElementIndex(int index) {
        checkPositionIndex(index, false);
    }

    private void checkPositionIndex(int index, boolean allowEnd) {
        int upperBound = allowEnd ? size : size - 1;
        if (index < 0 || index > upperBound) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private Node<T> node(int index) {
        if (index < (size >> 1)) {
            Node<T> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node<T> current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

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
        size--;
        return element;
    }
}

