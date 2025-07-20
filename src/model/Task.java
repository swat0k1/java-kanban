package model;

import manager.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private final int id;
    private String name;
    private String description;
    private TaskType type;
    private TaskStatus status;
    protected InMemoryTaskManager inMemoryTaskManager = null;
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime = LocalDateTime.MIN;
    private LocalDateTime endTime = LocalDateTime.MIN;

    // Конструктор для создания обычной таски без времени исполнения
    public Task(String taskName, String taskDescription) {
        this.id = InMemoryTaskManager.getIDCounter();
        this.name = taskName;
        this.description = taskDescription;
        this.type = TaskType.TASK;
        this.status = TaskStatus.NEW;
        InMemoryTaskManager.incrementIDCounter();
    }

    // Конструктор для создания таски определенного типа без времени исполнения
    public Task(String taskName, String taskDescription, TaskType type) {
        this.id = InMemoryTaskManager.getIDCounter();
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = TaskStatus.NEW;
        InMemoryTaskManager.incrementIDCounter();
    }

    // Конструктор для создания таски с временем исполнения
    public Task(String taskName, String taskDescription, TaskType type, int duration, LocalDateTime startTime) {
        this.id = InMemoryTaskManager.getIDCounter();
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
        this.endTime = calculateEndTime();
        InMemoryTaskManager.incrementIDCounter();
    }

    // Конструктор для создания таски при загрузке из файла
    public Task(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription, TaskType type, TaskStatus status) {
        this.id = id;
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = status;
        this.inMemoryTaskManager = inMemoryTaskManager;
        InMemoryTaskManager.incrementIDCounter();
    }

    // Конструктор для создания таски при загрузке из файла с временем исполнения
    public Task(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription, TaskType type, TaskStatus status,
                Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.inMemoryTaskManager = inMemoryTaskManager;
        InMemoryTaskManager.incrementIDCounter();
    }

    public Task(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription, TaskType type) {
        this.id = id;
        this.name = taskName;
        this.description = taskDescription;
        this.type = type;
        this.inMemoryTaskManager = inMemoryTaskManager;
        InMemoryTaskManager.incrementIDCounter();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime calculateEndTime() {
        if (durationAndStartTimeNotSet()) {
            System.out.println("Значения duration или startTime не заданы");
            return LocalDateTime.MIN;
        } else {
            return startTime.plus(duration);
        }
    }

    public boolean durationAndStartTimeNotSet() {
        return this.duration.equals(Duration.ZERO) || this.startTime.equals(LocalDateTime.MIN);
    }

    public void setInMemoryTaskManager(InMemoryTaskManager inMemoryTaskManager) {
        if (this.inMemoryTaskManager == null) {
            this.inMemoryTaskManager = inMemoryTaskManager;
        }
    }

    protected void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    protected void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected void setDuration(Duration duration) {
        this.duration = duration;
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
        //id,type,name,status,description,duration,startTime,endTime
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                this.id, this.type.toString(), this.name, this.status.toString(), this.description,
                this.duration.toString(), this.startTime.toString(), this.endTime.toString());
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

    @Override
    public int compareTo(Task o) {
        return this.startTime.compareTo(o.startTime);
    }
}
