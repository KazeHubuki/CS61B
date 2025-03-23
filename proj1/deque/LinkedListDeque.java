package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {

    private static class ListNode<T> {
        T item;
        ListNode<T> prev;
        ListNode<T> next;

        ListNode(T item) {
            this.item = item;
            this.prev = null;
            this.next = null;
        }
    }

    private ListNode<T> sentinel;
    private int size;

    public LinkedListDeque() {
        size = 0;
        sentinel = new ListNode<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item) {
        ListNode<T> newNode = new ListNode<>(item);
        sentinel.next.prev = newNode;
        newNode.next = sentinel.next;
        sentinel.next = newNode;
        newNode.prev = sentinel;
        size += 1;
    }

    public void addLast(T item) {
        ListNode<T> newNode = new ListNode<>(item);
        sentinel.prev.next = newNode;
        newNode.prev = sentinel.prev;
        sentinel.prev = newNode;
        newNode.next = sentinel;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    public void printDeque() {
        ListNode<T> p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        ListNode<T> first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size -= 1;
        return first.item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        ListNode<T> last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        size -= 1;
        return last.item;
    }

    public T get(int index) {
        int i = 0;
        ListNode<T> p = sentinel.next;
        while (i < index) {
            if (p == sentinel) {
                return null;
            }
            p = p.next;
            i += 1;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        ListNode<T> p = sentinel.next;
        return helper(index, p);
    }

    private T helper(int index, ListNode<T> p) {
        if (p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.item;
        } else {
            return helper(index - 1, p.next);
        }
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private ListNode<T> current = sentinel.next;

        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> other = (Deque<?>) o;
        if (this.size() != other.size()) {
            return false;
        }
        Iterator<T> thisIterator = this.iterator();
        Iterator<?> otherIterator = other.iterator();
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            T thisElement = thisIterator.next();
            Object otherElement = otherIterator.next();
            if (!(thisElement.equals(otherElement))) {
                return false;
            }
        }
        return true;
    }
}

