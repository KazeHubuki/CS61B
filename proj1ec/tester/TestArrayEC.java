package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.Iterator;

public class TestArrayEC {

    @Test
    public void testRandomOperation() {
        StudentArrayDeque<Integer> studentAD = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> testAD = new ArrayDequeSolution<>();
        ArrayDequeSolution<String> callsSeries = new ArrayDequeSolution<>();
        for (int i = 0; i < 500; i += 1) {
            int operationNumber = StdRandom.uniform(4);
            if (operationNumber == 0) {
                studentAD.addFirst(1);
                testAD.addFirst(1);
                addOperation(callsSeries, String.format("addFirst(%d)", 1));
            } else if (operationNumber == 1) {
                studentAD.addLast(2);
                testAD.addLast(2);
                addOperation(callsSeries, String.format("addLast(%d)", 2));
            } else if (operationNumber == 2 && !testAD.isEmpty()) {
                addOperation(callsSeries, "removeFirst()");
                assertEquals(getMessage(callsSeries), testAD.removeFirst(), studentAD.removeFirst());
            } else if (operationNumber == 3 && !testAD.isEmpty()) {
                addOperation(callsSeries, "removeLast()");
                assertEquals(getMessage(callsSeries), testAD.removeLast(), studentAD.removeLast());
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
