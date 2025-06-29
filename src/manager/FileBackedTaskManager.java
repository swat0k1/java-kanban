package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private final String header = "id,type,name,status,description,epic";
    private final int headerIdIndex = 0;
    private final int headerTypeIndex = 1;
    private final int headerNameIndex = 2;
    private final int headerStatusIndex = 3;
    private final int headerDescriptionIndex = 4;
    private final int headerEpicTaskIdIndex = 5;
    private final int headerSubtaskStartIDIndex = 6;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxTaskID = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = manager.taskFromString(line);
                if (task.getId() > maxTaskID) {
                    maxTaskID = task.getId();
                }
                manager.createTask(task);
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке файла", e);
        }

        if (maxTaskID != -1) {
            InMemoryTaskManager.setIDCounter(maxTaskID + 1);
        }
        return manager;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(header);
            writer.newLine();

            // Таска
            for (Object task : getTaskList(TaskType.TASK)) {
                writer.write(((Task) task).getStringValueOfTask());
                writer.newLine();
            }

            // Эпик
            for (Object task : getTaskList(TaskType.EPIC)) {
                writer.write(((EpicTask) task).getStringValueOfTask());
                writer.newLine();
            }

            // Сабтаска
            for (Object task : getTaskList(TaskType.SUBTASK)) {
                writer.write(((SubTask) task).getStringValueOfTask());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    private Task taskFromString(String stringValueOfTask) {
        String[] values = stringValueOfTask.split(",");
        int id = Integer.parseInt(values[headerIdIndex]);
        TaskType type = TaskType.valueOf(values[headerTypeIndex]);
        String name = values[headerNameIndex];
        TaskStatus status = TaskStatus.valueOf(values[headerStatusIndex]);
        String description = values[headerDescriptionIndex];

        return switch (type) {
            case TASK -> createTask(id, name, description, status);
            case SUBTASK -> createSubTask(id, name, description, status, Integer.parseInt(values[headerEpicTaskIdIndex]));
            case EPIC -> createEpicTask(id, name, description, status, values);
        };
    }

    private Task createTask(int id, String name, String description, TaskStatus status) {
        return new Task(id, name, description, TaskType.TASK, status);
    }

    private SubTask createSubTask(int id, String name, String description, TaskStatus status, int epicID) {
        return new SubTask(id, name, description, TaskType.SUBTASK, status, epicID);
    }

    private EpicTask createEpicTask(int id, String name, String description, TaskStatus status, String[] values) {
        EpicTask epicTask = new EpicTask(id, name, description, TaskType.EPIC, status);
        for (int i = headerSubtaskStartIDIndex; i < values.length; i++) {
            epicTask.addSubTask(Integer.parseInt(values[i]));
        }
        return epicTask;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks(TaskType taskType) {
        super.removeAllTasks(taskType);
        save();
    }
}
