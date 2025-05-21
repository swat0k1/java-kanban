package manager;

import java.util.ArrayList;
import java.util.HashMap;
import model.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private static int idCounter = 0;

    public static int getIDCounter() {
        return idCounter;
    }

    public static void incrementIDCounter() {
        idCounter++;
    }

    /* ***********************************************************************************************************
    Область методов для работы с тасками
     */
    public void createTask(String taskName, String taskDescription) {
        Task task = new Task(taskName, taskDescription);
        tasks.put(task.getId(), task);
        System.out.println("Создана таска. id: " + task.getId());
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Создана таска. id: " + task.getId());
    }

    public void updateTask(Task task, int id) {
        if (!isTaskContainsID(id)) {
            System.out.println("Таска с указанным id не была найдена");
            return;
        }

        tasks.put(id, task);
    }

    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        if (isTaskContainsID(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public void removeTaskByID(int id) {
        if (!isTaskContainsID(id)) {
            System.out.println("Таска с указанным id не была найдена");
            return;
        }
        tasks.remove(id);
    }

    /* ***********************************************************************************************************
    Область методов для работы с эпиками
     */
    public void createEpicTask(String taskName, String taskDescription) {
        EpicTask epicTask = new EpicTask(taskName, taskDescription, TaskType.EPIC);
        epicTasks.put(epicTask.getId(), epicTask);
        System.out.println("Создан эпик. id: " + epicTask.getId());
    }

    public void createEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
        System.out.println("Создан эпик. id: " + epicTask.getId());
    }

    public void updateEpicTask(EpicTask epicTask, int id) {
        if (!isEpicContainsID(id)) {
            System.out.println("Эпик с указанным id не был найден");
            return;
        }

        epicTasks.put(id, epicTask);
    }

    public ArrayList<Task> getEpicTaskList() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : epicTasks.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public void removeAllEpicTasks() {
        epicTasks.clear();
    }

    public EpicTask getEpicTask(int id) {
        if (isEpicContainsID(id)) {
            return epicTasks.get(id);
        }
        return null;
    }

    public void removeEpicTaskByID(int id) {
        if (!isEpicContainsID(id)) {
            System.out.println("Эпик с указанным id не был найден");
            return;
        }

        EpicTask epicTask = epicTasks.get(id);
        ArrayList<Integer> subTaskArrayList = epicTask.getSubTasks();
        for (Integer subTaskID : subTaskArrayList) {
            if (isSubTaskContainsID(subTaskID)) {
                subTasks.remove(subTaskID);
            }
        }

        epicTask.clearSubTasksList();
        epicTasks.remove(epicTask.getId());
    }

    /* ***********************************************************************************************************
    Область методов для работы с сабтасками
     */
    public void createSubTask(String taskName, String taskDescription, int epicTaskID) {
        SubTask subTask = new SubTask(taskName, taskDescription, TaskType.SUBTASK, epicTaskID);
        getEpicTask(epicTaskID).addSubTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        System.out.println("Создана сабтаска. id: " + subTask.getId());
    }

    public void createSubTask(SubTask subTask) {
        EpicTask epicTask = getEpicTask(subTask.getEpicTask(this).getId());
        epicTask.addSubTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        System.out.println("Создана сабтаска. id: " + subTask.getId());
    }

    public void updateSubTask(SubTask subTask, int id) {
        if (!isSubTaskContainsID(id)) {
            System.out.println("Сабтаска с указанным id не была найдена");
            return;
        }

        subTasks.put(id, subTask);
    }

    public ArrayList<Task> getSubTaskList() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : subTasks.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public SubTask getSubTask(int id) {
        if (isSubTaskContainsID(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public void removeSubTasksByID(int id) {
        if (!isSubTaskContainsID(id)) {
            System.out.println("Сабтаска с указанным id не была найдена");
            return;
        }

        SubTask subTask = subTasks.get(id);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTask(this).getId());

        epicTask.removeSubTask(subTask.getId());
        subTasks.remove(subTask.getId());
        epicTask.updateEpicTaskStatus(this);
    }

    /* ***********************************************************************************************************
    Область с дополнительными методами
     */

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

    // Проверяет есть ли эпики
    public boolean isEpicTaskAvailable() {
        return !epicTasks.isEmpty();
    }
}
