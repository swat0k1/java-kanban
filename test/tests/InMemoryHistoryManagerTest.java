package tests;

import interfaces.TaskManager;
import manager.Managers;
import model.EpicTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InMemoryHistoryManagerTest {

    private Managers managers;
    private TaskManager taskManager;

    @BeforeEach
    public void BeforeEach() {
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

        int historySize1 = taskHistoryList.size();
        Task task1 = taskHistoryList.get(0);
        taskManager.getTask(task1.getId());
        taskHistoryList = taskManager.getHistory();
        int historySize2 = taskHistoryList.size();

        Assertions.assertEquals(true, historySize1 == historySize2);

        taskManager.removeTask(task1.getId());
        taskHistoryList = taskManager.getHistory();
        int historySize3 = taskHistoryList.size();
        Assertions.assertEquals(true, historySize1 == historySize3 + 1);

    }

}
