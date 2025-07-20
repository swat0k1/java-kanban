package manager;

import java.time.LocalDateTime;
import java.util.*;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.*;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private TreeSet<Task> sortedTasks = new TreeSet<>();
    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int idCounter = 0;

    public static int getIDCounter() {
        return idCounter;
    }

    public static void incrementIDCounter() {
        idCounter++;
    }

    public static void setIDCounter(int id) {
        InMemoryTaskManager.idCounter = id;
    }

    @Override
    public int createTask(T task) {

        if (taskOverlapAnyOtherTask(task)) {
            System.out.println("Задача не может быть добавлена в менеджер задач: пересечение времени!");
            return -2;
        }

        if (task.getType().equals(TaskType.EPIC)) {
            epicTasks.put(task.getId(), (EpicTask) task);
            task.setInMemoryTaskManager(this);
            System.out.println("Создан эпик. id: " + task.getId());
            return 1;

        } else if (task.getType().equals(TaskType.SUBTASK)) {
            SubTask subTask = (SubTask) task;
            task.setInMemoryTaskManager(this);
            subTasks.put(task.getId(), subTask);
            if (!subTask.getStartTime().equals(LocalDateTime.MIN)) {
                sortedTasks.add(subTask);
            }
            try {
                EpicTask epicTask = (EpicTask) getTaskWithoutRecord(subTask.getEpicTask().getId());
                epicTask.addSubTask(task.getId());
                System.out.println("Создана сабтаска. id: " + task.getId());
            } catch (NullPointerException exception) {
                System.out.println("Эпик отсутствует");
                return -1;
            }

            return 1;

        } else if (task.getType().equals(TaskType.TASK)) {
            tasks.put(task.getId(), task);
            task.setInMemoryTaskManager(this);
            if (!task.getStartTime().equals(LocalDateTime.MIN)) {
                sortedTasks.add(task);
            }
            System.out.println("Создана таска. id: " + task.getId());
            return 1;

        } else {
            System.out.println("Ошибка при создании таски!");
            return -1;
        }

    }

    @Override
    public int updateTask(T task, int id) {

        if (taskOverlapAnyOtherTask(task)) {
            System.out.println("Задача не может быть обновлена в менеджере задач: пересечение времени!");
            return -2;
        }

        if (task.getType().equals(TaskType.EPIC)) {
            if (!isEpicContainsID(id)) {
                System.out.println("Эпик с указанным id не был найден");
                return -3;
            }
            epicTasks.put(id, (EpicTask) task);
            return 1;
        } else if (task.getType().equals(TaskType.SUBTASK)) {
            if (!isSubTaskContainsID(id)) {
                System.out.println("Сабтаска с указанным id не была найдена");
                return -3;
            }
            subTasks.put(id, (SubTask) task);
            return 1;
        } else if (task.getType().equals(TaskType.TASK)) {
            if (!isTaskContainsID(id)) {
                System.out.println("Таска с указанным id не была найдена");
                return -3;
            }
            tasks.put(id, task);
            return 1;
        } else {
            System.out.println("Ошибка при обновлении таски!");
            return -1;
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

    public SubTask getSubTaskWithoutRecord(int id) {

        if (isSubTaskContainsID(id)) {
            return subTasks.get(id);
        }

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

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    public boolean tasksOverlapAnotherTask(Task task1, Task task2) {

        if (task1 instanceof EpicTask || task2 instanceof EpicTask) {
            return false;
        }

        if (task1.getId() == task2.getId()) {
            return false;
        }

        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        if ((startTime1.equals(LocalDateTime.MIN) && endTime1.equals(LocalDateTime.MIN)) ||
                (startTime2.equals(LocalDateTime.MIN) && endTime2.equals(LocalDateTime.MIN))) {
            return false;
        } else {
            return (startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1));
        }

    }

    public boolean taskOverlapAnyOtherTask(Task task) {

        if (task instanceof EpicTask) {
            return false;
        }

        return sortedTasks
                .stream()
                .filter(element -> !(element instanceof EpicTask))
                .anyMatch(element -> tasksOverlapAnotherTask(task, element));
    }

    @Override
    public void removeTask(int id) {

        if (isTaskContainsID(id)) {
            T task = (T) tasks.get(id);
            if (inMemoryHistoryManager.getNodeMap().containsKey(task.getId())) {
                inMemoryHistoryManager.remove(task.getId());
            }
            tasks.remove(id);
            if (sortedTasks.contains(task)) {
                sortedTasks.remove(task);
            }

        } else if (isEpicContainsID(id)) {
            EpicTask epicTask = epicTasks.get(id);
            ArrayList<Integer> subTaskArrayList = epicTask.getSubTasks();
            for (Integer subTaskID : subTaskArrayList) {
                if (isSubTaskContainsID(subTaskID)) {
                    T task = (T) subTasks.get(subTaskID);
                    if (inMemoryHistoryManager.getNodeMap().containsKey(task.getId())) {
                        inMemoryHistoryManager.remove(task.getId());
                    }
                    subTasks.remove(subTaskID);
                    if (sortedTasks.contains(task)) {
                        sortedTasks.remove(task);
                    }
                }
            }
            epicTask.clearSubTasksList();
            T task = (T) epicTasks.get(id);
            if (inMemoryHistoryManager.getNodeMap().containsKey(task.getId())) {
                inMemoryHistoryManager.remove(task.getId());
            }
            epicTasks.remove(epicTask.getId());
            if (sortedTasks.contains(task)) {
                sortedTasks.remove(task);
            }

        } else if (isSubTaskContainsID(id)) {
            SubTask subTask = subTasks.get(id);
            EpicTask epicTask = epicTasks.get(subTask.getEpicTask().getId());

            epicTask.removeSubTask(subTask.getId());
            T task = (T) subTasks.get(id);
            if (inMemoryHistoryManager.getNodeMap().containsKey(task.getId())) {
                inMemoryHistoryManager.remove(task.getId());
            }
            subTasks.remove(subTask.getId());
            if (sortedTasks.contains(task)) {
                sortedTasks.remove(task);
            }
            epicTask.updateEpicTaskStatus(this);

        }
    }

    @Override
    public void removeAllTasks(TaskType taskType) {

        if (taskType.equals(TaskType.TASK)) {
            tasks.clear();
            clearSortedListByTaskType(taskType);
        } else if (taskType.equals(TaskType.EPIC)) {
            epicTasks.clear();
            clearSortedListByTaskType(taskType);
        } else if (taskType.equals(TaskType.SUBTASK)) {
            subTasks.clear();
            clearSortedListByTaskType(taskType);
        } else {
            System.out.println("Ошибка! Тип таски указан не верно!");
        }

    }

    private void clearSortedListByTaskType(TaskType taskType) {
        sortedTasks.removeIf(task -> task.getType().equals(taskType));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
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

}
