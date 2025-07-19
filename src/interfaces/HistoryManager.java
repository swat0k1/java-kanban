package interfaces;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface HistoryManager {

    public ArrayList<Task> getHistory();

    public HashMap<Integer, Node> getNodeMap();

    public void add(Task task);

    public void remove(int id);

}
