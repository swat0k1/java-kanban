package handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.InMemoryTaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

abstract class BaseHandler {

    private InMemoryTaskManager taskManager;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final String ID_FORMAT_1 = "\"id\":";
    protected static final String ID_FORMAT_2 = "\"id\" :";

    protected BaseHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected InMemoryTaskManager getTaskManager() {
        return taskManager;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String text = "Not found";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String text = "Task Interaction";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
        InMemoryTaskManager.setIDCounter(InMemoryTaskManager.getIDCounter() - 1);
    }

    protected void sendServerError(HttpExchange exchange) throws IOException {
        String text = "Server error";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected Endpoint getEndpoint(HttpExchange exchange) throws IOException {
        String exchangePath = exchange.getRequestURI().getPath();
        String exchangeMethod = exchange.getRequestMethod();
        String[] pathParts = exchangePath.split("/");
        int pathLength= pathParts.length;

        if (pathLength == 2 && pathParts[1].equals("tasks")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (exchangeMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
        } else if (pathLength == 3 && pathParts[1].equals("tasks")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            } else if (exchangeMethod.equals("DELETE")) {
                return Endpoint.DELETE_DELETE_TASK;
            }
        }

        if (pathLength == 2 && pathParts[1].equals("subtasks")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            } else if (exchangeMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;

            }
        } else if (pathLength == 3 && pathParts[1].equals("subtasks")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK_BY_ID;
            } else if (exchangeMethod.equals("DELETE")) {
                return Endpoint.DELETE_DELETE_SUBTASK;
            } else if (exchangeMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;
            }
        }

        if (pathLength == 2 && pathParts[1].equals("epics")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            } else if (exchangeMethod.equals("POST")) {
                return Endpoint.POST_EPIC;
            }
        } else if (pathLength == 3 && pathParts[1].equals("epics")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_EPIC_BY_ID;
            } else if (exchangeMethod.equals("DELETE")) {
                return Endpoint.DELETE_DELETE_EPIC;
            }
        } else if (pathLength == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }

        if (pathLength == 2 && pathParts[1].equals("history")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        }

        if (pathLength == 2 && pathParts[1].equals("prioritized")) {
            if (exchangeMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
        }

        return Endpoint.UNKNOWN;
    }
}
