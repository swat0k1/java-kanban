package manager;

import interfaces.TaskManager;
import interfaces.HistoryManager;

import java.io.File;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public FileBackedTaskManager getBacked(File file) {
        return new FileBackedTaskManager(file);
    }

    public FileBackedTaskManager getBackedFromFile(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

}
