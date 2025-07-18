package model;

public class Node {

    private Task task;
    private Node prev = null;
    private Node next = null;

    public Node(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }

    public Node getPrev() {
        return this.prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return this.next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

}
