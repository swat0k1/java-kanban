package manager;

import interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> viewHistory = new ArrayList<>(10);

    @Override
    public ArrayList<Task> getHistory() {
        return viewHistory;
    }

    @Override
    public void addHistory(Task task) {

        if (viewHistory.size() == 10) {
            viewHistory.removeFirst();
            viewHistory.add(task);
        } else {
            viewHistory.add(task);
        }
    }

    @Override
    public void removeHistory(Task task) {
        viewHistory.remove(task);
    }

}
