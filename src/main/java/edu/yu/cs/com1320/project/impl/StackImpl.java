package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    class Node{
        public T data;
        public Node(T data){ this.data = data;}
        public Node next;

    }

    private Node head;

    public StackImpl(){}

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element){
        if(element == null) return;
        Node n = new Node(element);
        if(head == null) head = n;
        else{n.next = head;
        head = n;}
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop(){
        if(head == null) return null;
        Node popping = head;
        head = head.next;
        return popping.data;
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */

    @Override
    public T peek(){
        if(head == null) return null;
        return head.data;
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size(){
        if(head == null) return 0;
        Node current = head;
        int size = 1;
        while(current.next != null){
            size++;
            current = current.next;
        }
        return size;
    }
}
