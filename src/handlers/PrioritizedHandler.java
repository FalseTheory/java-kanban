package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import server.HttpTaskServer;
import service.TaskManager;

import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                Gson gson = HttpTaskServer.getGson();
                List<Task> prioritized = manager.getPrioritizedTasks();
                String response = gson.toJson(prioritized);
                writeResponse(httpExchange, response, 200);
                break;
            default:
                writeResponse(httpExchange, "Такого метода не существует", 400);
        }
    }

}
