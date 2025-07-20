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

public class HttpSubTaskManagerTasksTest {

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
        EpicTask epicTask = new EpicTask("Epic1", "");
        manager.createTask(epicTask);
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
    public void testAddSubTask() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTwoSubTasks() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:15\",\n" +
                "    \"endTime\": \"2025-07-19T09:20\"\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask4", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTwoSubTasks() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:15\",\n" +
                "    \"endTime\": \"2025-07-19T09:20\"\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String correctResponse = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"type\": \"SUBTASK\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"type\": \"SUBTASK\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:15\",\n" +
                "    \"endTime\": \"2025-07-19T09:20\"\n" +
                "  }\n" +
                "]";

        Assertions.assertTrue(response.body().equals(correctResponse));
    }

    @Test
    public void testGetOneSubTask() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String correctResponse = "{\n" +
                "  \"id\": 1,\n" +
                "  \"epicTaskID\": 0,\n" +
                "  \"name\": \"subtask3\",\n" +
                "  \"description\": \"\",\n" +
                "  \"type\": \"SUBTASK\",\n" +
                "  \"status\": \"NEW\",\n" +
                "  \"duration\": 5,\n" +
                "  \"startTime\": \"2025-07-19T09:10\",\n" +
                "  \"endTime\": \"2025-07-19T09:15\"\n" +
                "}";

        Assertions.assertTrue(response.body().equals(correctResponse));
    }

    @Test
    public void testGetNotListedSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testTimeIntersectionCreation() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:14\",\n" +
                "    \"endTime\": \"2025-07-19T09:19\"\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testTimeIntersectionUpdate() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:15\",\n" +
                "    \"endTime\": \"2025-07-19T09:20\"\n" +
                "    }";


        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask4", tasksFromManager.get(1).getName(), "Некорректное имя задачи");

        taskJson = "{\n" +
                "    \"id\": 1,\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask4\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:15\",\n" +
                "    \"endTime\": \"2025-07-19T09:20\"\n" +
                "    }";

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {

        String taskJson = "{\n" +
                "    \"epicTaskID\": 0,\n" +
                "    \"name\": \"subtask3\",\n" +
                "    \"description\": \"\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"duration\": 5,\n" +
                "    \"startTime\": \"2025-07-19T09:10\",\n" +
                "    \"endTime\": \"2025-07-19T09:15\"\n" +
                "    }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        url = URI.create("http://localhost:8080/subtasks/0");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void testUpdateNotListedSubTask() throws IOException, InterruptedException {

        SubTask task = new SubTask(manager, 1, "Test 2", "Testing task 2",
                            TaskStatus.NEW, 0, Duration.ofMinutes(5),
                            LocalDateTime.of(2025, 7, 19, 12, 0, 0),
                            LocalDateTime.of(2025, 7, 19, 12, 5, 0));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<SubTask> tasksFromManager = manager.getTaskList(TaskType.SUBTASK);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
        assertThrows(IndexOutOfBoundsException.class, () -> tasksFromManager.get(0).getName());
    }

}

