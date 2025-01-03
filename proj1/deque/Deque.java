package deque;
import java.util.Iterator;

public interface Deque<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);
    default public boolean isEmpty() {
        if (size() == 0) {
            return true;
        }
        return false;
    }
    public boolean equals(Object o);
    public Iterator<T> iterator();
}
