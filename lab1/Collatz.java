/** Class that prints the Collatz sequence starting from a given number.
 *  @author SHRUTI PAI
 */
public class Collatz {
    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            System.out.print(nextNumber(n) + " ");
            n = nextNumber(n);
        }

    }
    /** method that takes in an integer and returns the next number in the Collatz sequence */
    public static int nextNumber(int n) {
        if (n%2 == 0) {
            n = n/2;
        } else {
            n = 3*n + 1;
        }
        return n;
    }
}

