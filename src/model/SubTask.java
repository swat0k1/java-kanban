package model;

import interfaces.TaskManager;
import java.util.Objects;

public class SubTask extends Task {

    private final int epicTaskID;

    public SubTask(String taskName, String taskDescription, TaskType taskType, int epicTask) {
        super(taskName, taskDescription, taskType);
        if (epicTask != this.getId()) {
            this.epicTaskID = epicTask;
        } else {
            System.out.println("Сабтаску нельзя сделать своим же эпиком!");
            this.epicTaskID = -1;
        }
    }

    // Конструктор для создания таски при загрузке из файла
    public SubTask(int id, String taskName, String taskDescription, TaskType taskType, TaskStatus status, int epicTask) {
        super(id, taskName, taskDescription, taskType, status);
        if (epicTask != this.getId()) {
            this.epicTaskID = epicTask;
        } else {
            System.out.println("Сабтаску нельзя сделать своим же эпиком!");
            this.epicTaskID = -1;
        }
    }

    public EpicTask getEpicTask(TaskManager inMemoryTaskManager) {
        if (inMemoryTaskManager.isEpicContainsID(epicTaskID)) {
            return (EpicTask) inMemoryTaskManager.getEpicTaskHashMap().get(epicTaskID);
        }
        return null;
    }

    public int getEpicTaskID() {
        return epicTaskID;
    }

    public void changeTaskStatus(TaskStatus taskStatus, TaskManager inMemoryTaskManager) {
            super.changeTaskStatus(taskStatus);
            getEpicTask(inMemoryTaskManager).updateEpicTaskStatus(inMemoryTaskManager);
    }

    @Override
    public String getStringValueOfTask() {
        //id,type,name,status,description,epicID
        return String.format("%d,%s,%s,%s,%s,%d",
                getId(), getType().toString(), getName(), getStatus().toString(), getDescription(), getEpicTaskID());
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
