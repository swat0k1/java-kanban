package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private final String header = "id,type,name,status,description,duration,startTime,endTime,(-/epicID/subTasksIDs...)";
    private final int headerIdIndex = 0;
    private final int headerTypeIndex = 1;
    private final int headerNameIndex = 2;
    private final int headerStatusIndex = 3;
    private final int headerDescriptionIndex = 4;
    private final int headerDurationIndex = 5;
    private final int headerStartTimeIndex = 6;
    private final int headerEndTimeIndex = 7;
    private final int headerEpicTaskIdIndex = 8;
    private final int headerSubtaskStartIDIndex = 9;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxTaskID = -1;

        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Файл был успешно создан: " + file.getAbsolutePath());
                } else {
                    System.out.println("Не удалось создать файл.");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = manager.taskFromString(line);
                if (task.getId() > maxTaskID) {
                    maxTaskID = task.getId();
                }
                manager.createTaskWithoutSaving(task);
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке файла", e);
        }

        for (Object element : manager.getTaskList(TaskType.EPIC)) {
            EpicTask epicTask = (EpicTask) element;
            epicTask.calculateTime();
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
        Duration duration = Duration.parse(values[headerDurationIndex]);
        LocalDateTime startTime = LocalDateTime.parse(values[headerStartTimeIndex]);
        LocalDateTime endTime = LocalDateTime.parse(values[headerEndTimeIndex]);

        return switch (type) {
            case TASK -> createTask(id, name, description, status, duration, startTime, endTime);
            case SUBTASK -> createSubTask(id, name, description, status, Integer.parseInt(values[headerEpicTaskIdIndex]),
                                            duration, startTime, endTime);
            case EPIC -> createEpicTask(id, name, description, status, values, duration, startTime, endTime);
        };
    }

    private Task createTask(int id, String name, String description, TaskStatus status,
                            Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        return new Task(this, id, name, description, TaskType.TASK, status, duration, startTime, endTime);
    }

    private SubTask createSubTask(int id, String name, String description, TaskStatus status, int epicID,
                                  Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        return new SubTask(this, id, name, description, status, epicID, duration, startTime, endTime);
    }

    private EpicTask createEpicTask(int id, String name, String description, TaskStatus status, String[] values,
                                    Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        EpicTask epicTask = new EpicTask(this, id, name, description, status, duration, startTime, endTime);
        for (int i = headerSubtaskStartIDIndex; i < values.length; i++) {
            epicTask.addSubTask(Integer.parseInt(values[i]));
        }
        return epicTask;
    }

    @Override
    public int createTask(Task task) {
        int result = super.createTask(task);
        save();
        return result;
    }

    public void createTaskWithoutSaving(Task task) {
        super.createTask(task);
    }

    @Override
    public int updateTask(Task task, int id) {
        int result = super.updateTask(task, id);
        save();
        return result;
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
