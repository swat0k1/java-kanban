package tests;

import interfaces.TaskManager;
import manager.Managers;
import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {

    public Managers managers;
    public TaskManager taskManager;
    public Task task;
    public SubTask subTask;
    public EpicTask epicTask;

    @BeforeEach
    public void BeforeEach() {
        managers = new Managers();
        taskManager = managers.getDefault();
        task = new Task("Test task", "Test task description");
        taskManager.createTask(task);
        epicTask = new EpicTask("Test epicTask", "Test epicTask description", TaskType.EPIC);
        taskManager.createTask(epicTask);
        subTask = new SubTask("Test subTask", "Test subTask description",TaskType.SUBTASK, epicTask.getId());
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

}
