package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private BSTNode root;
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;

        private BSTNode(K key, V value, int size) {
            this.size = size;
            this.value = value;
            this.key = key;
        }
    }
    @Override
    public void clear() {
        this.root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        } else {
            if (keyNull(root, key)) {
                return true;
            }
            return (get(key) != null);
        }
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (key == null) throw new IllegalArgumentException();
        if (node != null){
            int compVal = key.compareTo(node.key);
            if (compVal > 0) {
                return get(node.right, key);
            } else if (compVal < 0) {
                return get(node.left, key);
            } else {
                return node.value;
            }
        } else {
            return null;
        }
    }

    private boolean keyNull(BSTNode node, K key) {
        if (key == null) throw new IllegalArgumentException();
        if (node != null){
            int compVal = key.compareTo(node.key);
            if (compVal > 0) {
                keyNull(node.right, key);
            } else if (compVal < 0) {
                keyNull(node.left, key);
            } else {
                return (node.value == null);
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        } else {
            return node.size;
        }
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value, 1);
        }
        int compVal = key.compareTo(node.key);
        if (compVal > 0) {
            node.right = put(node.right, key, value);
        } else if (compVal < 0) {
            node.left = put(node.left, key, value);
        } else {
            node.value = value;
        }

        node.size = 1 + size(node.right) + size(node.left);
        return node;
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.value);
        printInOrder(node.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
