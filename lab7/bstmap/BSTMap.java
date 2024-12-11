package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{

    int size;

    BSTNode root;

    /** Returns the value corresponding to KEY or null if no such value exists. */
    public V get(K key) {
        if (root == null) {
            return null;
        }
        BSTNode node = root.get(key);
        if (!node.key.equals(key)) {
            return null;
        }
        return (V) node.val;
    }

    @Override
    public int size() {
        return size;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    /** Inserts the key-value pair of KEY and VALUE into this dictionary,
     *  replacing the previous value associated to KEY, if any. */
    public void put(K key, V val) {
        if (root == null) {
            root = new BSTNode<>(key, val, null, null);
            size += 1;
        } else {
            BSTNode node = root.get(key);
            if (!node.key.equals(key)) {
                if (key.compareTo(node.key) < 0) {
                    node.left = new BSTNode<>(key, val, null, null);
                    size += 1;
                }
                if (key.compareTo(node.key) > 0) {
                    node.right = new BSTNode<>(key, val, null, null);
                    size += 1;
                }
            } else {
                node.val = val;
            }
        }
    }

    /** Returns true if and only if this dictionary contains KEY as the
     *  key of some key-value pair. */
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        BSTNode node = root.get(key);
        if (!node.key.equals(key)) {
            return false;
        } else {
            return true;
        }
    }

    public Iterator<K> iterator() {
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
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }


    /** Represents one node in the linked list that stores the key-value pairs
     *  in the dictionary. */
    private class BSTNode<K extends Comparable> {

        /** Stores the key of the key-value pair of this node in the list. */
        K key;
        /** Stores the value of the key-value pair of this node in the list. */
        V val;
        /** Stores the left node of the current node*/
        BSTNode left = null;
        /** Store the right node of the current node*/
        BSTNode right = null;

        BSTNode(K k, V v, BSTNode left, BSTNode right) {
            this.key = k;
            this.val = v;
            this.left = left;
            this.right = right;
        }

        BSTNode get(K k) {
            if (k == null) {
                return null;
            }
            if (k.equals(key)) {
                return this;
            }
            if (k.compareTo(this.key) < 0) {
                if (this.left != null) {
                    return this.left.get(k);
                } else {
                    return this;
                }
            }
            if (k.compareTo(this.key) > 0) {
                if (this.right != null) {
                    return this.right.get(k);
                } else {
                    return this;
                }
            }
            return null;
        }
    }
}
