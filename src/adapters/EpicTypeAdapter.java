package adapters;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import model.EpicTask;
import model.TaskStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EpicTypeAdapter implements JsonSerializer<EpicTask>, JsonDeserializer<EpicTask> {

    private InMemoryTaskManager taskManager;

    public EpicTypeAdapter(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public JsonElement serialize(EpicTask epicTask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", epicTask.getId());
        jsonObject.addProperty("name", epicTask.getName());
        jsonObject.addProperty("description", epicTask.getDescription());
        jsonObject.addProperty("type", epicTask.getType().toString());
        jsonObject.addProperty("status", epicTask.getStatus().toString());
        jsonObject.addProperty("duration", epicTask.getDuration().toMinutes());
        jsonObject.addProperty("startTime", epicTask.getStartTime().toString());
        jsonObject.addProperty("endTime", epicTask.getEndTime().toString());

        JsonArray subTasksArray = new JsonArray();
        for (Integer id : epicTask.getSubTasks()) {
            subTasksArray.add(id);
        }
        jsonObject.add("subTasksID", subTasksArray);

        return jsonObject;
    }

    @Override
    public EpicTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean bodyContainsID = jsonObject.get("id") != null;

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        ArrayList<Integer> subTasksID = new ArrayList<>();
        JsonArray subTasksArray = jsonObject.getAsJsonArray("subTasksID");
        for (JsonElement element : subTasksArray) {
            subTasksID.add(element.getAsInt());
        }

        EpicTask epicTask;
        if (bodyContainsID) {
            int id = jsonObject.get("id").getAsInt();
            epicTask = new EpicTask(taskManager, id, name, description, TaskStatus.NEW, subTasksID);
        } else {
            epicTask = new EpicTask(taskManager, taskManager.getIDCounter(), name, description, TaskStatus.NEW,
                    subTasksID);
        }

        return epicTask;
    }
}