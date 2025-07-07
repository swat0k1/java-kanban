package model;

import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private int epicTaskID;

    // Конструктор для создания сабтаски с указанием эпика без времени исполнения
    public SubTask(String taskName, String taskDescription, int epicTask) {
        super(taskName, taskDescription, TaskType.SUBTASK);
        checkEpicTaskID(epicTask);
    }

    // Конструктор для создания сабтаски с эпиком и с временем исполнения
    public SubTask(String taskName, String taskDescription, int epicTask, int duration, LocalDateTime startTime) {
        super(taskName, taskDescription, TaskType.SUBTASK, duration, startTime);
        checkEpicTaskID(epicTask);
    }

    // Конструктор для создания сабтаски при загрузке из файла c временем исполнения
    public SubTask(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription, TaskStatus status, int epicTask,
                   Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(inMemoryTaskManager, id, taskName, taskDescription, TaskType.SUBTASK, status, duration, startTime, endTime);
        checkEpicTaskID(epicTask);
    }

    public EpicTask getEpicTask() {
        if (this.inMemoryTaskManager.isEpicContainsID(epicTaskID)) {
            return (EpicTask) inMemoryTaskManager.getEpicTaskHashMap().get(epicTaskID);
        }
        return null;
    }

    public int getEpicTaskID() {
        return epicTaskID;
    }

    private void checkEpicTaskID(int epicTaskID) {
        if (epicTaskID != this.getId()) {
            this.epicTaskID = epicTaskID;
        } else {
            System.out.println("Сабтаску нельзя сделать своим же эпиком!");
            this.epicTaskID = -1;
        }
    }

    @Override
    public void changeTaskStatus(TaskStatus taskStatus) {
        super.changeTaskStatus(taskStatus);
        getEpicTask().updateEpicTaskStatus(this.inMemoryTaskManager);
    }

    @Override
    public String getStringValueOfTask() {
        //id,type,name,status,description,duration,startTime,endTime,epicID
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d",
                getId(), getType().toString(), getName(), getStatus().toString(), getDescription(),
                this.getDuration().toString(), this.getStartTime().toString(), this.calculateEndTime().toString(),
                getEpicTaskID());
    }

    @Override
    public String toString() {
        return "model.SubTask{" +
                "taskName='" + this.getName() + '\'' +
                ", taskDescription='" + this.getDescription() + '\'' +
                ", taskType=" + this.getType() +
                ", taskStatus=" + this.getStatus() +
                ", taskID='" + this.getId() + '\'' +
                ", epicTaskID=" + epicTaskID +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SubTask subTask = (SubTask) object;
        return epicTaskID == subTask.epicTaskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskID);
    }
}
