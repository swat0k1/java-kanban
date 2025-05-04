import java.util.Objects;

public class Task {
    private final String taskName;
    private final String taskDescription;
    private final TaskType taskType;
    private TaskStatus taskStatus;
    private final String taskUUID;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskType = TaskType.TASK;
        this.taskStatus = TaskStatus.NEW;
        this.taskUUID = String.valueOf(TaskManager.getUUIDCounter());
        TaskManager.incrementUUIDCounter();
    }

    public Task(String taskName, String taskDescription, TaskType taskType) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskType = taskType;
        this.taskStatus = TaskStatus.NEW;
        this.taskUUID = String.valueOf(TaskManager.getUUIDCounter());
        TaskManager.incrementUUIDCounter();
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void changeTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getSimpleDescription() {
        String result = "";

        if (this.taskType.equals(TaskType.TASK)) {
            result += "TASK: ";

        } else if (this.taskType.equals(TaskType.EPIC)) {
            result += "EPIC: ";
        } else {
            result += "SUBTASK: ";
        }

        result += this.taskName;

        if (this.taskStatus.equals(TaskStatus.NEW)) {
            result += " NEW ";
        } else if (this.taskStatus.equals(TaskStatus.IN_PROGRESS)) {
            result += " IN_PROGRESS ";
        } else {
            result += " DONE ";
        }

        result += "uuid " + this.taskUUID;

        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskType=" + taskType +
                ", taskStatus=" + taskStatus +
                ", taskUUID='" + taskUUID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return getTaskUUID() == task.getTaskUUID() && Objects.equals(getTaskName(), task.getTaskName()) &&
                Objects.equals(getTaskDescription(), task.getTaskDescription()) && getTaskType() == task.getTaskType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskName(), getTaskDescription(), getTaskType(), getTaskUUID());
    }

}
