package manager;

import model.Node;
import model.Task;

import java.util.ArrayList;

class DoubleLinkedList {

    DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private Node head;
    private Node tail;
    private int size;

    public void linkLast(Node newNode) {
        if (head == null) {
            head = newNode;
        } else if (tail == null) {
            tail = newNode;
            tail.setPrev(head);
            head.setNext(tail);
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
    }

    public void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrev(prev);
        }
        if (node.equals(head)) {
            head = null;
        } else if (node.equals(tail)) {
            tail = tail.getPrev();
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>(size);
        if (head != null && tail == null) {
            tasks.add(head.getTask());
            return tasks;
        }
        Node current = tail;
        while (current != null) {
            tasks.add(current.getTask());
            current = current.getPrev();
        }
        return tasks;
    }

}
