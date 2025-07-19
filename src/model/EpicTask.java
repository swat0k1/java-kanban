package model;

import interfaces.TaskManager;
import manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class EpicTask extends Task {

    private ArrayList<Integer> subTasksID = new ArrayList<>();

    // Конструктор для создания эпика без времени исполнения
    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskType.EPIC);
        calculateTime();
    }

    // Конструктор для создания эпика при загрузке из файла c временем исполнения
    public EpicTask(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription, TaskStatus status,
                    Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(inMemoryTaskManager, id, taskName, taskDescription, TaskType.EPIC, status, duration, startTime, endTime);
    }

    public EpicTask(InMemoryTaskManager inMemoryTaskManager, int id, String taskName, String taskDescription,
                    TaskStatus taskStatus, ArrayList<Integer> subTasksID) {
        super(inMemoryTaskManager, id, taskName, taskDescription, TaskType.EPIC, taskStatus);
        this.subTasksID = subTasksID;
        calculateTime();
    }

    public void addSubTask(Integer subtask) {
        if (subtask.equals(this.getId())) {
            System.out.println("Эпик нельзя добавить в самого себя в виде задачи!");
            return;
        }

        if (!subTasksID.contains(subtask)) {
            subTasksID.add(subtask);
            calculateTime();
        }
    }

    public void removeSubTask(Integer subtask) {
        subTasksID.remove(subtask);
        calculateTime();
    }

    public void calculateTime() {

        if (subTasksID.isEmpty()) {
           this.setDuration(Duration.ZERO);
           this.setStartTime(LocalDateTime.MIN);
           this.setEndTime(LocalDateTime.MIN);
           return;
        }

        ArrayList<SubTask> subTasks = getTasksWithTime();

        LocalDateTime minStartTime = LocalDateTime.MAX;
        LocalDateTime maxEndTime = LocalDateTime.MIN;
        Duration duration = Duration.ZERO;

        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime().isBefore(minStartTime)) {
                minStartTime = subTask.getStartTime();
            }
            if (subTask.calculateEndTime().isAfter(maxEndTime)) {
                maxEndTime = subTask.calculateEndTime();
            }
            duration = duration.plus(subTask.getDuration());
        }

        this.setDuration(duration);
        this.setStartTime(minStartTime);
        this.setEndTime(maxEndTime);

    }

    private ArrayList<SubTask> getTasksWithTime() {

        return subTasksID
                .stream()
                .map(inMemoryTaskManager::getSubTaskWithoutRecord)
                .filter(Objects::nonNull)
                .filter(subTask -> !subTask.durationAndStartTimeNotSet())
                .collect(Collectors.toCollection(ArrayList::new));

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
        //id,type,name,status,description,duration,startTime,endTime,subTasksIDs...
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s%s",
                getId(), getType().toString(), getName(), getStatus().toString(), getDescription(),
                this.getDuration().toString(), this.getStartTime().toString(), this.getEndTime().toString(),
                subTasksString);
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
