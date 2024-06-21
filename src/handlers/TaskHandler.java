package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import server.HttpTaskServer;
import service.TaskManager;

import java.util.Arrays;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        String path = httpExchange.getRequestURI().getPath();

        String method = httpExchange.getRequestMethod();

        String[] pathParts = path.split("/");

        if (pathParts.length == 3) {
            if (method.equals("GET")) {
                handleGetTaskById(httpExchange);
            } else if (method.equals("DELETE")) {
                handleDeleteTaskById(httpExchange);
            }
        } else if (pathParts.length == 2) {
            if (method.equals("GET")) {
                handleGetTasks(httpExchange);
            } else if (method.equals("POST")) {
                handlePostTasks(httpExchange);
            }
        }

    }

    public void handleGetTaskById(HttpExchange httpExchange) {
        long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        Optional<Task> optTask = manager.getTask(id);
        if (optTask.isEmpty()) {
            writeResponse(httpExchange, "Такой задачи не существует", 404);
        } else {
            Gson gson = HttpTaskServer.getGson();
            String response = gson.toJson(optTask.get());
            writeResponse(httpExchange, response, 200);
        }
    }

    public void handleDeleteTaskById(HttpExchange httpExchange) {
        long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[3]);
        manager.removeTask(id);
    }

    public void handleGetTasks(HttpExchange httpExchange) {

    }

    public void handlePostTasks(HttpExchange httpExchange) {
        String[] queryString = httpExchange.getRequestURI().getQuery().split("&");
        System.out.println(Arrays.toString(queryString));
    }
}
