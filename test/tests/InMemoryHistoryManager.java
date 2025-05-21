package tests;

import interfaces.TaskManager;
import manager.Managers;
import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InMemoryHistoryManager {

    public static Managers managers;
    public static TaskManager taskManager;

    @BeforeAll
    public static void BeforeAll() {
        managers = new Managers();
        taskManager = managers.getDefault();

        Task task1 = new Task("Test task 1", "Description");
        Task task2 = new Task("Test task 2", "Description");
        Task task3 = new Task("Test task 3", "Description");
        Task task4 = new Task("Test task 4", "Description");
        Task task5 = new Task("Test task 5", "Description");
        Task task6 = new Task("Test task 6", "Description");
        Task task7 = new Task("Test task 7", "Description");
        Task task8 = new Task("Test task 8", "Description");
        Task task9 = new Task("Test task 9", "Description");
        Task task10 = new Task("Test task 10", "Description");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);
        taskManager.createTask(task6);
        taskManager.createTask(task7);
        taskManager.createTask(task8);
        taskManager.createTask(task9);
        taskManager.createTask(task10);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task4.getId());
        taskManager.getTask(task5.getId());
        taskManager.getTask(task6.getId());
        taskManager.getTask(task7.getId());
        taskManager.getTask(task8.getId());
        taskManager.getTask(task9.getId());
        taskManager.getTask(task10.getId());

        Assertions.assertEquals(10, taskManager.getHistory().size());
    }

    @Test
    public void AddingAndRemovalTest() {
        ArrayList<Task> taskHistoryList = taskManager.getHistory();
        Task task1 = taskHistoryList.get(0);
        EpicTask epicTask = new EpicTask("Epic task 1", "Description", TaskType.EPIC);
        Assertions.assertEquals(true, taskHistoryList.contains(task1));
        Assertions.assertEquals(false,taskHistoryList.contains(epicTask));
        taskManager.createTask(epicTask);
        taskManager.getTask(epicTask.getId());
        Assertions.assertEquals(false, taskHistoryList.contains(task1));
        Assertions.assertEquals(true,taskHistoryList.contains(epicTask));
    }

}
