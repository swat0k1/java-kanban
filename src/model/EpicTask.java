package model;

import interfaces.TaskManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class EpicTask extends Task {

    private ArrayList<Integer> subTasksID;

    public EpicTask(String taskName, String taskDescription, TaskType taskType) {
        super(taskName, taskDescription, taskType);
        this.subTasksID = new ArrayList<>();
    }

    // Конструктор для создания таски при загрузке из файла
    public EpicTask(int id, String taskName, String taskDescription, TaskType taskType, TaskStatus status) {
        super(id, taskName, taskDescription, taskType, status);
        this.subTasksID = new ArrayList<>();
    }

    public void addSubTask(Integer subtask) {
        if (subtask.equals(this.getId())) {
            System.out.println("Эпик нельзя добавить в самого себя в виде задачи!");
            return;
        }

        if (!subTasksID.contains(subtask)) {
            subTasksID.add(subtask);
        }
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

    public void updateEpicTaskStatus(TaskManager inMemoryTaskManager) {
        boolean subTaskDone = false;
        boolean subTaskInProgress = false;
        boolean subTaskNew = false;

        for (Integer subTask : subTasksID) {
            if (inMemoryTaskManager.getTask(subTask).getStatus().equals(TaskStatus.DONE)) {
                subTaskDone = true;
            } else if (inMemoryTaskManager.getTask(subTask).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                subTaskInProgress = true;
            } else if (inMemoryTaskManager.getTask(subTask).getStatus().equals(TaskStatus.NEW)) {
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
    public String getStringValueOfTask() {
        ArrayList<Integer> subTasks = getSubTasks();
        StringBuilder subTasksString = new StringBuilder();
        Collections.sort(subTasks);
        for (Integer subTaskID : subTasks) {
            subTasksString.append(",").append(subTaskID);
        }
        //id,type,name,status,description,subTasksIDs...
        return String.format("%d,%s,%s,%s,%s%s",
                getId(), getType().toString(), getName(), getStatus().toString(), getDescription(), subTasksString);
    }

    @Override
    public void changeTaskStatus(TaskStatus taskStatus) {
        System.out.println("Менять статус эпика в ручную нельзя");
    }

    private void updateEpicTaskStatus(TaskStatus taskStatus) {
        super.changeTaskStatus(taskStatus);
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
