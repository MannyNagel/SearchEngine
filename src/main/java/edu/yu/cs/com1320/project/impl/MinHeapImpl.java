package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;


//when an element is updated
public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
    
    protected int count = 0;



    public MinHeapImpl() {
        elements = (E[]) new Comparable[8];
    }

    private boolean less(E e1, E e2) {
        if (e1.compareTo(e2) < 0) return true;
        return false;
    }

    private int parent(int k) {
        return k / 2;
    }

    private int childL(int k) {
        return 2 * k;
    }

    private int childR(int k) {
        return 2 * k + 1;
    }


    private boolean greaterThanEitherChild(int e) {
        //determine if we need to downheap
        int length = elements.length;
        if (childL(e) >= length && childR(e) > length) return false;
        if (elements[childL(e)] == null && elements[childR(e)] == null) return false;
        else if (childL(e) < length && elements[childL(e)] != null && less(elements[childL(e)], elements[e])) return true;
        else if (childR(e) < length && elements[childR(e)] != null && less(elements[childR(e)], elements[e])) return true;
        return false;
    }

    //TODO finish this class
    public void reHeapify(E element) {
        int updatedE = 0;
        try{
            updatedE = getArrayIndex(element);
        }
        catch(NoSuchElementException e){
            insert(element);
            updatedE = getArrayIndex(element);
        }

        if (greaterThanEitherChild(updatedE)) {
//            System.out.println("downheap");
            downHeap(updatedE);
        } else {
            //if updatedE shouldn't have been downheaped then the object will be in the same place to upHeap
//            System.out.println("upheaping");
            upHeap(updatedE);
        }
    }

    protected int getArrayIndex(E element) {
        if(element == null) throw new NoSuchElementException("Element is null");
        for (int i = 1; i < elements.length && elements[i] != null; i++) {
            if (elements[i].equals(element)) return i;
        }
        throw new NoSuchElementException("Element is not in heap");
    }

    protected void doubleArraySize() {
        E[] newHeap = (E[]) new Comparable[elements.length * 2];
        for (int i = 1; i < elements.length; i++) {
            newHeap[i] = elements[i];
        }
        elements = newHeap;
    }
}
