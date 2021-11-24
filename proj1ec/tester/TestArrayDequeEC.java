package tester;

import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;
import java.util.ArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> A = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> B = new ArrayDequeSolution<>();

        String errors = "\n";

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                A.addLast(randVal);
                B.addLast(randVal);
                errors += "\naddLast(" + randVal + ")";
            } else if (operationNumber == 1) {
                // size
                int a = A.size();
                int b = B.size();
                errors += "\nsize()";
                assertEquals(errors, a, b);
            } else if (operationNumber == 2) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                A.addFirst(randVal);
                B.addFirst(randVal);
                errors += "\naddFirst(" + randVal + ")";
            } else if (operationNumber == 3) {
                // isEmpty
                boolean a = A.isEmpty();
                boolean b = B.isEmpty();
                errors += "\nisEmpty()";
                assertEquals(errors, a, b);
            } else if (operationNumber == 4) {
                // removeFirst
                if (A.isEmpty() || B.isEmpty()) {
                    continue;
                }
                int a = A.removeFirst();
                int b = B.removeFirst();
                errors += "\nremoveFirst()";
                assertEquals(errors, a, b);
            } else if (operationNumber == 5) {
                // removeLast
                if (A.isEmpty() || B.isEmpty()) {
                    continue;
                }
                int a = A.removeLast();
                int b = B.removeLast();
                errors += "\nremoveLast()";
                assertEquals(errors, a, b);
            }
        }
    }
}
