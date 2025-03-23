package deque;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

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

    @Test
    public void testAddFrontRemoveAddAgain() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            deque.addFirst(i);
        }
        for (int i = 15; i >= 0; i--) {
            assertEquals(Integer.valueOf(i), deque.removeFirst());
        }
        for (int i = 0; i < 32; i++) {
            deque.addFirst(i);
        }
        for (int i = 31; i >= 0; i--) {
            assertEquals(Integer.valueOf(i), deque.removeFirst());
        }
    }

    @Test
    public void testMultipleInstances() {
        ArrayDeque<Integer> deque1 = new ArrayDeque<>();
        ArrayDeque<Integer> deque2 = new ArrayDeque<>();

        // 向 deque1 添加元素
        for (int i = 0; i < 100; i++) {
            deque1.addLast(i);
        }

        // 向 deque2 添加元素
        for (int i = 200; i < 300; i++) {
            deque2.addLast(i);
        }

        // 验证 deque1 和 deque2 独立
        for (int i = 0; i < 100; i++) {
            assertEquals(Integer.valueOf(i), deque1.removeFirst());
        }
        for (int i = 200; i < 300; i++) {
            assertEquals(Integer.valueOf(i), deque2.removeFirst());
        }
    }

    @Test
    public void testDifferentElementTypes() {
        // 创建两个不同类型的 Deque
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        LinkedListDeque<String> strDeque = new LinkedListDeque<>();
        assertTrue(intDeque.equals(strDeque));

        // 逐步添加元素并验证每一步
        for (int i = 0; i < 100; i++) {
            intDeque.addLast(i);               // 添加 Integer 元素
            strDeque.addLast(String.valueOf(i)); // 添加 String 元素

            // 断言：两个 Deque 的内容类型不同，应始终返回 false
            assertFalse(
                    "Deques should not be equal after adding element " + i,
                    intDeque.equals(strDeque)
            );
        }

        // 移除部分元素并验证剩余部分
        for (int i = 0; i < 50; i++) {
            intDeque.removeLast(); // 移除 Integer 元素
            strDeque.removeLast(); // 移除 String 元素

            // 断言：移除后剩余元素类型仍不同，应返回 false
            assertFalse(
                    "Deques should not be equal after removing " + (i + 1) + " elements",
                    intDeque.equals(strDeque)
            );
        }
    }

    @Test
    public void testRandomOperations() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        java.util.Deque<Integer> referenceDeque = new java.util.ArrayDeque<>();

        Random rand = new Random(42); // 固定随机种子
        int operations = 500;
        for (int i = 0; i < operations; i++) {
            int choice = rand.nextInt(7); // 0-6
            if (choice < 2) { // 30% addFirst
                int value = rand.nextInt(1000);
                deque.addFirst(value);
                referenceDeque.addFirst(value);
            } else if (choice < 4) { // 30% addLast
                int value = rand.nextInt(1000);
                deque.addLast(value);
                referenceDeque.addLast(value);
            } else if (choice < 5 && !referenceDeque.isEmpty()) { // 20% removeFirst
                assertEquals(referenceDeque.removeFirst(), deque.removeFirst());
            } else if (choice < 6 && !referenceDeque.isEmpty()) { // 20% removeLast
                assertEquals(referenceDeque.removeLast(), deque.removeLast());
            } else if (!referenceDeque.isEmpty()) { // 20% get
                int index = rand.nextInt(referenceDeque.size());
                Iterator<Integer> refIt = referenceDeque.iterator();
                Integer expected = null;
                for (int j = 0; j <= index; j++) {
                    expected = refIt.next();
                }
                assertEquals(expected, deque.get(index));
            }
        }
    }

    @Test
    public void testCircularPointerWrap() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        // 交替添加和移除，迫使 head/tail 循环
        for (int i = 0; i < 100; i++) {
            deque.addFirst(i);
            deque.removeLast();
        }
        assertTrue(deque.isEmpty());

        // 重新填充并验证
        for (int i = 0; i < 8; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < 8; i++) {
            assertEquals(Integer.valueOf(i), deque.removeFirst());
        }
    }
}
