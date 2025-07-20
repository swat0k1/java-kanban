package adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class IntegerArrayTypeAdapter implements JsonSerializer<ArrayList<Integer>>, JsonDeserializer<ArrayList<Integer>> {

    @Override
    public JsonElement serialize(ArrayList<Integer> tasks, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        JsonArray tasksArray = new JsonArray();
        for (Integer id : tasks) {
            tasksArray.add(id);
        }
        jsonObject.add("tasksID", tasksArray);

        return jsonObject;
    }

    @Override
    public ArrayList<Integer> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }
}