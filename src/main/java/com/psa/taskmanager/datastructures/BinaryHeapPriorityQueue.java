package com.psa.taskmanager.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Binary heap implementation.
 *
 * @param <T> element type
 */
public class BinaryHeapPriorityQueue<T> implements PriorityQueue<T> {
	
	//variables and array based store for implementing PQ
    private static final int DEFAULT_CAPACITY = 11;
    private final Comparator<T> comparator;
    private Object[] elements;
    private int size;

     public BinaryHeapPriorityQueue() {
        this(null);
     }
     
     //constructor to initialize
    public BinaryHeapPriorityQueue(Comparator<T> comparator) {
        this.comparator = comparator;
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    /*
     * insert element at the end of array
     * call siftUp (Heapify) to arrange sorting
     */
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

    /*
     * remove element at the start of array
     * move tail element of array to start
     * call siftDown (Heapify) to arrange sorting
     */
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

    //return size
    @Override
    public int size() {
        return size;
    }
    
    //clear every elements.
    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
        if (elements.length > DEFAULT_CAPACITY) {
            elements = new Object[DEFAULT_CAPACITY];
        }
    }
    
    //assign comparator to compare for sorting
    @Override
    public Comparator<T> comparator() {
        return comparator;
    }
    
    //create copy of heap & extract and return list
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
    
    // ensure capacity and increment 
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + (elements.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
    
    //remove uecessary space from array.
    private void shrinkIfNecessary() {
        int currentCapacity = elements.length;
        if (size <= currentCapacity / 4 && currentCapacity > DEFAULT_CAPACITY) {
            int newCapacity = Math.max(DEFAULT_CAPACITY, currentCapacity / 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
    
    /*
     * checks with parent from getting index i.e (index - 1)/2;  
     * compare parent and current index 
     * swap if current index element is smaller than parent
     * do this until index > 0
     */
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
    
    /*
     * retrieves left & right index & compares with current index
     * swap if found current element has low proirity
     * continue until index reaches half
     */
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

    /*
     * compare method for comparing two task using given compare method or else use objects compareTo method
     * 
     */
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

