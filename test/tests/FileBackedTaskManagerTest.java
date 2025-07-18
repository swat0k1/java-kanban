package tests;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest {

    @Test
    public void SaveLoadEmptyFile() throws IOException {
        File file = File.createTempFile("test", ".csv");
        Managers managers = new Managers();
        FileBackedTaskManager taskManagerBacked = managers.getBacked(file);

        taskManagerBacked.save();
        FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    public void SaveLoadTasks() throws IOException {
        File file = File.createTempFile("test", ".csv");
        Managers managers = new Managers();
        FileBackedTaskManager taskManagerBacked = managers.getBacked(file);
        String checkString1 = "";
        String checkString2 = "";
        String checkString3 = "";
        String checkString4 = "";
        String checkString5 = "";
        String checkString6 = "";

        Task task = new Task("Test task", "Test task description");
        taskManagerBacked.createTask(task);
        EpicTask epicTask = new EpicTask("Test epicTask", "Test epicTask description");
        taskManagerBacked.createTask(epicTask);
        SubTask subTask = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
        taskManagerBacked.createTask(subTask);
        checkString1 = task.getStringValueOfTask();
        checkString2 = epicTask.getStringValueOfTask();
        checkString3 = subTask.getStringValueOfTask();

        int taskID = task.getId();
        int epicTaskID = epicTask.getId();
        int subTaskID = subTask.getId();

        taskManagerBacked = FileBackedTaskManager.loadFromFile(file);
        checkString4 = taskManagerBacked.getTask(taskID).getStringValueOfTask();
        checkString5 = taskManagerBacked.getTask(epicTaskID).getStringValueOfTask();
        checkString6 = taskManagerBacked.getTask(subTaskID).getStringValueOfTask();

        Assertions.assertEquals(checkString1, checkString4);
        Assertions.assertEquals(checkString2, checkString5);
        Assertions.assertEquals(checkString3,checkString6);

    }

    @Test
    public void SaveLoadTasksWithDeletion() throws IOException {
        File file = File.createTempFile("test", ".csv");
        Managers managers = new Managers();
        FileBackedTaskManager taskManagerBacked = managers.getBacked(file);
        InMemoryTaskManager.setIDCounter(0);
        String checkString1 = "";
        String checkString2 = "";
        String checkString3 = "";
        String checkString4 = "";
        String checkString5 = "";
        String checkString6 = "";
        String checkString7 = "";
        String checkString8 = "";

        Task task = new Task("Test task before", "Test task description");
        taskManagerBacked.createTask(task);
        EpicTask epicTask = new EpicTask("Test epicTask before", "Test epicTask description");
        taskManagerBacked.createTask(epicTask);
        SubTask subTask = new SubTask("Test subTask before", "Test subTask description", epicTask.getId());
        taskManagerBacked.createTask(subTask);
        SubTask subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description", epicTask.getId());
        taskManagerBacked.createTask(subTask1);

        taskManagerBacked.removeAllTasks(TaskType.TASK);
        taskManagerBacked.removeAllTasks(TaskType.EPIC);
        taskManagerBacked.removeAllTasks(TaskType.SUBTASK);

        task = new Task("Test task after", "Test task description");
        taskManagerBacked.createTask(task);
        epicTask = new EpicTask("Test epicTask after", "Test epicTask description");
        taskManagerBacked.createTask(epicTask);
        subTask = new SubTask("Test subTask after", "Test subTask description", epicTask.getId());
        taskManagerBacked.createTask(subTask);
        subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description", epicTask.getId());
        taskManagerBacked.createTask(subTask1);

        checkString1 = task.getStringValueOfTask();
        checkString2 = epicTask.getStringValueOfTask();
        checkString3 = subTask.getStringValueOfTask();
        checkString4 = subTask1.getStringValueOfTask();

        taskManagerBacked = FileBackedTaskManager.loadFromFile(file);
        checkString5 = taskManagerBacked.getTask(4).getStringValueOfTask();
        checkString6 = taskManagerBacked.getTask(5).getStringValueOfTask();
        checkString7 = taskManagerBacked.getTask(6).getStringValueOfTask();
        checkString8 = taskManagerBacked.getTask(7).getStringValueOfTask();

        Assertions.assertEquals(checkString1, checkString5);
        Assertions.assertEquals(checkString2, checkString6);
        Assertions.assertEquals(checkString3, checkString7);
        Assertions.assertEquals(checkString4, checkString8);

    }

    @Test
    public void SaveLoadTasksWithDeletionAndTime() throws IOException {
        File file = File.createTempFile("test", ".csv");
        Managers managers = new Managers();
        FileBackedTaskManager taskManagerBacked = managers.getBacked(file);
        InMemoryTaskManager.setIDCounter(0);
        String checkString1 = "";
        String checkString2 = "";
        String checkString3 = "";
        String checkString4 = "";
        String checkString5 = "";
        String checkString6 = "";
        String checkString7 = "";
        String checkString8 = "";

        Task task = new Task("Test task before", "Test task description", TaskType.TASK,
                    5, LocalDateTime.of(2025, 7, 6, 17, 15, 30));
        taskManagerBacked.createTask(task);
        EpicTask epicTask = new EpicTask("Test epicTask before", "Test epicTask description");
        taskManagerBacked.createTask(epicTask);
        SubTask subTask = new SubTask("Test subTask before", "Test subTask description",
                epicTask.getId(), 5, LocalDateTime.of(2025, 7, 6, 17, 30));
        taskManagerBacked.createTask(subTask);
        SubTask subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description",
                epicTask.getId(), 5, LocalDateTime.of(2025, 7, 6, 17, 45));
        taskManagerBacked.createTask(subTask1);

        taskManagerBacked.removeAllTasks(TaskType.TASK);
        taskManagerBacked.removeAllTasks(TaskType.EPIC);
        taskManagerBacked.removeAllTasks(TaskType.SUBTASK);

        task = new Task("Test task after", "Test task description", TaskType.TASK,
                5, LocalDateTime.of(2025, 7, 7, 17, 15, 30));
        taskManagerBacked.createTask(task);
        epicTask = new EpicTask("Test epicTask after", "Test epicTask description");
        taskManagerBacked.createTask(epicTask);
        subTask = new SubTask("Test subTask after", "Test subTask description", epicTask.getId(),
                5, LocalDateTime.of(2025, 7, 7, 17, 30));
        taskManagerBacked.createTask(subTask);
        subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description",
                epicTask.getId(), 5, LocalDateTime.of(2025, 7, 7, 17, 45));
        taskManagerBacked.createTask(subTask1);

        checkString1 = task.getStringValueOfTask();
        checkString2 = epicTask.getStringValueOfTask();
        checkString3 = subTask.getStringValueOfTask();
        checkString4 = subTask1.getStringValueOfTask();

        taskManagerBacked = FileBackedTaskManager.loadFromFile(file);
        checkString5 = taskManagerBacked.getTask(4).getStringValueOfTask();
        checkString6 = taskManagerBacked.getTask(5).getStringValueOfTask();
        checkString7 = taskManagerBacked.getTask(6).getStringValueOfTask();
        checkString8 = taskManagerBacked.getTask(7).getStringValueOfTask();

        Assertions.assertEquals(checkString1, checkString5);
        Assertions.assertEquals(checkString2, checkString6);
        Assertions.assertEquals(checkString3, checkString7);
        Assertions.assertEquals(checkString4, checkString8);

    }

    @Test
    void LoadFromFileTest() {

        File file = new File("file.csv");

        Exception exception = assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Ошибка при загрузке файла");

    }

    @Test
    void testSave_FileWriteError() {
        // Создаем файл в защищенной директории или с запрещенными правами (это пример, возможно, потребуется адаптация под вашу файловую систему)
        File protectedFile = new File("/file.csv"); // Замените на путь к защищенной директории

        FileBackedTaskManager manager = new FileBackedTaskManager(protectedFile);

        // Проверяем, что метод выбрасывает исключение
        Exception exception = assertThrows(ManagerSaveException.class, () -> {
            manager.save();
        }, "Ошибка сохранения файла");

    }

}
