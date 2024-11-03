package deque;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    private class largerValue implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private class lessValue implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }

    private class largerName implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
    @Test
    public void largerIntTest(){
        largerValue cmp1 = new largerValue();
        MaxArrayDeque testMax = new MaxArrayDeque(cmp1);
        assertEquals(testMax.max(), null);
        for (int i=0; i<10; i++) {
            testMax.addFirst(i);
        }
        assertEquals(testMax.max(), 9);
    }

    @Test
    public void largerNameTest(){
        largerName cmp1 = new largerName();
        MaxArrayDeque testMax = new MaxArrayDeque(cmp1);
        testMax.addFirst("abandon");
        testMax.addFirst("addFirst");
        testMax.addFirst("testMax");
        testMax.addFirst("MaxArrayDeque");
        assertEquals(testMax.max(), "testMax");
    }
}
