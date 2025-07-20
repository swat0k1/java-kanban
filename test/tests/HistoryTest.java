package tests;

import adapters.EpicTypeAdapter;
import adapters.IntegerArrayTypeAdapter;
import adapters.SubTaskTypeAdapter;
import adapters.TaskTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.InMemoryTaskManager;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryTest {

    InMemoryTaskManager manager;
    HttpServer httpServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskTypeAdapter(manager))
            .registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter(manager))
            .registerTypeAdapter(EpicTask.class, new EpicTypeAdapter(manager))
            .registerTypeAdapter(ArrayList.class, new IntegerArrayTypeAdapter())
            .setPrettyPrinting()
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        InMemoryTaskManager.setIDCounter(0);
        manager = new InMemoryTaskManager();
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/task", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubTaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        manager.removeAllTasks(TaskType.TASK);
        manager.removeAllTasks(TaskType.EPIC);
        manager.removeAllTasks(TaskType.SUBTASK);
        EpicTask epicTask0 = new EpicTask("Epic0", "");
        EpicTask epicTask1 = new EpicTask("Epic1", "");
        SubTask subTask2 = new SubTask(manager, 2, "subtask2", "", TaskStatus.NEW, 0, Duration.ofMinutes(5),
                LocalDateTime.of(2025, 7, 19, 12, 0, 0),
                LocalDateTime.of(2025, 7, 19, 12, 5, 0));
        SubTask subTask3 = new SubTask(manager, 3, "subtask3", "", TaskStatus.NEW, 0, Duration.ofMinutes(5),
                LocalDateTime.of(2025, 7, 19, 12, 5, 0),
                LocalDateTime.of(2025, 7, 19, 12, 10, 0));
        Task task4 = new Task("Task4", "");
        Task task5 = new Task("Task5", "");
        Task task6 = new Task("Task6", "");
        manager.createTask(epicTask0);
        manager.createTask(epicTask1);
        manager.createTask(subTask2);
        manager.createTask(subTask3);
        manager.createTask(task4);
        manager.createTask(task5);
        manager.createTask(task6);
        httpServer.start();
    }

    @AfterEach
    public void shutDown(){
        manager.removeAllTasks(TaskType.TASK);
        manager.removeAllTasks(TaskType.EPIC);
        manager.removeAllTasks(TaskType.SUBTASK);
        InMemoryTaskManager.setIDCounter(0);
        httpServer.stop(0);
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String correctResponse = "{\n" +
                "  \"tasksID\": []\n" +
                "}";
        assertEquals(200, response.statusCode());
        assertEquals(correctResponse, response.body());

        manager.getTask(0);
        manager.getTask(2);
        manager.getTask(4);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        correctResponse = "{\n" +
                "  \"tasksID\": [\n" +
                "    4,\n" +
                "    2,\n" +
                "    0\n" +
                "  ]\n" +
                "}";
        assertEquals(200, response.statusCode());
        assertEquals(correctResponse, response.body());
    }

}

