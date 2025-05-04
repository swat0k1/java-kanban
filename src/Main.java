import manager.TaskManager;
import model.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        /*
            Область для тестирования согласно ТЗ
         */
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Test task 1", "Description of test task 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Test task 2", "Description of test task 2");
        taskManager.createTask(task2);

        EpicTask epicTask1 = new EpicTask("Test epic 1", "Description of test epic 1",
                                        TaskType.EPIC);
        taskManager.createEpicTask(epicTask1);
        SubTask subTask1 = new SubTask("Test subtask 1","Description of test subtask 1",
                                        TaskType.SUBTASK, epicTask1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test subtask 2","Description of test subtask 2",
                TaskType.SUBTASK, epicTask1.getId());
        taskManager.createSubTask(subTask2);

        EpicTask epicTask2 = new EpicTask("Test epic 2", "Description of test epic 2",
                TaskType.EPIC);
        taskManager.createEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("Test subtask 3","Description of test subtask 3",
                TaskType.SUBTASK, epicTask2.getId());
        taskManager.createSubTask(subTask3);

        System.out.println();
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicTaskList() + "\n");
        System.out.println(taskManager.getSubTaskList() + "\n");

        task1.changeTaskStatus(TaskStatus.IN_PROGRESS);
        task2.changeTaskStatus(TaskStatus.DONE);

        epicTask1.changeTaskStatus(TaskStatus.DONE);
        subTask1.changeTaskStatus(TaskStatus.DONE, taskManager);
        subTask2.changeTaskStatus(TaskStatus.IN_PROGRESS, taskManager);

        subTask3.changeTaskStatus(TaskStatus.DONE, taskManager);

        System.out.println(task1.getSimpleDescription());
        System.out.println(task2.getSimpleDescription() + "\n");

        System.out.println(epicTask1.getSimpleDescription());
        System.out.println(subTask1.getSimpleDescription());
        System.out.println(subTask2.getSimpleDescription() + "\n");

        System.out.println(epicTask2.getSimpleDescription());
        System.out.println(subTask3.getSimpleDescription() + "\n");

        taskManager.removeTaskByID(1);
        taskManager.removeSubTasksByID(4);
        taskManager.removeEpicTaskByID(5);

        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicTaskList() + "\n");
        System.out.println(taskManager.getSubTaskList() + "\n");


        /*
            Область для ручного тестирования
         */
        Scanner scanner = new Scanner(System.in);
        int command;
        taskManager = new TaskManager();
        System.out.println("Добро пожаловать в трекер задач!");

        while (true) {
            printMainMenu();
            command = scanner.nextInt();

            if (command == 1) {

                System.out.println("Выберете тип задачи: 1 - таск; 2 - эпик; 3 - сабтаск");

                TaskType taskType;
                String taskName;
                String taskDescription;
                command = scanner.nextInt();

                if (command == 1) {
                    taskType = TaskType.TASK;
                } else if (command == 2) {
                    taskType = TaskType.EPIC;
                } else if (command == 3) {
                    taskType = TaskType.SUBTASK;
                } else {
                    System.out.println("Такого типа задачи нет");
                    continue;
                }

                if (taskType.equals(TaskType.TASK)) {
                    scanner.nextLine();
                    System.out.println("Введите название задачи");
                    taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи");
                    taskDescription = scanner.nextLine();
                    taskManager.createTask(taskName, taskDescription);

                } else if (taskType.equals(TaskType.EPIC)) {
                    scanner.nextLine();
                    System.out.println("Введите название задачи");
                    taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи");
                    taskDescription = scanner.nextLine();
                    taskManager.createEpicTask(taskName, taskDescription);

                } else if (taskType.equals(TaskType.SUBTASK)) {
                    if (taskManager.isEpicTaskAvailable()) {
                        scanner.nextLine();
                        System.out.println("Введите название задачи");
                        taskName = scanner.nextLine();
                        System.out.println("Введите описание задачи");
                        taskDescription = scanner.nextLine();
                        System.out.println("Введите id эпика");
                        int taskID = scanner.nextInt();
                        EpicTask epicTask = taskManager.getEpicTask(taskID);
                        if (epicTask != null) {
                            taskManager.createSubTask(taskName, taskDescription, epicTask.getId());
                        } else {
                            System.out.println("Не удалось найти эпик по указанному id");
                        }
                    } else {
                        System.out.println("Отсутствуют эпики. Создайте сначала эпик");
                    }
                }

            } else if (command == 2) {

                System.out.println("Введите id задачи");
                int taskID = scanner.nextInt();

                System.out.println("Укажите новый статус задачи: 1 - New; 2 - In progress; 3 - Done");
                command = scanner.nextInt();
                scanner.nextLine();
                TaskStatus newStatus;

                if (command == 1) {
                    newStatus = TaskStatus.NEW;
                } else if (command == 2) {
                    newStatus = TaskStatus.IN_PROGRESS;
                } else if (command == 3) {
                    newStatus = TaskStatus.DONE;
                } else {
                    System.out.println("Такого статуса нет");
                    continue;
                }

                if (taskManager.isEpicContainsID(taskID)) {
                    EpicTask task = taskManager.getEpicTask(taskID);
                    task.changeTaskStatus(newStatus);
                } else if (taskManager.isTaskContainsID(taskID)) {
                    Task task = taskManager.getTask(taskID);
                    task.changeTaskStatus(newStatus);
                } else if (taskManager.isSubTaskContainsID(taskID)) {
                    SubTask task = taskManager.getSubTask(taskID);
                    task.changeTaskStatus(newStatus, taskManager);
                } else {
                    System.out.println("Задача с указанным id не найдена");
                }

            } else if (command == 3) {
                taskManager.printTasksNames();
            } else if (command == 4) {
                taskManager.printTasksDescriptionFull();
            } else if (command == 0) {
                System.out.println("Работа программы окончена");
                break;
            } else {
                System.out.println("Такой команды нет");
            }
        }
    }

    public static void printMainMenu() {
        System.out.println("1 - Создание задачи");
        System.out.println("2 - Редактировать статус задачи");
        System.out.println("3 - Вывести описание задач");
        System.out.println("4 - Вывести полное описание задач");
        System.out.println("0 - Завершить работу");
    }
}
