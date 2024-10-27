package deque;

public class LinkedListDeque<T> {

    public static class LinkedList<T>{
        public T item;
        public LinkedList prev;
        public LinkedList next;

        LinkedList(T obj, LinkedList pre, LinkedList nex){
            item = obj;
            prev = pre;
            next = nex;
        }
    }

    private int size;
    private LinkedList<T> sentinel;

    public LinkedListDeque(){
        size = 0;
        sentinel = new LinkedList(114514, null, null);
    }
    public LinkedListDeque(T item){
        size = 1;
        LinkedList<T> FirstNode= new LinkedList<>(item, null, null);
        FirstNode.next = FirstNode;
        FirstNode.prev = FirstNode;
        sentinel = new LinkedList(114514, null, FirstNode);
    }

    public void addFirst(T item){
        size += 1;
        LinkedList<T> Original_First = sentinel.next;
        //assume that there is nothing in the linkedlist.
        LinkedList<T> NewFirst = new LinkedList<>(item, null, null);
        NewFirst.prev = NewFirst;
        NewFirst.next = NewFirst;
        sentinel.next = NewFirst;
        //if there is something in the linkedlist, then fix it.
        if (Original_First != null) {
            LinkedList<T> Last = sentinel.next.prev;
            NewFirst.prev = Last;
            NewFirst.next = Original_First;
            Last.next = NewFirst;
            Original_First.prev = NewFirst;
        }
    }

    public void addLast(T item){
        size += 1;
        LinkedList<T> First = sentinel.next;
        //assume that there is nothing in the linkedlist.
        LinkedList<T> NewLast = new LinkedList<>(item, null, null);
        NewLast.prev = NewLast;
        NewLast.next = NewLast;
        sentinel.next = NewLast;
        //if there is something in the linkedlist, then fix it.
        //if first is not null, then original_last can not be null.
        if (First != null){
            LinkedList<T> Original_Last = First.prev;
            NewLast.next = First;
            NewLast.prev = Original_Last;
            sentinel.next = First;
            First.prev = NewLast;
            Original_Last.next = NewLast;
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        if (size() == 0){
            return true;
        }
        return false;
    }

    public void printDeque(){
        if (isEmpty()){
            return ;
        }
        LinkedList<T> First = sentinel.next;
        LinkedList<T> p = First;
        System.out.print(p.item);
        p = p.next;
        while (p != First){
            System.out.print(" " + p.item);
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        size -= 1;
        LinkedList<T> First = sentinel.next;
        LinkedList<T> NewFirst = sentinel.next.next;
        LinkedList<T> Last = sentinel.next.prev;
        T ret_item = First.item;
        NewFirst.prev = Last;
        Last.next = NewFirst;
        sentinel.next = NewFirst;
        First = null;
        return ret_item;
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        size -= 1;
        LinkedList<T> First = sentinel.next;
        LinkedList<T> Last = sentinel.next.prev;
        LinkedList<T> NewLast = sentinel.next.prev.prev;
        T ret_item = Last.item;
        First.prev = NewLast;
        NewLast.next = First;
        Last = null;
        return ret_item;
    }

    public T get(int index){
        if (index > size || isEmpty()){
            return null;
        }
        LinkedList<T> p = sentinel.next;
        while (index != 0){
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    private T helper_get(int index, LinkedList<T> p){
        if (index == 0){
            return p.item;
        }
        LinkedList<T> np = p.next;
        return helper_get(index-1, np);
    }

    public T getRecursive(int index){
        if(index > size || isEmpty()){
            return null;
        }
        LinkedList<T> p = sentinel.next;
        return helper_get(index, p);
    }
}
