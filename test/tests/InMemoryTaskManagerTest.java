package tests;

import manager.InMemoryTaskManager;
import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManager taskManager;
    public Task task;
    public SubTask subTask;
    public EpicTask epicTask;

    @BeforeEach
    public void BeforeEach() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Test task", "Test task description");
        taskManager.createTask(task);
        epicTask = new EpicTask("Test epicTask", "Test epicTask description");
        taskManager.createTask(epicTask);
        subTask = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
        taskManager.createTask(subTask);
    }

    @Test
    public void TestOfTaskFinding() {
        Assertions.assertEquals(true, taskManager.isTaskContainsID(task.getId()));
        Assertions.assertEquals(true, taskManager.getTask(task.getId()).getType().equals(TaskType.TASK));
    }

    @Test
    public void TestOfSubTaskFinding() {
        Assertions.assertEquals(true, taskManager.isSubTaskContainsID(subTask.getId()));
        Assertions.assertEquals(true, taskManager.getTask(subTask.getId()).getType().equals(TaskType.SUBTASK));
    }

    @Test
    public void TestOfEpicTaskFinding() {
        Assertions.assertEquals(true, taskManager.isEpicContainsID(epicTask.getId()));
        Assertions.assertEquals(true, taskManager.getTask(epicTask.getId()).getType().equals(TaskType.EPIC));
    }

    @Test
    public void TaskDoesntChangeAfterAddingToManager() {
        int taskid = task.getId();
        Assertions.assertEquals(task, taskManager.getTask(taskid));
    }

    @Test
    public void SubTaskDoesntChangeAfterAddingToManager() {
        int taskid = subTask.getId();
        Assertions.assertEquals(subTask, taskManager.getTask(taskid));
    }

    @Test
    public void EpicTaskDoesntChangeAfterAddingToManager() {
        int taskid = epicTask.getId();
        Assertions.assertEquals(epicTask, taskManager.getTask(taskid));
    }

    @Test
    public void EpicTaskShouldntHaveDeletedSubtaskID() {
        Assertions.assertEquals(true, epicTask.getSubTasks().contains(subTask.getId()));
        taskManager.getTask(subTask.getId());
        taskManager.removeTask(subTask.getId());
        Assertions.assertEquals(false, epicTask.getSubTasks().contains(subTask.getId()));
        taskManager.createTask(subTask);
    }

    @Test
    public void sortedTasksListCheck() {
        Task task1 = new Task("Test task", "Test task description", TaskType.TASK, 1,
                LocalDateTime.of(2025, 7, 6, 22, 0));
        taskManager.createTask(task1);
        EpicTask epicTask1 = new EpicTask("Test epicTask", "Test epicTask description");
        taskManager.createTask(epicTask1);
        SubTask subTask1 = new SubTask("Test subTask", "Test subTask description",
                epicTask.getId(), 2, LocalDateTime.of(2025, 7, 6, 22, 4));
        taskManager.createTask(subTask1);
        SubTask subTask2 = new SubTask("Test subTask", "Test subTask description",
                epicTask.getId(), 2, LocalDateTime.of(2025, 7, 6, 22, 2));
        taskManager.createTask(subTask2);
        ArrayList<Task> list = taskManager.getPrioritizedTasks();
        ArrayList<Task> listCorrect = new ArrayList<>();
        listCorrect.add(task1);
        listCorrect.add(subTask2);
        listCorrect.add(subTask1);
        Assertions.assertEquals(list.size(), listCorrect.size());
        Assertions.assertEquals(list.get(0), listCorrect.get(0));
        Assertions.assertEquals(list.get(1), listCorrect.get(1));
        Assertions.assertEquals(list.get(2), listCorrect.get(2));
    }

}
