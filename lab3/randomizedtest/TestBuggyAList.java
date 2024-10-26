package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import net.sf.saxon.om.Item;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> right_test = new AListNoResizing<>();
        BuggyAList<Integer> wrong_test = new BuggyAList<>();
        right_test.addLast(4);
        wrong_test.addLast(4);
        right_test.addLast(5);
        wrong_test.addLast(5);
        right_test.addLast(6);
        wrong_test.addLast(6);
        assertEquals(right_test.size(), wrong_test.size());
        assertEquals(right_test.removeLast(), wrong_test.removeLast());
        assertEquals(right_test.removeLast(), wrong_test.removeLast());
        assertEquals(right_test.removeLast(), wrong_test.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1){
                //size
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2 & L.size() > 0 & B.size() > 0) {
                // getLast
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 3 & L.size() > 0 & B.size() > 0) {
                //removeLast
                assertEquals(L.removeLast(), B.removeLast());
            }
        }
    }

}
