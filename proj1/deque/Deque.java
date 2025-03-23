package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T>{
    int size();

    @Override
    Iterator<T> iterator();

    @Override
    boolean equals(Object o);

    default boolean isEmpty() {
        return (size() == 0);
    }
}
