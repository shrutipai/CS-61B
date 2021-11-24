package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;

    /** Constructor */
    public MaxArrayDeque(Comparator<T> c) {
        this.comp = c;
    }

    public T max() {
        return max(this.comp);
    }

    public T max(Comparator<T> c) {
        T max = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            if (c.compare(max, this.get(i)) <= 0) {
                max = this.get(i);
            }
        }
        return max;
    }
}
