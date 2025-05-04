package model;

import manager.*;
import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task{

    private final ArrayList<Integer> subTasksID;

    public EpicTask(String taskName, String taskDescription, TaskType taskType) {
        super(taskName, taskDescription, taskType);
        this.subTasksID = new ArrayList<>();
    }

    public void addSubTask(Integer subtask) {
        subTasksID.add(subtask);
    }

    public void removeSubTask(Integer subtask) {
        subTasksID.remove(subtask);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasksID;
    }

    public void clearSubTasksList() {
        subTasksID.clear();
    }

    @Override
    public void changeTaskStatus(TaskStatus taskStatus) {
        System.out.println("Менять статус эпика в ручную нельзя");
    }

    private void updateEpicTaskStatus(TaskStatus taskStatus) {
        super.changeTaskStatus(taskStatus);
    }

    public void updateEpicTaskStatus(TaskManager taskManager) {
        boolean subTaskDone = false;
        boolean subTaskInProgress = false;
        boolean subTaskNew = false;

        for (Integer subTask : subTasksID) {
            if (taskManager.getSubTask(subTask).getStatus().equals(TaskStatus.DONE)) {
                subTaskDone = true;
            } else if (taskManager.getSubTask(subTask).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                subTaskInProgress = true;
            } else if (taskManager.getSubTask(subTask).getStatus().equals(TaskStatus.NEW)) {
                subTaskNew = true;
            }
        }

        if (subTaskDone && !subTaskInProgress && !subTaskNew) {
            this.updateEpicTaskStatus(TaskStatus.DONE);
        } else if (subTaskNew && !subTaskInProgress && !subTaskDone) {
            this.updateEpicTaskStatus(TaskStatus.NEW);
        } else if (!subTaskDone && !subTaskNew && !subTaskInProgress) {
            this.updateEpicTaskStatus(TaskStatus.NEW);
        } else {
            this.updateEpicTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "model.EpicTask{" +
                "taskName='" + this.getName() + '\'' +
                ", taskDescription='" + this.getDescription() + '\'' +
                ", taskType=" + this.getType() +
                ", taskStatus=" + this.getStatus() +
                ", taskID='" + this.getId() + '\'' +
                ", subTasks=" + subTasksID +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        EpicTask epicTask = (EpicTask) object;
        return Objects.equals(subTasksID, epicTask.subTasksID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksID);
    }
}
