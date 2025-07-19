package handlers;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.Task;
import model.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TaskHandler extends BaseHandler implements HttpHandler {

    public TaskHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_TASKS -> handleGetTasks(exchange);
                case GET_TASK_BY_ID -> handleGetTask(exchange);
                case POST_TASK -> handlePostTask(exchange);
                case DELETE_DELETE_TASK -> handleDeleteTask(exchange);
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

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        ArrayList<Task> tasks = getTaskManager().getTaskList(TaskType.TASK);

        Gson gson = new GsonBuilder().
                registerTypeAdapter(Task.class, new TaskTypeAdapter(getTaskManager())).
                setPrettyPrinting().
                create();

        String serializedTasks = gson.toJson(tasks);
        sendText(exchange, serializedTasks, 200);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);

        Task task = null;
        if (getTaskManager().isTaskContainsID(taskID)) {
            task = getTaskManager().getTask(taskID);
        }

        if (task != null) {
            Gson gson = new GsonBuilder().
                    registerTypeAdapter(Task.class, new TaskTypeAdapter(getTaskManager())).
                    setPrettyPrinting().
                    create();

            String serializedTasks = gson.toJson(task);
            sendText(exchange, serializedTasks, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        boolean bodyContainsID = body.contains(ID_FORMAT_1) || body.contains(ID_FORMAT_2);
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Task.class, new TaskTypeAdapter(getTaskManager())).
                create();
        Task task = gson.fromJson(body, Task.class);

        if (bodyContainsID) {
            int result = getTaskManager().updateTask(task, task.getId());

            switch (result) {
                case 1 -> sendText(exchange, "Task was updated", 201);
                case -3 -> sendNotFound(exchange);
                case -2 -> sendHasInteractions(exchange);
                case -1 -> sendServerError(exchange);
                default -> sendServerError(exchange);
            }
        } else {
            int result = getTaskManager().createTask(task);

            switch (result) {
                case 1 -> sendText(exchange, "Task was created", 201);
                case -2 -> sendHasInteractions(exchange);
                case -1 -> sendServerError(exchange);
                default -> sendServerError(exchange);
            }
        }
    }

    public void handleDeleteTask(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);
        getTaskManager().removeTask(taskID);
        sendText(exchange, "Task was removed", 200);
    }
}
