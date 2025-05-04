import java.util.ArrayList;

public class EpicTask extends Task{

    private final ArrayList<Subtask> subtasks;

    public EpicTask(String taskName, String taskDescription, TaskType taskType) {
        super(taskName, taskDescription, taskType);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void clearSubtasksList() {
        subtasks.clear();
    }

    @Override
    public void changeTaskStatus(TaskStatus taskStatus) {
        System.out.println("Менять статус эпика в ручную нельзя");
    }

    private void updateEpicTaskStatus(TaskStatus taskStatus) {
        super.changeTaskStatus(taskStatus);
    }

    public void updateEpicTaskStatus() {
        boolean subtaskDone = false;
        boolean subtaskInProgress = false;
        boolean subtaskNew = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskStatus().equals(TaskStatus.DONE)) {
                subtaskDone = true;
            } else if (subtask.getTaskStatus().equals(TaskStatus.IN_PROGRESS)) {
                subtaskInProgress = true;
            } else if (subtask.getTaskStatus().equals(TaskStatus.NEW)) {
                subtaskNew = true;
            }
        }

        if (subtaskDone && !subtaskInProgress && !subtaskNew) {
            this.updateEpicTaskStatus(TaskStatus.DONE);
        } else if (subtaskNew && !subtaskInProgress && !subtaskDone) {
            this.updateEpicTaskStatus(TaskStatus.NEW);
        } else if (!subtaskDone && !subtaskNew && !subtaskInProgress) {
            this.updateEpicTaskStatus(TaskStatus.NEW);
        } else {
            this.updateEpicTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskType=" + this.getTaskType() +
                ", taskStatus=" + this.getTaskStatus() +
                ", taskUUID='" + this.getTaskUUID() + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }

}
