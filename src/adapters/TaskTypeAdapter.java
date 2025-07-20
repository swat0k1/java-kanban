package adapters;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTypeAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {

    private InMemoryTaskManager taskManager;

    public TaskTypeAdapter(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("description", task.getDescription());
        jsonObject.addProperty("type", task.getType().toString());
        jsonObject.addProperty("status", task.getStatus().toString());
        jsonObject.addProperty("duration", task.getDuration().toMinutes());
        jsonObject.addProperty("startTime", task.getStartTime().toString());
        jsonObject.addProperty("endTime", task.getEndTime().toString());
        return jsonObject;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean bodyContainsID = jsonObject.get("id") != null;

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
        long durationMinutes = jsonObject.get("duration").getAsLong();
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
        LocalDateTime endTime = LocalDateTime.parse(jsonObject.get("endTime").getAsString());
        Task task;
        if (bodyContainsID) {
            int id = jsonObject.get("id").getAsInt();
            task = new Task(taskManager, id, name, description, TaskType.TASK, status, Duration.ofMinutes(durationMinutes), startTime, endTime);
        } else {
            task = new Task(taskManager, taskManager.getIDCounter(), name, description, TaskType.TASK, status, Duration.ofMinutes(durationMinutes), startTime, endTime);
        }

        return task;
    }
}