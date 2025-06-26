package model;

import manager.*;
import java.util.Objects;

public class Task {
    private final int id;
    private String name;
    private String description;
    private TaskType type;
    private TaskStatus status;

    public Task(String taskName, String taskDescription) {
        this.id = InMemoryTaskManager.getIDCounter();
        this.name = taskName;
        this.description = taskDescription;
        this.type = TaskType.TASK;
        this.status = TaskStatus.NEW;
        InMemoryTaskManager.incrementIDCounter();
    }

    public Task(String taskName, String taskDescription, TaskType type) {
        this.id = InMemoryTaskManager.getIDCounter();
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = TaskStatus.NEW;
        InMemoryTaskManager.incrementIDCounter();
    }

    // Конструктор для создания таски при загрузке из файла
    public Task(int id, String taskName, String taskDescription, TaskType type, TaskStatus status) {
        this.id = id;
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void changeTaskStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    public String getStringValueOfTask() {
        //id,type,name,status,description
        return String.format("%d,%s,%s,%s,%s",
                                this.id, this.type.toString(), this.name, this.status.toString(), this.description);
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "taskName='" + name + '\'' +
                ", taskDescription='" + description + '\'' +
                ", taskType=" + type +
                ", taskStatus=" + status +
                ", taskID='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return getId() == task.getId() && Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) && getType() == task.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getType(), getId());
    }

}
