package adapters;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import model.SubTask;
import model.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTaskTypeAdapter implements JsonSerializer<SubTask>, JsonDeserializer<SubTask> {

    private InMemoryTaskManager taskManager;

    public SubTaskTypeAdapter(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public JsonElement serialize(SubTask subTask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", subTask.getId());
        jsonObject.addProperty("epicTaskID", subTask.getEpicTaskID());
        jsonObject.addProperty("name", subTask.getName());
        jsonObject.addProperty("description", subTask.getDescription());
        jsonObject.addProperty("type", subTask.getType().toString());
        jsonObject.addProperty("status", subTask.getStatus().toString());
        jsonObject.addProperty("duration", subTask.getDuration().toMinutes());
        jsonObject.addProperty("startTime", subTask.getStartTime().toString());
        jsonObject.addProperty("endTime", subTask.getEndTime().toString());
        return jsonObject;
    }

    @Override
    public SubTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean bodyContainsID = jsonObject.get("id") != null;

        int epicTaskID = jsonObject.get("epicTaskID").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
        long durationMinutes = jsonObject.get("duration").getAsLong();
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
        LocalDateTime endTime = LocalDateTime.parse(jsonObject.get("endTime").getAsString());
        SubTask subTask;
        if (bodyContainsID) {
            int id = jsonObject.get("id").getAsInt();
            subTask = new SubTask(taskManager, id, name, description, status, epicTaskID,
                                    Duration.ofMinutes(durationMinutes), startTime, endTime);
        } else {
            subTask = new SubTask(taskManager, taskManager.getIDCounter(), name, description, status, epicTaskID,
                                        Duration.ofMinutes(durationMinutes), startTime, endTime);
        }

        return subTask;
    }
}