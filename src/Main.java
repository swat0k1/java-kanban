import interfaces.TaskManager;
import manager.Managers;
import model.*;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();

        TaskManager inMemoryTaskManager = managers.getDefault();

        Task task1 = new Task("Test task 1", "Description of test task 1");
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Test task 2", "Description of test task 2");
        inMemoryTaskManager.createTask(task2);

        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(0);
        inMemoryTaskManager.getTask(1);
        System.out.println("Заполненная история:");
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getTask(0);
        System.out.println("В историю добавлен 11ый элемент");
        System.out.println(inMemoryTaskManager.getHistory());


        EpicTask epicTask1 = new EpicTask("Test epic 1", "Description of test epic 1",
                                        TaskType.EPIC);
        inMemoryTaskManager.createTask(epicTask1);
        SubTask subTask1 = new SubTask("Test subtask 1","Description of test subtask 1",
                                        TaskType.SUBTASK, epicTask1.getId());
        inMemoryTaskManager.createTask(subTask1);
        SubTask subTask2 = new SubTask("Test subtask 2","Description of test subtask 2",
                TaskType.SUBTASK, epicTask1.getId());
        inMemoryTaskManager.createTask(subTask2);

        EpicTask epicTask2 = new EpicTask("Test epic 2", "Description of test epic 2",
                TaskType.EPIC);
        inMemoryTaskManager.createTask(epicTask2);
        SubTask subTask3 = new SubTask("Test subtask 3","Description of test subtask 3",
                TaskType.SUBTASK, epicTask2.getId());
        inMemoryTaskManager.createTask(subTask3);

        System.out.println();
        System.out.println(inMemoryTaskManager.getTaskList(TaskType.TASK) + "\n");
        System.out.println(inMemoryTaskManager.getTaskList(TaskType.EPIC) + "\n");
        System.out.println(inMemoryTaskManager.getTaskList(TaskType.SUBTASK) + "\n");

        task1.changeTaskStatus(TaskStatus.IN_PROGRESS);
        task2.changeTaskStatus(TaskStatus.DONE);

        //epicTask1.changeTaskStatus(TaskStatus.DONE);
        subTask1.changeTaskStatus(TaskStatus.DONE, inMemoryTaskManager);
        subTask2.changeTaskStatus(TaskStatus.IN_PROGRESS, inMemoryTaskManager);

        subTask3.changeTaskStatus(TaskStatus.DONE, inMemoryTaskManager);

        System.out.println(task1.getSimpleDescription());
        System.out.println(task2.getSimpleDescription() + "\n");

        System.out.println(epicTask1.getSimpleDescription());
        System.out.println(subTask1.getSimpleDescription());
        System.out.println(subTask2.getSimpleDescription() + "\n");

        System.out.println(epicTask2.getSimpleDescription());
        System.out.println(subTask3.getSimpleDescription() + "\n");

        inMemoryTaskManager.removeTask(1);
        inMemoryTaskManager.removeTask(4);
        inMemoryTaskManager.removeTask(5);

        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getTaskList(TaskType.TASK) + "\n");
        System.out.println(inMemoryTaskManager.getTaskList(TaskType.EPIC) + "\n");
        System.out.println(inMemoryTaskManager.getTaskList(TaskType.SUBTASK) + "\n");

    }
}
