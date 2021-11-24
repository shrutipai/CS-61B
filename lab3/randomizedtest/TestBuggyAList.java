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
        BuggyAList<Integer> buggy = new BuggyAList<>();
        AListNoResizing<Integer> notBuggy = new AListNoResizing<>();

        for (int i = 4; i < 7; i ++) {
            buggy.addLast(i);
            notBuggy.addLast(i);
        }

        assertEquals(notBuggy.removeLast(), buggy.removeLast());
        assertEquals(notBuggy.removeLast(), buggy.removeLast());
        assertEquals(notBuggy.removeLast(), buggy.removeLast());

    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggy.addLast(randVal);

            } else if (operationNumber == 1) {
                // size
                int a = L.size();
                int b = buggy.size();
                assertEquals(a, b);

            } else if (operationNumber == 2) {
                // getLast
                if (L.size() < 1 || buggy.size() < 1){
                    continue;
                } else {
                    int a = L.getLast();
                    int b = buggy.getLast();
                    assertEquals(a, b);
                }

            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() < 1 || buggy.size() < 1){
                    continue;
                } else {
                    int a = L.removeLast();
                    int b = buggy.removeLast();
                    assertEquals(a, b);
                }

            }
        }
    }
}
