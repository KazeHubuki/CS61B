package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T>{
    int size();



    default boolean isEmpty() {
        return (size() == 0);
    }
}
