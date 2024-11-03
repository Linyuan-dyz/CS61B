package deque;
import  java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque{
    private Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        int i = 1;
        T returnMax = (T) get(0);
        while (i != size()) {
            T currVal = (T)get(i);
            if (cmp.compare(returnMax, currVal) < 0) {
                returnMax = currVal;
            }
            i += 1;
        }
        return returnMax;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        int i = 1;
        T returnMax = (T) get(0);
        while (i != size()) {
            T currVal = (T)get(i);
            if (c.compare(returnMax, currVal) < 0) {
                returnMax = currVal;
            }
            i += 1;
        }
        return returnMax;
    }
}
