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
import org.junit.jupiter.api.Assertions;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicTasksTest {

    InMemoryTaskManager manager;
    HttpServer httpServer;
    Gson gson = new GsonBuilder().
            registerTypeAdapter(Task.class, new TaskTypeAdapter(manager)).
            registerTypeAdapter(SubTask.class, new SubTaskTypeAdapter(manager)).
            registerTypeAdapter(EpicTask.class, new EpicTypeAdapter(manager)).
            registerTypeAdapter(ArrayList.class, new IntegerArrayTypeAdapter()).
            setPrettyPrinting().
            create();

    @BeforeEach
    public void setUp() throws IOException {
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
        SubTask subTask2 = new SubTask(manager, 2, "subtask2", "", TaskStatus.NEW, 0, Duration.ofMinutes(5),
                LocalDateTime.of(2025, 7, 19, 12, 0, 0),
                LocalDateTime.of(2025, 7, 19, 12, 5, 0));
        SubTask subTask3 = new SubTask(manager, 3, "subtask3", "", TaskStatus.NEW, 0, Duration.ofMinutes(5),
                LocalDateTime.of(2025, 7, 19, 12, 5, 0),
                LocalDateTime.of(2025, 7, 19, 12, 10, 0));
        manager.createTask(subTask2);
        manager.createTask(subTask3);
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
    public void testAddEpicTask() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": [2,3]\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epictask0", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTwoEpicTasks() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": [2,3]\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epictask0", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        taskJson = "{\n" +
                "    \"name\": \"epictask1\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": []\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epictask1", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTwoEpicTasks() throws IOException, InterruptedException {

        InMemoryTaskManager.setIDCounter(0);
        String taskJson = "{\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": [2,3]\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        taskJson = "{\n" +
                "    \"name\": \"epictask1\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": []\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String correctResponse = "[\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"type\": \"EPIC\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 10,\n" +
                "    \"startTime\": \"2025-07-19T12:00\",\n" +
                "    \"endTime\": \"2025-07-19T12:10\",\n" +
                "    \"subTasksID\": [\n" +
                "      2,\n" +
                "      3\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"epictask1\",\n" +
                "    \"description\": \"\",\n" +
                "    \"type\": \"EPIC\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 0,\n" +
                "    \"startTime\": \"-999999999-01-01T00:00\",\n" +
                "    \"endTime\": \"-999999999-01-01T00:00\",\n" +
                "    \"subTasksID\": []\n" +
                "  }\n" +
                "]";

        Assertions.assertTrue(response.body().equals(correctResponse));
    }

    @Test
    public void testGetOneEpicTask() throws IOException, InterruptedException {

        InMemoryTaskManager.setIDCounter(0);
        String taskJson = "{\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": [2,3]\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/epics/0");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String correctResponse = "{\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"epictask0\",\n" +
                "  \"description\": \"\",\n" +
                "  \"type\": \"EPIC\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": 10,\n" +
                "  \"startTime\": \"2025-07-19T12:00\",\n" +
                "  \"endTime\": \"2025-07-19T12:10\",\n" +
                "  \"subTasksID\": [\n" +
                "    2,\n" +
                "    3\n" +
                "  ]\n" +
                "}";

        Assertions.assertTrue(response.body().equals(correctResponse));
    }

    @Test
    public void testGetNotListedEpicTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetNotListedEpicTasksSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteEpicTask() throws IOException, InterruptedException {

        InMemoryTaskManager.setIDCounter(0);
        String taskJson = "{\n" +
                "    \"name\": \"epictask0\",\n" +
                "    \"description\": \"\",\n" +
                "    \"subTasksID\": [2,3]\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epictask0", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        url = URI.create("http://localhost:8080/epics/0");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testUpdateNotListedEpicTask() throws IOException, InterruptedException {

        ArrayList<Integer> subTasksID = new ArrayList<>();

        EpicTask task = new EpicTask(manager, 1, "Test 2", "Testing task 2", TaskStatus.NEW, subTasksID);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.EPIC);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName());
    }

}

