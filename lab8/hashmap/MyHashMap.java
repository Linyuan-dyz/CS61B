package hashmap;

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
    // You should probably define some more!
    private int initialSize;
    private double loadFactor;
    private int size;

    /** Constructors */
    public MyHashMap() {
        this.size = 0;
        this.initialSize = 16;
        this.loadFactor = 0.75;
        buckets = createTable(this.initialSize);
    }

    public MyHashMap(int initialSize) {
        this.size = 0;
        this.initialSize = initialSize;
        this.loadFactor = 0.75;
        buckets = createTable(this.initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.size = 0;
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(this.initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

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
    private Collection<Node>[] createTable(int tableSize) {
        Collection[] newTable = new Collection[tableSize];
        for(int i=0; i < tableSize; i++) {
            newTable[i] = createBucket();
        }
        return newTable;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    /** Removes all of the mappings from this map. */
    public void clear() {
        this.size = 0;
        buckets = createTable(this.initialSize);
    }

    @Override
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (get(key) == null) {
            return false;
        }
        return true;
    }

    @Override
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node targetNode = getNode(key);
        if (targetNode != null) {
            return targetNode.value;
        }
        return null;
    }

    private Node getNode(K key) {
        int keyHashCode = key.hashCode();
        int position = Math.floorMod(keyHashCode, this.initialSize);
        Iterator bucketIter = buckets[position].iterator();
        while (bucketIter.hasNext()) {
            Node newNode = (Node)bucketIter.next();
            if (newNode.key.equals(key)) {
                return newNode;
            }
        }
        return null;
    }

    @Override
    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return this.size;
    }

    @Override
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int keyHashCode = key.hashCode();
        if (!containsKey(key)) {
            this.size += 1;
            if ((double) size / initialSize > loadFactor) {
                resize();
            }
            int position = Math.floorMod(keyHashCode, this.initialSize);
            Node newNode = createNode(key, value);
            buckets[position].add(newNode);
        } else {
            Node targetNode = getNode(key);
            targetNode.value = value;
        }
    }

    private void putOriginAfterResize(Node node) {
        K key = node.key;
        int keyHashCode = key.hashCode();
        int position = Math.floorMod(keyHashCode, this.initialSize);
        buckets[position].add(node);
    }

    private void resize() {
        int currentSize = this.initialSize;
        this.initialSize *= 2;
        this.buckets = createTable(this.initialSize);
        for(int i=0; i < currentSize; i++) {
            Iterator bucketIter = buckets[i].iterator();
            while (bucketIter.hasNext()) {
                Node newNode = (Node) bucketIter.next();
                putOriginAfterResize(newNode);
            }
        }
    }

    @Override
    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for(int i=0; i < initialSize; i++) {
            Iterator bucketIter = buckets[i].iterator();
            while (bucketIter.hasNext()) {
                Node newNode = (Node) bucketIter.next();
                keySet.add(newNode.key);
            }
        }
        return keySet;
    }

    @Override
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        return new myHashMapIter();
    }

    private class myHashMapIter implements Iterator{
        int index;
        myHashMapIter() {
            this.index = 0;
        }

        public boolean hasNext() {
            if (index < MyHashMap.this.size) {
                return true;
            }
            return false;
        }

        public Node next() {
            int newIndex = 0;
            for(int i=0; i < MyHashMap.this.initialSize; i++) {
                Iterator bucketIter = MyHashMap.this.buckets[i].iterator();
                while (bucketIter.hasNext()) {
                    Node newNode = (Node) bucketIter.next();
                    if (newIndex == index) {
                        index += 1;
                        return newNode;
                    }
                    newIndex += 1;
                }
            }
            return null;
        }
    }

}
