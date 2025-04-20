package hashmap;

import org.checkerframework.checker.units.qual.C;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int actualSize;

    private int capacity;
    private double maxLoad;

    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private static final int RESIZE_FACTOR = 2;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.capacity = initialSize;
        this.maxLoad = maxLoad;
        actualSize = 0;

        buckets = createTable(capacity);
        for (int i = 0; i < capacity; i += 1) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) { return new Node(key, value); }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        return (Collection<Node>[]) new Collection[tableSize];
    }

    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> bucket = buckets[index];
        if (bucket != null && !bucket.removeIf(node -> key.equals(node.key))) {
            // If bucket is not null, and it does not contain the key originally.
            actualSize += 1;
        }
        bucket.add(new Node(key, value));

        if (getLoadFactor() > maxLoad) {
            resize();
        }
    }

    private double getLoadFactor() {
        return (double) actualSize / capacity;
    }

    private void resize() {
        Collection<Node>[] oldBuckets = buckets;

        capacity = capacity * RESIZE_FACTOR;
        buckets = createTable(capacity);
        for (int i = 0; i < capacity; i += 1) {
            buckets[i] = createBucket();
        }

        actualSize = 0;
        for (Collection<Node> bucket : oldBuckets) {
            if (bucket == null) {
                continue;
            }
            for (Node node : bucket) {
                put(node.key, node.value); // This call will increase the "actualSize" variable
            }
        }
    }

    public V get(K key) {
        Node node = getNode(key);
        return (node != null) ? node.value : null;
    }

    private Node getNode(K key) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> bucket = buckets[index];
        if (bucket == null) {
            return null;
        }
        for (Node node : bucket) {
            if (key.equals(node.key)) {
                return node;
            }
        }
        return null;
    }

    public int size() {
        return actualSize;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket == null) {
                continue;
            }
            for (Node node : bucket) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets[i] = createBucket();
        }
        actualSize = 0;
    }

    public V remove(K key) {
        Node nodeToRemove = getNode(key);
        if (nodeToRemove == null) {
            return null;
        } else {
            return remove(key, nodeToRemove.value);
        }
    }

    public V remove(K key, V value) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> bucket = buckets[index];

        Node nodeToRemove = getNode(key);
        if (nodeToRemove == null) {
            return null;
        }
        bucket.remove(nodeToRemove);
        actualSize -= 1;
        return nodeToRemove.value;
    }

    private class MyHashMapIterator implements Iterator<K> {
        int index = 0;
        List<K> keys;

        public MyHashMapIterator() {
            keys = new ArrayList<>(keySet());
        }

        @Override
        public boolean hasNext() {
            return index < keys.size();
        }

        @Override
        public K next() {
            K k = keys.get(index);
            index += 1;
            return k;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
}
