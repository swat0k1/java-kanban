package manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.*;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int idCounter = 0;

    public static int getIDCounter() {
        return idCounter;
    }

    public static void incrementIDCounter() {
        idCounter++;
    }


    @Override
    public void createTask(T task) {

        if (task.getType().equals(TaskType.EPIC)) {
            epicTasks.put(task.getId(), (EpicTask) task);
            System.out.println("Создан эпик. id: " + task.getId());
        } else if (task.getType().equals(TaskType.SUBTASK)) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) getTaskWithoutRecord(subTask.getEpicTask(this).getId());
            epicTask.addSubTask(task.getId());
            subTasks.put(task.getId(), (SubTask) task);
            System.out.println("Создана сабтаска. id: " + task.getId());
        } else if (task.getType().equals(TaskType.TASK)) {
            tasks.put(task.getId(), task);
            System.out.println("Создана таска. id: " + task.getId());
        } else {
            System.out.println("Ошибка при создании таски!");
        }

    }

    @Override
    public void updateTask(T task, int id) {

        if (task.getType().equals(TaskType.EPIC)) {
            if (!isEpicContainsID(id)) {
                System.out.println("Эпик с указанным id не был найден");
                return;
            }
            epicTasks.put(id, (EpicTask) task);
        } else if (task.getType().equals(TaskType.SUBTASK)) {
            if (!isSubTaskContainsID(id)) {
                System.out.println("Сабтаска с указанным id не была найдена");
                return;
            }
            subTasks.put(id, (SubTask) task);
        } else if (task.getType().equals(TaskType.TASK)) {
            if (!isTaskContainsID(id)) {
                System.out.println("Таска с указанным id не была найдена");
                return;
            }
            tasks.put(id, task);
        } else {
            System.out.println("Ошибка при обновлении таски!");
        }

    }

    @Override
    public T getTask(int id) {

        if (isTaskContainsID(id)) {
            T task = (T) tasks.get(id);
                inMemoryHistoryManager.add(task);
            return task;
        } else if (isEpicContainsID(id)) {
            T task = (T) epicTasks.get(id);
                inMemoryHistoryManager.add(task);
            return task;
        } else if (isSubTaskContainsID(id)) {
            T task = (T) subTasks.get(id);
                inMemoryHistoryManager.add(task);
            return task;
        }

        System.out.println("Ошибка! Таска с указанным id не найдена!");
        return null;

    }

    public T getTaskWithoutRecord(int id) {

        if (isTaskContainsID(id)) {
            T task = (T) tasks.get(id);
            return task;
        } else if (isEpicContainsID(id)) {
            T task = (T) epicTasks.get(id);
            return task;
        } else if (isSubTaskContainsID(id)) {
            T task = (T) subTasks.get(id);
            return task;
        }

        System.out.println("Ошибка! Таска с указанным id не найдена!");
        return null;

    }

    @Override
    public ArrayList<T> getTaskList(TaskType taskType) {

        ArrayList<T> taskArrayList = new ArrayList<>();

        if (taskType.equals(TaskType.TASK)) {
            taskArrayList.addAll((Collection<? extends T>) tasks.values());
            return taskArrayList;
        } else if (taskType.equals(TaskType.EPIC)) {
            taskArrayList.addAll((Collection<? extends T>) epicTasks.values());
            return taskArrayList;
        } else if (taskType.equals(TaskType.SUBTASK)) {
            taskArrayList.addAll((Collection<? extends T>) subTasks.values());
            return taskArrayList;
        }

        System.out.println("Ошибка! Тип таски указан не верно!");
        return null;

    }

    @Override
    public void removeTask(int id) {

        if (isTaskContainsID(id)) {
            T task = (T) tasks.get(id);
            inMemoryHistoryManager.remove(task.getId());
            tasks.remove(id);

        } else if (isEpicContainsID(id)) {
            EpicTask epicTask = epicTasks.get(id);
            ArrayList<Integer> subTaskArrayList = epicTask.getSubTasks();
            for (Integer subTaskID : subTaskArrayList) {
                if (isSubTaskContainsID(subTaskID)) {
                    T task = (T) subTasks.get(subTaskID);
                    inMemoryHistoryManager.remove(task.getId());
                    subTasks.remove(subTaskID);
                }
            }
            epicTask.clearSubTasksList();
            T task = (T) epicTasks.get(id);
            inMemoryHistoryManager.remove(task.getId());
            epicTasks.remove(epicTask.getId());

        } else if (isSubTaskContainsID(id)) {
            SubTask subTask = subTasks.get(id);
            EpicTask epicTask = epicTasks.get(subTask.getEpicTask(this).getId());

            epicTask.removeSubTask(subTask.getId());
            T task = (T) subTasks.get(id);
            inMemoryHistoryManager.remove(task.getId());
            subTasks.remove(subTask.getId());
            epicTask.updateEpicTaskStatus(this);

        } else {
            System.out.println();
        }

    }

    @Override
    public void removeAllTasks(TaskType taskType) {

        if (taskType.equals(TaskType.TASK)) {
            tasks.clear();
        } else if (taskType.equals(TaskType.EPIC)) {
            epicTasks.clear();
        } else if (taskType.equals(TaskType.SUBTASK)) {
            subTasks.clear();
        } else {
            System.out.println("Ошибка! Тип таски указан не верно!");
        }

    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    // Выводит полное описание всех тасков
    public void printTasksDescriptionFull() {
        for (Task task : tasks.values()) {
            System.out.println(task.toString());
        }
        for (EpicTask task : epicTasks.values()) {
            System.out.println(task.toString());
        }
        for (SubTask task : subTasks.values()) {
            System.out.println(task.toString());
        }
    }

    // Выводит описание всех тасков
    public void printTasksNames() {
        for (Task task : tasks.values()) {
            System.out.println(task.getSimpleDescription());
        }
        for (EpicTask task : epicTasks.values()) {
            System.out.println(task.getSimpleDescription());
        }
        for (SubTask task : subTasks.values()) {
            System.out.println(task.getSimpleDescription());
        }
    }

    public ArrayList<Integer> getEpicsSubtasksList(EpicTask epicTask) {
        return epicTask.getSubTasks();
    }

    public ArrayList<Integer> getEpicsSubtasksList(int id) {
        if (!isEpicContainsID(id)) {
            System.out.println("Эпик с указанным id не был найден");
            return new ArrayList<>();
        }
        EpicTask epicTask = epicTasks.get(id);
        return epicTask.getSubTasks();
    }

    public boolean isTaskContainsID(int id) {
        return tasks.containsKey(id);
    }

    public boolean isEpicContainsID(int id) {
        return epicTasks.containsKey(id);
    }

    public boolean isSubTaskContainsID(int id) {
        return subTasks.containsKey(id);
    }

    public HashMap<Integer, EpicTask> getEpicTaskHashMap() {
        return epicTasks;
    }

    public int getSubTasksSize() {
        return subTasks.size();
    }

    // Проверяет есть ли эпики
    public boolean isEpicTaskAvailable() {
        return !epicTasks.isEmpty();
    }
}
