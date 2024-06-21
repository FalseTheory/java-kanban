package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import server.HttpTaskServer;
import service.TaskManager;

import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        if (httpExchange.getRequestMethod().equals("GET")) {
            Gson gson = HttpTaskServer.getGson();
            List<Task> history = manager.getHistory();
            String response = gson.toJson(history);
            writeResponse(httpExchange, response, 200);
        } else {
            writeResponse(httpExchange, "Такого метода не существует", 400);
        }
    }
}

