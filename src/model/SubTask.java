package model;

import manager.*;
import java.util.Objects;

public class SubTask extends Task {

    private final int epicTaskID;

    public SubTask(String taskName, String taskDescription, TaskType taskType, int epicTask) {
        super(taskName, taskDescription, taskType);
        this.epicTaskID = epicTask;
    }

    public EpicTask getEpicTask(TaskManager taskManager) {
        EpicTask epicTask = taskManager.getEpicTask(epicTaskID);
        return epicTask;
    }

    public void changeTaskStatus(TaskStatus taskStatus, TaskManager taskManager) {
            super.changeTaskStatus(taskStatus);
            getEpicTask(taskManager).updateEpicTaskStatus(taskManager);
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
