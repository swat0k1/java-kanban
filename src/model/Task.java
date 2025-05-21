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

    public String getSimpleDescription() {
        String result = "";

        if (this.type.equals(TaskType.TASK)) {
            result += TaskType.TASK.toString();

        } else if (this.type.equals(TaskType.EPIC)) {
            result += TaskType.EPIC.toString();
        } else {
            result += TaskType.SUBTASK.toString();
        }

        result += " " + this.name;

        if (this.status.equals(TaskStatus.NEW)) {
            result += " " + TaskStatus.NEW.toString() + " ";
        } else if (this.status.equals(TaskStatus.IN_PROGRESS)) {
            result += " " + TaskStatus.IN_PROGRESS.toString() + " ";
        } else {
            result += " " + TaskStatus.DONE.toString() + " ";
        }

        result += "ID " + this.id;

        return result;
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
