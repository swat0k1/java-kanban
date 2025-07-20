package handlers;

import adapters.SubTaskTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.SubTask;
import model.Task;
import model.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SubTaskHandler extends BaseHandler implements HttpHandler {

    public SubTaskHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_SUBTASKS -> handleGetSubTasks(exchange);
                case GET_SUBTASK_BY_ID -> handleGetSubTask(exchange);
                case POST_SUBTASK -> handlePostSubTask(exchange);
                case DELETE_DELETE_SUBTASK -> handleDeleteSubTask(exchange);
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

    public void handleGetSubTasks(HttpExchange exchange) throws IOException {

        ArrayList<SubTask> subTasks = getTaskManager().getTaskList(TaskType.SUBTASK);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter(getTaskManager()))
                .setPrettyPrinting()
                .create();

        String serializedTasks = gson.toJson(subTasks);
        sendText(exchange, serializedTasks, 200);

    }

    public void handleGetSubTask(HttpExchange exchange) throws IOException {

        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);

        Task subTask = null;
        if (getTaskManager().isSubTaskContainsID(taskID)) {
            subTask = getTaskManager().getTask(taskID);
        }

        if (subTask != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter(getTaskManager()))
                    .setPrettyPrinting()
                    .create();

            String serializedTasks = gson.toJson(subTask);
            sendText(exchange, serializedTasks, 200);
        } else {
            sendNotFound(exchange);
        }

    }

    public void handlePostSubTask(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        boolean bodyContainsID = body.contains(ID_FORMAT_1) || body.contains(ID_FORMAT_2);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter(getTaskManager()))
                .create();
        SubTask subTask = gson.fromJson(body, SubTask.class);

        if (bodyContainsID) {
            int result = getTaskManager().updateTask(subTask, subTask.getId());

            switch (result) {
                case 1 -> sendText(exchange, "SubTask was updated", 201);
                case -3 -> sendNotFound(exchange);
                case -2 -> sendHasInteractions(exchange);
                case -1 -> sendServerError(exchange);
                default -> sendServerError(exchange);
            }
        } else {
            int result = getTaskManager().createTask(subTask);

            switch (result) {
                case 1 -> sendText(exchange, "SubTask was created", 201);
                case -2 -> sendHasInteractions(exchange);
                case -1 -> sendServerError(exchange);
                default -> sendServerError(exchange);
            }
        }

    }

    public void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);
        getTaskManager().removeTask(taskID);
        sendText(exchange, "SubTask was removed", 200);
    }

}
