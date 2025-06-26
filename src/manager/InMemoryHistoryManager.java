package manager;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class InMemoryHistoryManager implements HistoryManager {

    private HashMap<Integer, Node> nodeMap = new HashMap<>();
    private DoubleLinkedList nodeList = new DoubleLinkedList();

    @Override
    public ArrayList<Task> getHistory() {
        return nodeList.getTasks();
    }

    @Override
    public void add(Task task) {
        Integer taskId = task.getId();

        if (nodeMap.containsKey(taskId)) {
            Node curr = nodeMap.get(taskId);
            nodeList.removeNode(curr);
            Node newNode = new Node(task);
            nodeList.linkLast(newNode);
            nodeMap.put(taskId, newNode);
        } else {
            Node newNode = new Node(task);
            nodeMap.put(taskId, newNode);
            nodeList.linkLast(newNode);
        }
    }

    @Override
    public void remove(int taskId) {
        if (!nodeMap.containsKey(taskId)) {
            throw new NoSuchElementException();
        }

        Node curr = nodeMap.get(taskId);
        nodeMap.remove(taskId);
        nodeList.removeNode(curr);
    }

}

