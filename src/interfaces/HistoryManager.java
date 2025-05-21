package interfaces;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public ArrayList<Task> getHistory();

    public void addHistory(Task task);

    public void removeHistory(Task task);

}
