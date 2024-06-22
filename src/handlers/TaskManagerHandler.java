package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;
import server.HttpTaskServer;
import service.TaskManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManagerHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskManagerHandler(TaskManager manager) {
        this.manager = manager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        String path = httpExchange.getRequestURI().getPath();

        String method = httpExchange.getRequestMethod();

        String[] pathParts = path.split("/");

        switch (pathParts[1]) {
            case "tasks":
                handleTasks(httpExchange);
                break;
            case "subtasks":
                handleSubtasks(httpExchange);
                break;
            case "epics":
                handleEpics(httpExchange);
                break;
            case "history":
                handleHistory(httpExchange);
                break;
            case "prioritized":
                handlePrioritizedTasks(httpExchange);
                break;
            default:
                break;
        }


    }

    public void handleGetTaskById(HttpExchange httpExchange) {
        long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        Optional<Task> optTask = manager.getTask(id);
        if (optTask.isEmpty()) {
            writeResponse(httpExchange, "Такой задачи не существует", 404);
        } else {
            writeResponse(httpExchange, gson.toJson(optTask.get()), 200);
        }
    }

    public void handleGetEpicById(HttpExchange httpExchange) {
        long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        Optional<Epic> optEpic = manager.getEpic(id);
        if (optEpic.isEmpty()) {
            writeResponse(httpExchange, "Такого эпика не существует", 404);
        } else {
            String response = gson.toJson(optEpic.get());
            writeResponse(httpExchange, gson.toJson(optEpic.get()), 200);
        }
    }

    public void handleGetSubtaskById(HttpExchange httpExchange) {
        long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        Optional<Subtask> optSub = manager.getSubTask(id);
        if (optSub.isEmpty()) {
            writeResponse(httpExchange, "Такой подзадачи не существует", 404);
        } else {
            writeResponse(httpExchange, gson.toJson(optSub.get()), 200);
        }
    }


    public void handlePostTasks(HttpExchange httpExchange) {

    }

    public void handlePrioritizedTasks(HttpExchange httpExchange) {
        if (httpExchange.getRequestMethod().equals("GET")) {
            Gson gson = HttpTaskServer.getGson();
            List<Task> prioritized = manager.getPrioritizedTasks();
            String response = gson.toJson(prioritized);
            writeResponse(httpExchange, response, 200);
        } else {
            writeResponse(httpExchange, "Такого метода не существует", 400);
        }
    }

    public void handleHistory(HttpExchange httpExchange) {
        if (httpExchange.getRequestMethod().equals("GET")) {
            Gson gson = HttpTaskServer.getGson();
            List<Task> history = manager.getHistory();
            String response = gson.toJson(history);
            writeResponse(httpExchange, response, 200);
        } else {
            writeResponse(httpExchange, "Такого метода не существует", 400);
        }
    }

    public void handleTasks(HttpExchange httpExchange) {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (httpExchange.getRequestURI().getPath().split("/").length == 2) {
                    Map<Long, Task> tasksMap = manager.getTasks();
                    String response = gson.toJson(tasksMap);
                    writeResponse(httpExchange, response, 200);
                } else {
                    handleGetTaskById(httpExchange);
                }
                break;
            case "POST":
                handlePostTasks(httpExchange);
                break;
            case "DELETE":
                long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
                manager.removeTask(id);
                writeResponse(httpExchange, "Задача удалена", 200);
                break;
            default:
                writeResponse(httpExchange, "Такого метода не существует", 400);
                break;
        }
    }

    public void handleEpics(HttpExchange httpExchange) {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (httpExchange.getRequestURI().getPath().split("/").length == 2) {
                    Map<Long, Epic> epicMap = manager.getEpics();
                    String response = gson.toJson(epicMap);
                    writeResponse(httpExchange, response, 200);
                } else {
                    handleGetEpicById(httpExchange);
                }
                break;
            case "POST":
                break;
            case "DELETE":
                long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
                manager.removeEpicTask(id);
                writeResponse(httpExchange, "Эпик удалён", 200);
                break;
            default:
                writeResponse(httpExchange, "Такого метода не существует", 400);
                break;
        }
    }

    public void handleSubtasks(HttpExchange httpExchange) {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (httpExchange.getRequestURI().getPath().split("/").length == 2) {
                    writeResponse(httpExchange, gson.toJson(manager.getSubtasks()), 200);
                } else {
                    handleGetSubtaskById(httpExchange);
                }
                break;
            case "POST":
                break;
            case "DELETE":
                long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
                manager.removeSubTask(id);
                writeResponse(httpExchange, "Подзадача удалена", 200);
                break;
            default:
                writeResponse(httpExchange, "Такого метода не существует", 400);
                break;
        }
    }

}
