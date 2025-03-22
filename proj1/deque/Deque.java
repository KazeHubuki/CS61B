package deque;

public interface Deque<T> {
    int size();

    default boolean isEmpty() {
        return (size() == 0);
    }
}
