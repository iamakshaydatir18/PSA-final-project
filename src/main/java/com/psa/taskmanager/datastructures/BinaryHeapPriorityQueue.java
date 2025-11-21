package com.psa.taskmanager.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Binary heap implementation of {@link PriorityQueue}.
 *
 * @param <T> element type
 */
public class BinaryHeapPriorityQueue<T> implements PriorityQueue<T> {

    private static final int DEFAULT_CAPACITY = 11;

    private final Comparator<T> comparator;
    private Object[] elements;
    private int size;

     public BinaryHeapPriorityQueue() {
        this(null);
     }

    public BinaryHeapPriorityQueue(Comparator<T> comparator) {
        this.comparator = comparator;
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    @Override
    public void insert(T element) {
        Objects.requireNonNull(element, "element must not be null");
        ensureCapacity(size + 1);
        elements[size] = element;
        siftUp(size++);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T peek() {
        return size == 0 ? null : (T) elements[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T extract() {
        if (size == 0) {
            return null;
        }
        T result = (T) elements[0];
        T tail = (T) elements[--size];
        elements[size] = null;
        if (size > 0) {
            elements[0] = tail;
            siftDown(0);
        }
        shrinkIfNecessary();
        return result;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
        if (elements.length > DEFAULT_CAPACITY) {
            elements = new Object[DEFAULT_CAPACITY];
        }
    }

    @Override
    public Comparator<T> comparator() {
        return comparator;
    }

    @Override
    public List<T> toList() {
        BinaryHeapPriorityQueue<T> copy = new BinaryHeapPriorityQueue<>(comparator);
        copy.elements = Arrays.copyOf(elements, elements.length);
        copy.size = size;

        List<T> result = new ArrayList<>(size);
        while (!copy.isEmpty()) {
            T extracted = copy.extract();
            if (extracted != null) {
                result.add(extracted);
            }
        }
        return result;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + (elements.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    private void shrinkIfNecessary() {
        int currentCapacity = elements.length;
        if (size <= currentCapacity / 4 && currentCapacity > DEFAULT_CAPACITY) {
            int newCapacity = Math.max(DEFAULT_CAPACITY, currentCapacity / 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        Object[] array = this.elements;
        T target = (T) array[index];
        while (index > 0) {
            int parentIndex = (index - 1) / 2; 
            T parent = (T) array[parentIndex];
            if (compare(target, parent) >= 0) {
                break;
            }
            array[index] = parent;
            index = parentIndex;
        }
        array[index] = target;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        Object[] array = this.elements;
        int half = size / 2;  
        T target = (T) array[index];
        while (index < half) {
            int left = index * 2 + 1;  
            int right = left + 1;      

            int smallest = left;
            T child = (T) array[left];
            if (right < size) {
                T rightChild = (T) array[right];
                if (compare(rightChild, child) < 0) {
                    smallest = right;
                    child = rightChild;
                }
            }

            if (compare(target, child) <= 0) {
                break;
            }

            array[index] = child;
            index = smallest;
        }
        array[index] = target;
    }

    @SuppressWarnings("unchecked")
    private int compare(T first, T second) {
        if (comparator != null) {
            return comparator.compare(first, second);
        }
        if (first instanceof Comparable<?> comparable) {
            @SuppressWarnings("unchecked")
            Comparable<? super T> typedComparable = (Comparable<? super T>) comparable;
            return typedComparable.compareTo(second);
        }
        throw new NoSuchElementException("Elements are not comparable and no comparator was provided");
    }
}

