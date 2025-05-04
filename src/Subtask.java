public class Subtask extends Task {

    private final EpicTask epicTask;

    public Subtask(String taskName, String taskDescription, TaskType taskType, EpicTask epicTask) {
        super(taskName, taskDescription, taskType);
        this.epicTask = epicTask;
    }

    public EpicTask getEpicTask() {
        return this.epicTask;
    }

    @Override
    public void changeTaskStatus(TaskStatus taskStatus) {
            super.changeTaskStatus(taskStatus);
            if (epicTask != null) {
                epicTask.updateEpicTaskStatus();
            }
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskType=" + this.getTaskType() +
                ", taskStatus=" + this.getTaskStatus() +
                ", taskUUID='" + this.getTaskUUID() + '\'' +
                ", epicTaskUUID=" + epicTask.getTaskUUID() +
                ", epicTaskName=" + epicTask.getTaskName() +
                '}';
    }

}
