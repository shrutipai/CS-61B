package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    /** Returns iterator */
    public Iterator<T> iterator() {
        return new LLIterator();
    }

    private class LLIterator implements Iterator<T> {
        private int pos;
        LLIterator() {
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

    /** Node Class*/
    private class TNode {
        private T item;
        private TNode next;
        private TNode prev;

        TNode(TNode p, T i, TNode n) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private TNode sent;
    private int size;

    /** Constructor */
    public LinkedListDeque() {
        sent = new TNode(null, null, null);
        sent.next = sent;
        sent.prev = sent;
        size = 0;
    }
    @Override
    /** Adds an item of type T to the front of the deque. Assume that item is never null.*/
    public void addFirst(T item) {
        TNode added = new TNode(sent, item, sent.next);
        sent.next.prev = added;
        sent.next = added;
        size += 1;
    }
    @Override
    /**Adds an item of type T to the back of the deque. Assume that item is never null.*/
    public void addLast(T item) {
        TNode added = new TNode(sent.prev, item, sent);
        sent.prev.next = added;
        sent.prev = added;
        size += 1;
    }
    /**
    @Override
    /** Returns true if deque is empty, false otherwise.
    public boolean isEmpty() {
        return size == 0;
    } */

    @Override
    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     *  Once all the items have been printed, print out a new line.
     */

    @Override
    public void printDeque() {
        TNode start = sent.next;
        while (start != sent) {
            System.out.print(start.item + " ");
            start = start.next;
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
            T res = sent.next.item;
            //new line
            sent.next.next.prev = sent;
            sent.next = sent.next.next;
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
            T res = sent.prev.item;
            //new line
            sent.prev.prev.next = sent;
            sent.prev = sent.prev.prev;
            size -= 1;
            return res;
        }
    }

    @Override
    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     */
    public T get(int index) {
        if (index < size) {
            TNode start = sent.next;
            while (index > 0) {
                start = start.next;
                index -= 1;
            }
            return start.item;
        } else {
            return null;
        }
    }
    /** Recursive helper method */
    private T recursionBuddy(int index, TNode sentinel) {
        if (index == 0) {
            return sentinel.next.item;
        }
        return recursionBuddy(index - 1, sentinel.next);
    }
    /** Same as get() but uses Recursion */
    public T getRecursive(int index) {
        return recursionBuddy(index, sent);
    }

    /** Returns whether or not the parameter o is equal to the Deque.*/
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
