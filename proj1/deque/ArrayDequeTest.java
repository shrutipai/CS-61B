package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ArrayDequeTest {

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        java.util.ArrayDeque<Integer> B = new java.util.ArrayDeque<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                A.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int a = A.size();
                int b = B.size();
                assertEquals(a, b);
            } else if (operationNumber == 2) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                A.addFirst(randVal);
                B.addFirst(randVal);
            } else if (operationNumber == 3) {
                // isEmpty
                boolean a = A.isEmpty();
                boolean b = B.isEmpty();
                assertEquals(a, b);
            } else if (operationNumber == 4) {
                // removeFirst
                if (A.isEmpty() || B.isEmpty()) {
                    continue;
                }
                int a = A.removeFirst();
                int b = B.removeFirst();
                assertEquals(a, b);
            } else if (operationNumber == 5) {
                // removeLast
                if (A.isEmpty() || B.isEmpty()) {
                    continue;
                }
                int a = A.removeLast();
                int b = B.removeLast();
                assertEquals(a,b);
            }
        }
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        java.util.Iterator<Integer> B = A.iterator();

        System.out.println(B.hasNext());
    }
}
