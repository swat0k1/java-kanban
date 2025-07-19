package handlers;

import adapters.IntegerArrayTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;

public class PrioritizedHandler extends BaseHandler implements HttpHandler {

    public PrioritizedHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_PRIORITIZED_TASKS -> handleGetPrioritized(exchange);
                default -> sendServerError(exchange);
            }
        } catch (NumberFormatException e) {
            sendServerError(exchange);
            e.printStackTrace();
        } catch (Exception e) {
            sendServerError(exchange);
            e.printStackTrace();
        }
    }

    public void handleGetPrioritized(HttpExchange exchange) throws IOException {
        ArrayList<Task> tasks = getTaskManager().getPrioritizedTasks();
        ArrayList<Integer> tasksID = new ArrayList<>();

        for (Task task : tasks) {
            tasksID.add(task.getId());
        }

        Gson gson = new GsonBuilder().
                registerTypeAdapter(ArrayList.class, new IntegerArrayTypeAdapter()).
                setPrettyPrinting().
                create();

        String serializedTasks = gson.toJson(tasksID);
        sendText(exchange, serializedTasks, 200);
    }

}
