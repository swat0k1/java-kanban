package handlers;

import adapters.EpicTypeAdapter;
import adapters.IntegerArrayTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import model.EpicTask;
import model.Task;
import model.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EpicHandler extends BaseHandler implements HttpHandler {

    public EpicHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_EPICS -> handleGetEpicTasks(exchange);
                case GET_EPIC_BY_ID -> handleGetEpicTask(exchange);
                case GET_EPIC_SUBTASKS -> handleGetEpicTaskSubtasks(exchange);
                case POST_EPIC -> handlePostEpicTask(exchange);
                case DELETE_DELETE_EPIC -> handleDeleteEpicTask(exchange);
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

    public void handleGetEpicTasks(HttpExchange exchange) throws IOException {

        ArrayList<EpicTask> epicTasks = getTaskManager().getTaskList(TaskType.EPIC);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EpicTask.class, new EpicTypeAdapter(getTaskManager()))
                .setPrettyPrinting()
                .create();

        String serializedTasks = gson.toJson(epicTasks);
        sendText(exchange, serializedTasks, 200);

    }

    public void handleGetEpicTask(HttpExchange exchange) throws IOException {

        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);

        Task epicTask = null;
        if (getTaskManager().isEpicContainsID(taskID)) {
            epicTask = getTaskManager().getTask(taskID);
        }

        if (epicTask != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(EpicTask.class, new EpicTypeAdapter(getTaskManager()))
                    .setPrettyPrinting()
                    .create();

            String serializedTasks = gson.toJson(epicTask);
            sendText(exchange, serializedTasks, 200);
        } else {
            sendNotFound(exchange);
        }

    }

    public void handleGetEpicTaskSubtasks(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 2]);

        EpicTask epicTask = (EpicTask) getTaskManager().getTask(taskID);

        if (epicTask != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ArrayList.class, new IntegerArrayTypeAdapter())
                    .setPrettyPrinting()
                    .create();

            String serializedTasks = gson.toJson(epicTask.getSubTasks());
            sendText(exchange, serializedTasks, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    public void handlePostEpicTask(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EpicTask.class, new EpicTypeAdapter(getTaskManager()))
                .create();
        EpicTask epicTask = gson.fromJson(body, EpicTask.class);

        int result = getTaskManager().createTask(epicTask);

        switch (result) {
            case 1 -> sendText(exchange, "EpicTask was created", 201);
            case -2 -> sendHasInteractions(exchange);
            case -1 -> sendServerError(exchange);
            default -> sendServerError(exchange);

        }

    }

    public void handleDeleteEpicTask(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String[] pathParts = exchangePath.split("/");
        int taskID = Integer.parseInt(pathParts[pathParts.length - 1]);
        getTaskManager().removeTask(taskID);
        sendText(exchange, "EpicTask was removed", 200);
    }

}
