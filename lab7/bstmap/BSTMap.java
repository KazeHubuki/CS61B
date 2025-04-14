package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {

        private K key;
        private V value;
        private Node left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size = 0;

    public BSTMap() {
        // Initialize a new, empty BSTMap.
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key argument to put() is null.");
        }
        root = put(root, key, value);
    }

    private Node put(Node currNode, K key, V value) {
        if (currNode == null) {
            size += 1;
            return new Node(key, value);
        }

        if (key.compareTo(currNode.key) > 0) {
            currNode.right = put(currNode.right, key, value);
        } else if (key.compareTo(currNode.key) < 0) {
            currNode.left = put(currNode.left, key, value);
        } else {
            currNode.value = value;
        }
        return currNode;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to containsKey() is null.");
        }
        return keySet().contains(key);
    }

    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to get() is null.");
        }
        return get(root, key);
    }

    private V get(Node currNode, K key) {
        if (currNode == null) {
            return null;
        }

        if (key.compareTo(currNode.key) == 0) {
            return currNode.value;
        } else {
            return (key.compareTo(currNode.key) > 0)
                    ? get(currNode.right, key)
                    : get(currNode.left, key);
        }
    }

    public int size() {
        return size;
    }

    public Set<K> keySet() {
        Set<K> keySet = new LinkedHashSet<>();
        keySetConstructor(root, keySet);
        return keySet;
    }

    private void keySetConstructor(Node currNode, Set<K> keySet) {
        if (currNode == null) {
            return;
        }
        keySetConstructor(currNode.left, keySet);
        keySet.add(currNode.key);
        keySetConstructor(currNode.right, keySet);
    }

    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to remove() is null.");
        }
        Node removedNode = remove(root, key);
        return (removedNode != null) ? removedNode.value : null;
    }

    public V remove(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key argument to remove() is null.");
        }
        if (value == null) {
            return null;
        }
        Node removedNode = remove(root, key);
        V removedNodeValue = (removedNode != null) ? removedNode.value : null;
        return (value.equals(removedNodeValue)) ? removedNodeValue : null;
    }

    private Node remove(Node currNode, K key) {
        if (currNode == null) {
            return null;
        }

        if (key.compareTo(currNode.key) > 0) {
            currNode.right = remove(currNode.right, key);
        } else if (key.compareTo(currNode.key) < 0) {
            currNode.left = remove(currNode.left, key);
        } else {
            currNode = handleRemove(currNode);
            size -= 1;
        }
        return currNode;
    }

    private Node handleRemove(Node currNode) {
        if (currNode.left == null && currNode.right == null) {
            return null;
        } else if (currNode.left != null && currNode.right == null) {
            return currNode.left;
        } else if (currNode.left == null && currNode.right != null) {
            return currNode.right;
        } else {
            Node successor = currNode.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            currNode.key = successor.key;
            currNode.value = successor.value;
            currNode.right = remove(currNode.right, successor.key);
            return currNode;
        }
    }

    private class BSTMapIterator implements Iterator<K> {

        private final List<K> keys;
        private int index;

        public BSTMapIterator() {
            keys = new ArrayList<>(keySet());
            index = 0;
        }

        public boolean hasNext() {
            return index < keys.size();
        }

        public K next() {
            K key = keys.get(index);
            index += 1;
            return key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }
}
