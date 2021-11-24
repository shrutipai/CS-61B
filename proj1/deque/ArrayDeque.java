package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {

    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {
        private int pos;
        ADIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            if (isEmpty()) {
                return false;
            }
            return pos < size;
        }

        @Override
        public T next() {
            if (isEmpty()) {
                return null;
            }
            T returnItem = get(pos);
            pos += 1;
            return returnItem;
        }
    }

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private double usageRatio = 0.25;
    private int capacity;


    /** Constructor */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 7;
        nextLast = 0;
        capacity = 8;
    }

    /** Helper method */
    private int next(int i, String direction) {
        if (direction.equals("first")) {
            if (i == 0) {
                return capacity - 1;
            } else {
                return i - 1;
            }
        } else {
            if (i == capacity - 1) {
                return 0;
            } else {
                return i + 1;
            }
        }
    }

    /** Resizes ArrayDeque up if at capacity */
    private void checkResizeUp() {
        if (size == capacity) {
            int newCapacity = (int) (capacity * 1.25);
            resize(newCapacity);
        }
    }

    /** Resizes ArrayDeque down if it will be below usage ratio after removing */
    private void checkResizeDown() {
        double currRatio = ((double) (size) - 1) / (double) (capacity);
        if (capacity >= 16 & currRatio < usageRatio) {
            int newCapacity = (int) (capacity / 2);
            resize(newCapacity);
        } else {
            return;
        }
    }

    /** Resizes array to newCapacity */
    private void resize(int newCapacity) {
        T[] newItems = (T[]) new Object[newCapacity];
        int first = next(nextFirst, "last");
        int last = next(nextLast, "first");

        if (first < last) {
            int length = last - first + 1;
            System.arraycopy(items, first, newItems, 0, length);
            items = newItems;
            size = length;
            capacity = newCapacity;
            nextFirst = next(0, "first");
            nextLast = next(length - 1, "last");

        } else {
            int length1 = capacity - first;
            int length2 = last + 1;
            System.arraycopy(items, first, newItems, 0, length1);
            System.arraycopy(items, 0, newItems, length1, length2);
            items = newItems;
            size = length1 + length2;
            capacity = newCapacity;
            nextFirst = next(0, "first");
            nextLast = length1 + length2;
        }
    }

    @Override
    /** Adds an item of type T to the front of the deque. Assume that item is never null.*/
    public void addFirst(T item) {
        items[nextFirst] = item;
        size += 1;
        nextFirst = next(nextFirst, "first");
        checkResizeUp();
    }

    @Override
    /**Adds an item of type T to the back of the deque. Assume that item is never null.*/
    public void addLast(T item) {
        items[nextLast] = item;
        size += 1;
        nextLast = next(nextLast, "last");
        checkResizeUp();
    }

    @Override
    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    @Override
    /** Prints the items in the deque from first to last, separated by a space.
     *  Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        int first = next(nextFirst, "last");
        while (first != nextLast) {
            System.out.print(items[first] + " ");
            first = next(first, "last");
        }
        System.out.println(" ");
    }

    @Override
    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            checkResizeDown();
            int first = next(nextFirst, "last");
            T res = items[first];
            items[first] = null;
            nextFirst = first;
            size -= 1;
            return res;
        }
    }

    @Override
    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            checkResizeDown();
            int last = next(nextLast, "first");
            T res = items[last];
            items[last] = null;
            nextLast = last;
            size -= 1;
            return res;
        }
    }

    @Override
    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     */
    public T get(int index) {
        if (index > capacity - 1) {
            return null;
        }
        int returnIndex = 1 + nextFirst + index;
        if (returnIndex > capacity - 1) {
            returnIndex -= capacity;
        }
        return items[returnIndex];
    }

    public boolean equals(Object o) {
        if (o instanceof Deque && this.size() == (((Deque<T>) o).size())) {
            for (int i = 0; i < this.size; i++) {
                if (!this.get(i).equals(((Deque<T>) o).get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}


