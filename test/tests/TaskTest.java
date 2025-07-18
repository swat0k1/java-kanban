package tests;
import interfaces.TaskManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskTest {

    private Managers managers;
    private TaskManager taskManager;
    private Task task;
    private SubTask subTask;
    private EpicTask epicTask;

    @BeforeEach
    public void BeforeEach() {
        managers = new Managers();
        taskManager = managers.getDefault();
        task = new Task("Test task", "Test task description");
        taskManager.createTask(task);

        epicTask = new EpicTask("Test epicTask", "Test epicTask description");
        taskManager.createTask(epicTask);

        subTask = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
        taskManager.createTask(subTask);
    }

    @Test
    public void TaskEqualityTest() {

        int taskId = task.getId();
        Assertions.assertEquals(taskManager.getTask(taskId), taskManager.getTask(taskId), "Test not passed!");

    }

    @Test
    public void SubTaskEqualityTest() {

        int taskId = subTask.getId();
        Assertions.assertEquals(taskManager.getTask(taskId), taskManager.getTask(taskId), "Test not passed!");

    }

    @Test
    public void EpicTaskEqualityTest() {

        int taskId = epicTask.getId();
        Assertions.assertEquals(taskManager.getTask(taskId), taskManager.getTask(taskId), "Test not passed!");

    }

    @Test
    public void EpicCantBeItsSubTask1() {
        int startSize = epicTask.getSubTasks().size();
        epicTask.addSubTask(epicTask.getId());
        int resultSize = epicTask.getSubTasks().size();
        Assertions.assertEquals(startSize, resultSize, "Test not passed!");
    }

    @Test
    public void SubTaskCantBeItsEpic() {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) taskManager;
        SubTask subTask1 = new SubTask("SubTask test", "SubTask test description",
                                        inMemoryTaskManager.getIDCounter());
        Assertions.assertEquals(-1, subTask1.getEpicTaskID(), "Test not passed!");
    }

    @Test
    public void EpicStatusCheck() {

        taskManager = managers.getDefault();

        epicTask = new EpicTask("Test epicTask", "Test epicTask description");
        taskManager.createTask(epicTask);

        SubTask subTask1 = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
        taskManager.createTask(subTask1);

        SubTask subTask2 = new SubTask("Test subTask", "Test subTask description", epicTask.getId());
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

}
