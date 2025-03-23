package deque;

import jh61b.junit.In;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void resizingDoesNotCauseNulls() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 8; i += 1) {
            a.addFirst(10);
        }
        assertNotEquals(null, a.removeFirst());
        assertNotEquals(null, a.removeLast());
    }

    @Test
    public void resizingDoesNotCauseNulls2() {
        ArrayDeque<Integer> b = new ArrayDeque<>();
        for (int i = 0; i < 8; i += 1) {
            b.addLast(10);
        }
        assertNotEquals(null, b.removeFirst());
        assertNotEquals(null, b.removeLast());
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(10);
        assertEquals(10, (int) ad.removeLast());
    }

    @Test
    public void fillUpEmptyFillUpAgain() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 9; i += 1) {
            ad.addFirst(10);
        }
        for (int i = 0; i < 9; i += 1) {
            ad.removeFirst();
        }
        for (int i = 0; i < 9; i += 1) {
            ad.addFirst(10);
        }

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        for (int i = 0; i < 9; i += 1) {
            ad2.addFirst(10);
        }

        assertEquals(ad2, ad);
    }

    @Test
    public void testMemoryOptimization() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 5; i += 1) {
            ad.addFirst(10);
        }
        for (int i = 0; i < 4; i += 1) {
            ad.addLast(10);
        }
        for (int i = 0; i < 6; i += 1) {
            ad.removeLast();
        }

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        for (int i = 0; i < 3; i += 1) {
            ad2.addFirst(10);
        }
        assertEquals(ad2.get(0), ad.get(0));
        assertEquals(ad2.get(1), ad.get(1));
        assertEquals(ad2.get(2), ad.get(2));
    }
}
