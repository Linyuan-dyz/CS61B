package deque;

public class ArrayDeque<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;
    /*in this array,
    the index point to the first place should be nextFirst + 1,
    also, when index is out of array's length, you should sub index by items.length
    to find the correct place.
     */

    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    //when you addFirst, the nextFirst needs to decrease.
    public void addFirst(T item) {
        // make sure new array is large enough to contain the new item.
        size += 1;
        if (size == items.length) {
            reSize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        if (nextFirst == -1) {
            nextFirst = items.length - 1;
        }
    }

    //when you addLast, the nextLast needs to increase.
    public void addLast(T item) {
        size += 1;
        if (size == items.length) {
            reSize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast += 1;
        if (nextLast == items.length) {
            nextLast = 0;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        return false;
    }

    private void reSize(int length) {
        int p = nextFirst + 1;
        if (p == items.length) {
            p = 0;
        }
        T[] a = (T []) new Object[length];
        int newPointer = 1;
        while (p != nextLast) {
            a[newPointer] = items[p];
            newPointer += 1;
            p += 1;
            if (newPointer == length) {
                newPointer = 0;
            }
            if (p == items.length) {
                p = 0;
            }
        }
        nextFirst = 0;
        nextLast = newPointer;
        items = a;
    }

    public void printDeque() {
        if (isEmpty()) {
            return;
        }
        int p = nextFirst + 1;
        System.out.print(items[p]);
        p += 1;
        while (p != nextLast) {
            if (p >= items.length) {
                p -= items.length;
            }
            System.out.print(" " + items[p]);
            p += 1;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        if (items.length >= 16 && size < items.length/4) {
            reSize(items.length / 2);
        }
        nextFirst += 1;
        if (nextFirst == items.length) {
            nextFirst = 0;
        }
        T retItem = items[nextFirst];
        items[nextFirst] = null;
        return retItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (items.length >= 16 && size < items.length/4) {
            reSize(items.length / 2);
        }
        size -= 1;
        nextLast -= 1;
        if (nextLast == -1) {
            nextLast = items.length - 1;
        }
        T retItem = items[nextLast];
        items[nextLast] = null;
        return retItem;
    }

    public T get(int index) {
        if (index > size || isEmpty()) {
            return null;
        }
        int p = nextFirst + 1 + index;
        if (p >= items.length) {
            p -= items.length;
        }
        return items[p];
    }
}
