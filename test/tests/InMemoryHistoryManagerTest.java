package tests;

import interfaces.TaskManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InMemoryHistoryManagerTest {

    @Test
    public void TaskAddingTest() {

        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();

        Task task1 = new Task("Test task 1", "Description");
        Task task2 = new Task("Test task 2", "Description");


        taskManager.createTask(task1);
        taskManager.createTask(task2);


        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());


        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void AddingAndRemovalTest1() {

        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();

        Task task1 = new Task("Test task 1", "Description");
        Task task2 = new Task("Test task 2", "Description");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        ArrayList<Task> taskHistoryList = taskManager.getHistory();

        int historySize1 = taskHistoryList.size();
        task1 = taskHistoryList.get(0);
        taskManager.getTask(task1.getId());
        taskHistoryList = taskManager.getHistory();
        int historySize2 = taskHistoryList.size();

        Assertions.assertEquals(true, historySize1 == historySize2);

        taskManager.removeTask(task1.getId());
        taskHistoryList = taskManager.getHistory();
        int historySize3 = taskHistoryList.size();
        Assertions.assertEquals(true, historySize1 == historySize3 + 1);

    }

    @Test
    public void AddingAndRemovalTest2() {

        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();

        ArrayList<Task> taskHistoryList = taskManager.getHistory();

        Assertions.assertEquals(0, taskHistoryList.size());

        Task task1 = new Task("Test task 1", "Description");
        Task task2 = new Task("Test task 2", "Description");
        Task task3 = new Task("Test task 3", "Description");
        Task task4 = new Task("Test task 4", "Description");
        Task task5 = new Task("Test task 5", "Description");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task4.getId());
        taskManager.getTask(task5.getId());

        taskHistoryList = taskManager.getHistory();
        Assertions.assertEquals(5, taskHistoryList.size());

        taskManager.getTask(task4.getId());
        taskHistoryList = taskManager.getHistory();
        Assertions.assertEquals(5, taskHistoryList.size());
        Assertions.assertEquals(task4.getId(), taskHistoryList.get(0).getId());

        taskManager.removeTask(task1.getId());
        taskHistoryList = taskManager.getHistory();
        Assertions.assertEquals(4, taskHistoryList.size());
        Assertions.assertEquals(task2.getId(), taskHistoryList.get(3).getId());

        taskManager.removeTask(task4.getId());
        taskHistoryList = taskManager.getHistory();
        Assertions.assertEquals(3, taskHistoryList.size());
        Assertions.assertEquals(task5.getId(), taskHistoryList.get(0).getId());

        taskManager.removeTask(task3.getId());
        taskHistoryList = taskManager.getHistory();
        Assertions.assertEquals(2, taskHistoryList.size());
        Assertions.assertEquals(task2.getId(), taskHistoryList.get(1).getId());
        Assertions.assertEquals(task5.getId(), taskHistoryList.get(0).getId());

    }

}
