package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private static class LinkedList<T> {
        private T item;
        private LinkedList prev;
        private LinkedList next;

        LinkedList(T obj, LinkedList pre, LinkedList nex) {
            item = obj;
            prev = pre;
            next = nex;
        }
    }

    private int size;
    private LinkedList<T> sentinel;

    public LinkedListDeque() {
        size = 0;
        sentinel = new LinkedList(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }
    public LinkedListDeque(T item) {
        size = 1;
        sentinel = new LinkedList(null, null, null);
        LinkedList<T> firstNode = new LinkedList<>(item, sentinel, sentinel);
        sentinel.next = firstNode;
        sentinel.prev = firstNode;
    }

    public void addFirst(T item) {
        size += 1;
        LinkedList<T> originalFirst = sentinel.next;
        LinkedList<T> newFirst = new LinkedList<>(item, sentinel, originalFirst);
        sentinel.next = newFirst;
        originalFirst.prev = newFirst;
    }

    public void addLast(T item){
        size += 1;
        LinkedList<T> originalLast = sentinel.prev;
        LinkedList<T> newLast = new LinkedList<>(item, originalLast, sentinel);
        sentinel.prev = newLast;
        originalLast.next = newLast;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (isEmpty()){
            return;
        }
        LinkedList<T> p = sentinel.next;
        System.out.print(p.item);
        p = p.next;
        while (p != sentinel){
            System.out.print(" " + p.item);
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        LinkedList<T> First = sentinel.next;
        LinkedList<T> newFirst = sentinel.next.next;
        sentinel.next = newFirst;
        newFirst.prev = sentinel;
        return First.item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        LinkedList<T> Last = sentinel.prev;
        LinkedList<T> newLast = sentinel.prev.prev;
        sentinel.prev = newLast;
        newLast.next = sentinel;
        return Last.item;
    }

    public T get(int index) {
        if (index > size || isEmpty()) {
            return null;
        }
        LinkedList<T> p = sentinel.next;
        while (index != 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    private T helperGet(int index, LinkedList<T> p) {
        if (index == 0) {
            return p.item;
        }
        LinkedList<T> np = p.next;
        return helperGet(index - 1, np);
    }

    public T getRecursive(int index) {
        if(index > size || isEmpty()) {
            return null;
        }
        LinkedList<T> p = sentinel.next;
        return helperGet(index, p);
    }

    public Iterator<T> iterator() {
        return new LinkListSetIterator();
    }

    private class LinkListSetIterator implements Iterator<T> {
        private int newIndex;
        public LinkListSetIterator() {
            newIndex = 0;
        }
        public boolean hasNext() {
            return size > newIndex;
        }

        public T next() {
            T returnItem = get(newIndex);
            newIndex += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o instanceof LinkedListDeque) {
            LinkedListDeque oo = (LinkedListDeque) o;
            if (oo.size() != size) {
                return false;
            }
            int i=0;
            LinkedList<T> p = sentinel.next;
            while (p != sentinel) {
                if (!p.item.equals(oo.get(i))) {
                    return false;
                }
                i += 1;
                p = p.next;
            }
            return true;
        }
        if (o instanceof ArrayDeque) {
            ArrayDeque oo = (ArrayDeque) o;
            if (oo.size() != size) {
                return false;
            }
            int i=0;
            LinkedList<T> p = sentinel.next;
            while (p != sentinel) {
                if (!p.item.equals(oo.get(i))) {
                    return false;
                }
                i += 1;
                p = p.next;
            }
            return true;
        }
        return true;
    }
}
