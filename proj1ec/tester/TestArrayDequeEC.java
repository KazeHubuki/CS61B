package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void testRandomOperation() {
        StudentArrayDeque<Integer> studentAD = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> testAD = new ArrayDequeSolution<>();
        ArrayDequeSolution<String> callsSeries = new ArrayDequeSolution<>();
        for (int i = 0; i < 500; i += 1) {
            int operationNumber = StdRandom.uniform(4);
            int randomValue = StdRandom.uniform(100);

            if (operationNumber == 0) {
                studentAD.addFirst(randomValue);
                testAD.addFirst(randomValue);
                addOperation(callsSeries, String.format("addFirst(%d)", randomValue));
            } else if (operationNumber == 1) {
                studentAD.addLast(randomValue);
                testAD.addLast(randomValue);
                addOperation(callsSeries, String.format("addLast(%d)", randomValue));
            } else if (operationNumber == 2 && !testAD.isEmpty()) {
                addOperation(callsSeries, "removeFirst()");
                Integer expected = testAD.removeFirst();
                Integer actual = studentAD.removeFirst();
                assertEquals(getMessage(callsSeries), expected, actual);
            } else if (operationNumber == 3 && !testAD.isEmpty()) {
                addOperation(callsSeries, "removeLast()");
                Integer expected = testAD.removeLast();
                Integer actual = studentAD.removeLast();
                assertEquals(getMessage(callsSeries), expected, actual);
            }
        }
    }

    private void addOperation(ArrayDequeSolution<String> callsSeries, String operation) {
        if (callsSeries.size() == 3) {
            callsSeries.removeFirst();
        }
        callsSeries.addLast(operation);
    }

    private String getMessage(ArrayDequeSolution<String> callsSeries) {
        StringBuilder message = new StringBuilder();
        message.append("\n");
        for (String call : callsSeries) {
            message.append(call).append("\n");
        }
        return message.toString();
    }
}
