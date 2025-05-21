package model;

import interfaces.HistoryManager;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public ArrayList<Task> getHistory() {
        return viewHistory;
    }

    private void addHistory(Task task) {

        if (viewHistory.size() == 10) {
            viewHistory.removeFirst();
            viewHistory.add(task);
        } else {
            viewHistory.add(task);
        }
    }

    private void removeHistory(Task task) {
        viewHistory.remove(task);
    }

}
