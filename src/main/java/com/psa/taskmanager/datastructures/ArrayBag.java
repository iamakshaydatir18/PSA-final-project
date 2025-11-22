package com.psa.taskmanager.datastructures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Array-backed implementation.
 *
 * @param <T> element type
 */
public class ArrayBag<T> implements Bag<T> {
	
	//ADT implementation of Bag
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    public ArrayBag() {
        this(DEFAULT_CAPACITY);
    }
    
    //constructor to initialize 
    public ArrayBag(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }
    
    //add element to bag
    @Override
    public boolean add(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        return true;
    }
    
    //remove element from a bag
    @Override
    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                int elementsToMove = size - i - 1;
                if (elementsToMove > 0) {
                    System.arraycopy(elements, i + 1, elements, i, elementsToMove);
                }
                elements[--size] = null;
                shrinkIfNecessary();
                return true;
            }
        }
        return false;
    }
    
    //check if elements exists 
    @Override
    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    //clear existing bag
    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
        if (elements.length > DEFAULT_CAPACITY) {
            elements = new Object[DEFAULT_CAPACITY];
        }
    }

    //iterator 
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) elements[cursor++];
            }
        };
    }

    //ensure capacity
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + (elements.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
    
    // remove unwanted
    private void shrinkIfNecessary() {
        int currentCapacity = elements.length;
        if (size <= currentCapacity / 4 && currentCapacity > DEFAULT_CAPACITY) {
            int newCapacity = Math.max(DEFAULT_CAPACITY, currentCapacity / 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
}

