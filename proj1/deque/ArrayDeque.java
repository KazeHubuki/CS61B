package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int head;
    private int tail;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = 0;
        tail = 0;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            newItems[i] = get(i);
        }
        items = newItems;
        head = 0;
        tail = size;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        head = (head - 1 + items.length) % items.length;
        items[head] = item;
        size += 1;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[tail] = item;
        tail = (tail + 1) % items.length;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T item = items[head];
        items[head] = null;
        head = (head + 1) % items.length;
        size -= 1;
        if (items.length >= 16 && (double) size / items.length < 0.25) {
            resize(items.length / 4);
        }
        return item;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        tail = (tail - 1 + items.length) % items.length;
        T item = items[tail];
        items[tail] = null;
        size -= 1;
        if (items.length >= 16 && (double) size / items.length < 0.25) {
            resize(items.length / 4);
        }
        return item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(head + index) % items.length];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return (get(index) != null);
        }

        @Override
        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o instanceof Deque) {
            Deque<T> target = (Deque<T>) o;
            if (target.size() != size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!target.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
