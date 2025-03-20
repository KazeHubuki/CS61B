package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> a = new AListNoResizing<Integer>();
        BuggyAList<Integer> b = new BuggyAList<Integer>();

        a.addLast(4);
        a.addLast(5);
        a.addLast(6);
        b.addLast(4);
        b.addLast(5);
        b.addLast(6);

        assertEquals(a.size(), b.size());
        assertEquals(a.removeLast(), b.removeLast());
        assertEquals(a.removeLast(), b.removeLast());
        assertEquals(a.removeLast(), b.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> a = new AListNoResizing<Integer>();
        BuggyAList<Integer> b = new BuggyAList<Integer>();

        int N = 1000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
                b.addLast(randVal);
            } else if (operationNumber == 1) {
                assertEquals(a.size(), b.size());
            } else if (operationNumber == 2) {
                if (a.size() == 0) {
                    continue;
                }
                assertEquals(a.getLast(), b.getLast());
            } else if (operationNumber == 3) {
                if (a.size() == 0) {
                    continue;
                }
                assertEquals(a.removeLast(), b.removeLast());
            }
        }
    }
}
