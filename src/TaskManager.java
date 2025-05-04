import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<String, Task> tasks = new HashMap<>();
    private final HashMap<String, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<String, Subtask> subtasks = new HashMap<>();
    private static int uuidCounter = 0;

    public static int getUUIDCounter() {
        return uuidCounter;
    }

    public static void incrementUUIDCounter() {
        uuidCounter++;
    }

    /* ***********************************************************************************************************
    Область методов для работы с тасками
     */
    public void createTask(String taskName, String taskDescription) {
        Task task = new Task(taskName, taskDescription);
        tasks.put(task.getTaskUUID(), task);
        System.out.println("Создана таска. uuid: " + task.getTaskUUID());
    }

    public void createTask(Task task) {
        tasks.put(task.getTaskUUID(), task);
        System.out.println("Создана таска. uuid: " + task.getTaskUUID());
    }

    public void updateTask(Task task, int uuid) {
        if (!isTaskContainsUUID(uuid)) {
            System.out.println("Таска с указанным uuid не была найдена");
            return;
        }

        tasks.put(String.valueOf(uuid), task);
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

    public Task getTask(int uuid) {
        if (isTaskContainsUUID(uuid)) {
            return tasks.get(String.valueOf(uuid));
        }
        return null;
    }

    public void removeTaskByUUID(int uuid) {
        if (!isTaskContainsUUID(uuid)) {
            System.out.println("Таска с указанным uuid не была найдена");
            return;
        }
        tasks.remove(String.valueOf(uuid));
    }

    /* ***********************************************************************************************************
    Область методов для работы с эпиками
     */
    public void createEpicTask(String taskName, String taskDescription) {
        EpicTask epicTask = new EpicTask(taskName, taskDescription, TaskType.EPIC);
        epicTasks.put(epicTask.getTaskUUID(), epicTask);
        System.out.println("Создан эпик. uuid: " + epicTask.getTaskUUID());
    }

    public void createEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getTaskUUID(), epicTask);
        System.out.println("Создан эпик. uuid: " + epicTask.getTaskUUID());
    }

    public void updateEpicTask(EpicTask epicTask, int uuid) {
        if (!isEpicContainsUUID(uuid)) {
            System.out.println("Эпик с указанным uuid не был найден");
            return;
        }

        epicTasks.put(String.valueOf(uuid), epicTask);
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

    public EpicTask getEpicTask(int uuid) {
        if (isEpicContainsUUID(uuid)) {
            return epicTasks.get(String.valueOf(uuid));
        }
        return null;
    }

    public void removeEpicTaskByUUID(int uuid) {
        if (!isEpicContainsUUID(uuid)) {
            System.out.println("Эпик с указанным uuid не был найден");
            return;
        }

        EpicTask epicTask = epicTasks.get(String.valueOf(uuid));
        ArrayList<Subtask> subtaskArrayList = epicTask.getSubtasks();
        for (Subtask subtask : subtaskArrayList) {
            String subtaskUUID = subtask.getTaskUUID();
            if (isSubtaskContainsUUID(subtaskUUID)) {
                subtasks.remove(subtaskUUID);
            }
        }

        epicTask.clearSubtasksList();
        epicTasks.remove(epicTask.getTaskUUID());
    }

    /* ***********************************************************************************************************
    Область методов для работы с сабтасками
     */
    public void createSubtask(String taskName, String taskDescription, EpicTask epicTask) {
        Subtask subtask = new Subtask(taskName, taskDescription, TaskType.SUBTASK, epicTask);
        epicTask.addSubtask(subtask);
        subtasks.put(subtask.getTaskUUID(), subtask);
        System.out.println("Создана сабтаска. uuid: " + subtask.getTaskUUID());
    }

    public void createSubtask(Subtask subtask) {
        EpicTask epicTask = subtask.getEpicTask();
        epicTask.addSubtask(subtask);
        subtasks.put(subtask.getTaskUUID(), subtask);
        System.out.println("Создана сабтаска. uuid: " + subtask.getTaskUUID());
    }

    public void updateSubtask(Subtask subtask, int uuid) {
        if (!isSubtaskContainsUUID(uuid)) {
            System.out.println("Сабтаска с указанным uuid не была найдена");
            return;
        }

        subtasks.put(String.valueOf(uuid), subtask);
    }

    public ArrayList<Task> getSubtaskList() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : subtasks.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtask(int uuid) {
        if (isSubtaskContainsUUID(uuid)) {
            return subtasks.get(String.valueOf(uuid));
        }
        return null;
    }

    public void removeSubtasksByUUID(int uuid) {
        if (!isSubtaskContainsUUID(uuid)) {
            System.out.println("Сабтаска с указанным uuid не была найдена");
            return;
        }

        Subtask subtask = subtasks.get(String.valueOf(uuid));
        EpicTask epicTask = subtask.getEpicTask();

        epicTask.removeSubtask(subtask);
        subtasks.remove(subtask.getTaskUUID());
        epicTask.updateEpicTaskStatus();
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
        for (Subtask task : subtasks.values()) {
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
        for (Subtask task : subtasks.values()) {
            System.out.println(task.getSimpleDescription());
        }
    }

    public ArrayList<Subtask> getEpicsSubtasksList(EpicTask epicTask) {
        return epicTask.getSubtasks();
    }

    public ArrayList<Subtask> getEpicsSubtasksList(int uuid) {
        if (!isEpicContainsUUID(uuid)) {
            System.out.println("Эпик с указанным uuid не был найден");
            return new ArrayList<>();
        }
        EpicTask epicTask = epicTasks.get(String.valueOf(uuid));
        return epicTask.getSubtasks();
    }

    public boolean isTaskContainsUUID(int uuid) {
        return tasks.containsKey(String.valueOf(uuid));
    }

    public boolean isTaskContainsUUID(String uuid) {
        return tasks.containsKey(uuid);
    }

    public boolean isEpicContainsUUID(int uuid) {
        return epicTasks.containsKey(String.valueOf(uuid));
    }

    public boolean isEpicContainsUUID(String uuid) {
        return epicTasks.containsKey(uuid);
    }

    public boolean isSubtaskContainsUUID(int uuid) {
        return subtasks.containsKey(String.valueOf(uuid));
    }

    public boolean isSubtaskContainsUUID(String uuid) {
        return subtasks.containsKey(uuid);
    }

    // Проверяет есть ли эпики
    public boolean isEpicTaskAvailable() {
        return !epicTasks.isEmpty();
    }
}
