package tests;

import interfaces.TaskManager;
import manager.Managers;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {

    @Test
    public void SubTaskAndEpicTaskCheck() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();

        EpicTask epicTask = new EpicTask("Epic", "");
        SubTask subTask1 = new SubTask("SubTask 1", "", epicTask.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "", epicTask.getId());

        Assertions.assertEquals(subTask1.getEpicTaskID(), epicTask.getId());
        Assertions.assertEquals(subTask2.getEpicTaskID(), epicTask.getId());

        taskManager.createTask(epicTask);
        taskManager.createTask(subTask1);
        taskManager.createTask(subTask2);

        Assertions.assertEquals(subTask1.getEpicTaskID(), epicTask.getId());
        Assertions.assertEquals(subTask2.getEpicTaskID(), epicTask.getId());
    }

    @Test
    public void EpicStatusCalculationTest() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();

        EpicTask epicTask = new EpicTask("Test epicTask", "Test epicTask description");
        SubTask subTask1 = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
        SubTask subTask2 = new SubTask("Test subTask", "Test subTask description", epicTask.getId());

        taskManager.createTask(epicTask);
        taskManager.createTask(subTask1);
        taskManager.createTask(subTask2);

        Assertions.assertEquals(TaskStatus.NEW, epicTask.getStatus());

        subTask1.changeTaskStatus(TaskStatus.DONE);
        subTask2.changeTaskStatus(TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.DONE, epicTask.getStatus());

        subTask1.changeTaskStatus(TaskStatus.NEW);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());

        subTask1.changeTaskStatus(TaskStatus.IN_PROGRESS);
        subTask2.changeTaskStatus(TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    public void tasksOverlapTest() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();

        Task overlapTask1 = new Task("Task 1", "", TaskType.TASK, 30,
                LocalDateTime.of(2025, 7, 7, 19, 0)); // начало 19:00 конец 19:30
        Task overlapSubTask2 = new SubTask("Task 2", "", -1, 30,
                LocalDateTime.of(2025, 7, 7, 19, 15)); // начало 19:15 конец 19:45
        Task overlapSubTask3 = new SubTask("Task 3", "", -1, 30,
                LocalDateTime.of(2025, 7, 7, 19, 30)); // начало 19:30 конец 20:00
        Task overlapTask4 = new Task("Task 4", "", TaskType.TASK, 30,
                LocalDateTime.of(2025, 7, 7, 19, 45)); // начало 19:45 конец 20:15

        Assertions.assertFalse(taskManager.tasksOverlapAnotherTask(overlapTask1, overlapTask4));
        Assertions.assertTrue(taskManager.tasksOverlapAnotherTask(overlapTask1, overlapSubTask2));
        Assertions.assertFalse(taskManager.tasksOverlapAnotherTask(overlapTask1, overlapSubTask3));

    }

}
