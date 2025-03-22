package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
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
    public void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        System.arraycopy(items, head, newItems, 0, size - head);
        System.arraycopy(items, 0, newItems, size - head, head);
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
        if (size == items.length){
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
        T item = items[tail];
        items[tail] = null;
        tail = (tail - 1 + items.length) % items.length;
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
            return (items[index + 1] != null);
        }

        @Override
        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrayDeque<?>)) {
            return false;
        }
        ArrayDeque<?> other = (ArrayDeque<?>) o;
        if (this.size() != other.size()) {
            return false;
        }
        int index = 0;
        while (this.get(index) != null || other.get(index) != null) {
            if (!this.get(index).equals(other.get(index))) {
                return false;
            }
            index += 1;
        }
        return true;
    }
}
