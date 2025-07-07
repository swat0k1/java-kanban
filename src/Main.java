import interfaces.TaskManager;
import manager.Managers;
import model.*;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();

        TaskManager inMemoryTaskManager = managers.getDefault();

        Task simpleTask1 = new Task("Simple task 1", "");
        Task simpleTask2 = new Task("Simple task 2", "");

        Task epicTask1 = new EpicTask("Epic task 1", "");
        Task subTask1 = new SubTask("Subtask 1","", epicTask1.getId());
        Task subTask2 = new SubTask("Subtask 2","", epicTask1.getId());
        Task subTask3 = new SubTask("Subtask 3","", epicTask1.getId());

        Task epicTask2 = new EpicTask("Epic task 2", "");

        inMemoryTaskManager.createTask(simpleTask1);
        inMemoryTaskManager.createTask(simpleTask2);
        inMemoryTaskManager.createTask(epicTask1);
        inMemoryTaskManager.createTask(subTask1);
        inMemoryTaskManager.createTask(subTask2);
        inMemoryTaskManager.createTask(subTask3);
        inMemoryTaskManager.createTask(epicTask2);

        System.out.println("====================================================================================");
        inMemoryTaskManager.getTask(simpleTask2.getId());
        inMemoryTaskManager.getTask(simpleTask2.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(simpleTask1.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(epicTask2.getId());
        inMemoryTaskManager.getTask(epicTask2.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(subTask3.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(subTask2.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(subTask1.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
        inMemoryTaskManager.getTask(epicTask1.getId());
        inMemoryTaskManager.getTask(epicTask1.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("====================================================================================");
        System.out.println();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        inMemoryTaskManager.removeTask(simpleTask2.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        inMemoryTaskManager.removeTask(epicTask1.getId());
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println();

    }
}
