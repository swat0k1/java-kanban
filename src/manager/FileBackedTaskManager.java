package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.*;

import java.io.*;

public class FileBackedTaskManager<T extends Task> extends InMemoryTaskManager {

    private final File file;
    private String header = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager<Task> loadFromFile(File file) {
        FileBackedTaskManager<Task> manager = new FileBackedTaskManager<>(file);
        int maxTaskID = -1;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

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

            writer.close();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    private Task taskFromString(String stringValueOfTask) {
        String[] values = stringValueOfTask.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];

        switch (type) {
            case TASK -> {
                return new Task(id, name, description, type, status);
            }
            case SUBTASK -> {
                int epicID = Integer.parseInt(values[5]);
                return new SubTask(id, name, description, type, status, epicID);
            }
            case EPIC -> {
                EpicTask epicTask = new EpicTask(id, name, description, type, status);
                int subTasksIdStart = 6;
                int subTasksIdEnd = values.length - 1;
                for (int i = subTasksIdStart; i <= subTasksIdEnd; i++) {
                    epicTask.addSubTask(Integer.parseInt(values[i]));
                }
                return epicTask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи");
        }
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
