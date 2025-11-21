package com.psa.taskmanager.datastructures;

import java.util.Iterator;

/**
 * Simple list abstract data type with positional access.
 *
 * @param <T> element type
 */
public interface SimpleList<T> extends Iterable<T>{
	void add(T element);

    void add(int index, T element);

    T get(int index);

    T set(int index, T element);

    T remove(int index);

    boolean remove(T element);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    void clear();

    @Override
    Iterator<T> iterator();
}
