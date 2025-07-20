import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.InMemoryTaskManager;
import manager.Managers;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {

    private static final int DEFAULT_PORT = 8080;
    private static final String workindDir = Paths.get("").toAbsolutePath().toString();
    private static final String dataFilePath = "\\run\\data.csv";

    public static void main(String[] args) throws IOException {

        File file = new File(workindDir + dataFilePath);
        Managers managers = new Managers();
        InMemoryTaskManager inMemoryTaskManager = managers.getBackedFromFile(file);

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);
        httpServer.createContext("/task", new TaskHandler(inMemoryTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(inMemoryTaskManager));
        httpServer.createContext("/epics", new EpicHandler(inMemoryTaskManager));
        httpServer.createContext("/history", new HistoryHandler(inMemoryTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(inMemoryTaskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + DEFAULT_PORT + " порту!");

    }

}

