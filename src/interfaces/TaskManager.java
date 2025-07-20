package interfaces;
import model.EpicTask;
import model.Task;
import model.TaskType;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager<T extends Task> {

    public int createTask(T task);

    public int updateTask(T task, int id);

    public T getTask(int id);

    public ArrayList<T> getTaskList(TaskType taskType);

    public void removeTask(int id);

    public void removeAllTasks(TaskType taskType);

    public ArrayList<Task> getHistory();

    public boolean isTaskContainsID(int id);

    public boolean isEpicContainsID(int id);

    public boolean isSubTaskContainsID(int id);

    public HashMap<Integer, EpicTask> getEpicTaskHashMap();

    public ArrayList<Task> getPrioritizedTasks();

    public boolean tasksOverlapAnotherTask(Task task1, Task task2);
}
