package interfaces;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public ArrayList<Task> getHistory();

    public void add(Task task);

    public void remove(int id);

}
