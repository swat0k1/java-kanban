package tests;

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
        EpicTask epicTask = new EpicTask("Test epicTask", "Test epicTask description", TaskType.EPIC);
        taskManagerBacked.createTask(epicTask);
        SubTask subTask = new SubTask("Test subTask", "Test subTask description",TaskType.SUBTASK, epicTask.getId());
        taskManagerBacked.createTask(subTask);
        checkString1 = task.getStringValueOfTask();
        checkString2 = epicTask.getStringValueOfTask();
        checkString3 = subTask.getStringValueOfTask();

        taskManagerBacked = FileBackedTaskManager.loadFromFile(file);
        checkString4 = taskManagerBacked.getTask(0).getStringValueOfTask();
        checkString5 = taskManagerBacked.getTask(1).getStringValueOfTask();
        checkString6 = taskManagerBacked.getTask(2).getStringValueOfTask();

        Assertions.assertEquals(true, checkString1.equals(checkString4));
        Assertions.assertEquals(true, checkString2.equals(checkString5));
        Assertions.assertEquals(true, checkString3.equals(checkString6));

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
        EpicTask epicTask = new EpicTask("Test epicTask before", "Test epicTask description", TaskType.EPIC);
        taskManagerBacked.createTask(epicTask);
        SubTask subTask = new SubTask("Test subTask before", "Test subTask description",TaskType.SUBTASK, epicTask.getId());
        taskManagerBacked.createTask(subTask);
        SubTask subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description",TaskType.SUBTASK, epicTask.getId());
        taskManagerBacked.createTask(subTask1);

        taskManagerBacked.removeAllTasks(TaskType.TASK);
        taskManagerBacked.removeAllTasks(TaskType.EPIC);
        taskManagerBacked.removeAllTasks(TaskType.SUBTASK);

        task = new Task("Test task after", "Test task description");
        taskManagerBacked.createTask(task);
        epicTask = new EpicTask("Test epicTask after", "Test epicTask description", TaskType.EPIC);
        taskManagerBacked.createTask(epicTask);
        subTask = new SubTask("Test subTask after", "Test subTask description",TaskType.SUBTASK, epicTask.getId());
        taskManagerBacked.createTask(subTask);
        subTask1 = new SubTask("Test subTask 2 before", "Test subTask 2 description",TaskType.SUBTASK, epicTask.getId());
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

        Assertions.assertEquals(true, checkString1.equals(checkString5));
        Assertions.assertEquals(true, checkString2.equals(checkString6));
        Assertions.assertEquals(true, checkString3.equals(checkString7));
        Assertions.assertEquals(true, checkString4.equals(checkString8));

    }

}
