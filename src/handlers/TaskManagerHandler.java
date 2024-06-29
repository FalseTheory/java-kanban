package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.BadRequestException;
import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManagerHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    public TaskManagerHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
        errorHandler = new ErrorHandler(gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();


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
        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        }


    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        long id;
        try {
            id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        } catch (NumberFormatException e) {
            errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
            return;
        }
        Optional<Task> optTask = manager.getTask(id);
        if (optTask.isEmpty()) {
            errorHandler.handle(httpExchange, new NotFoundException("Такой подзадачи не существует"));
        } else {
            writeResponse(httpExchange, gson.toJson(optTask.get()), 200);
        }


    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        long id;
        try {
            id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        } catch (NumberFormatException e) {
            errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
            return;
        }
        Optional<Epic> optEpic = manager.getEpic(id);
        if (optEpic.isEmpty()) {
            errorHandler.handle(httpExchange, new NotFoundException("Такого эпика не существует"));
        } else {
            writeResponse(httpExchange, gson.toJson(optEpic.get()), 200);
        }


    }

    private void handleGetSubtaskById(HttpExchange httpExchange) throws IOException {
        long id;
        try {
            id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        } catch (NumberFormatException e) {
            errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
            return;
        }
        Optional<Subtask> optSub = manager.getSubTask(id);
        if (optSub.isEmpty()) {
            errorHandler.handle(httpExchange, new NotFoundException("Такой подзадачи не существует"));
        } else {
            writeResponse(httpExchange, gson.toJson(optSub.get()), 200);
        }


    }


    private void handlePostTasks(HttpExchange httpExchange) {
        try (InputStream is = httpExchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null) {
                try {
                    manager.createTask(task);
                    writeResponse(httpExchange, gson.toJson(task), 201);
                } catch (ValidationException e) {
                    errorHandler.handle(httpExchange, e);
                }

            } else {
                try {
                    manager.updateTask(task);
                    writeResponse(httpExchange, gson.toJson(task) + task.getId(), 201);
                } catch (ValidationException e) {
                    errorHandler.handle(httpExchange, e);
                }

            }

        } catch (IOException e) {
            errorHandler.handle(httpExchange, e);
        }
    }

    private void handlePrioritizedTasks(HttpExchange httpExchange) throws IOException {
        int requestSplits = httpExchange.getRequestURI().getPath().split("/").length;
        if (httpExchange.getRequestMethod().equals("GET") &&
                requestSplits == 2) {
            List<Task> prioritized = manager.getPrioritizedTasks();
            String response = gson.toJson(prioritized);
            writeResponse(httpExchange, response, 200);
        } else if (requestSplits != 2) {
            writeResponse(httpExchange, "", 404);
        } else {
            writeResponse(httpExchange, "", 405);
        }

    }

    private void handleHistory(HttpExchange httpExchange) throws IOException {
        int requestSplits = httpExchange.getRequestURI().getPath().split("/").length;
        if (httpExchange.getRequestMethod().equals("GET") &&
                requestSplits == 2) {
            List<Task> history = manager.getHistory();
            String response = gson.toJson(history);
            writeResponse(httpExchange, response, 200);
        } else if (requestSplits != 2) {
            writeResponse(httpExchange, "", 404);
        } else {
            writeResponse(httpExchange, "", 405);
        }

    }

    private void handleTasks(HttpExchange httpExchange) throws IOException, NumberFormatException {
        String[] pathSplits = httpExchange.getRequestURI().getPath().split("/");
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (pathSplits.length == 2) {
                    Map<Long, Task> tasksMap = manager.getTasks();
                    String response = gson.toJson(tasksMap);
                    writeResponse(httpExchange, response, 200);
                } else if (pathSplits.length == 3) {
                    handleGetTaskById(httpExchange);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
                break;
            case "POST":
                handlePostTasks(httpExchange);
                break;
            case "DELETE":
                long id;
                try {
                    id = Long.parseLong(pathSplits[2]);
                } catch (NumberFormatException e) {
                    errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
                    return;
                }
                manager.removeTask(id);
                writeResponse(httpExchange, "", 204);


                break;
            default:
                writeResponse(httpExchange, "", 405);
                break;
        }
    }

    private void handleEpics(HttpExchange httpExchange) throws IOException {
        String[] pathSplits = httpExchange.getRequestURI().getPath().split("/");
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (pathSplits.length == 2) {
                    Map<Long, Epic> epicMap = manager.getEpics();
                    String response = gson.toJson(epicMap);
                    writeResponse(httpExchange, response, 200);
                } else if (pathSplits.length == 3) {
                    handleGetEpicById(httpExchange);
                } else if (pathSplits.length == 4) {
                    handleGetEpicSubtasks(httpExchange);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
                break;
            case "POST":
                handlePostEpics(httpExchange);
                break;
            case "DELETE":
                long id;
                try {
                    id = Long.parseLong(pathSplits[2]);
                } catch (NumberFormatException e) {
                    errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
                    return;
                }
                manager.removeEpicTask(id);
                writeResponse(httpExchange, "", 204);
                break;
            default:
                writeResponse(httpExchange, "", 405);
        }
    }

    private void handleSubtasks(HttpExchange httpExchange) throws IOException {
        String[] pathSplits = httpExchange.getRequestURI().getPath().split("/");
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (pathSplits.length == 2) {
                    writeResponse(httpExchange, gson.toJson(manager.getSubtasks()), 200);
                } else if (pathSplits.length == 3) {
                    handleGetSubtaskById(httpExchange);
                } else {
                    writeResponse(httpExchange, "", 404);
                }
                break;
            case "POST":
                handlePostSubtasks(httpExchange);
                break;
            case "DELETE":
                long id;
                try {
                    id = Long.parseLong(pathSplits[2]);
                } catch (NumberFormatException e) {
                    errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
                    return;
                }
                manager.removeSubTask(id);
                writeResponse(httpExchange, "", 204);
                break;
            default:
                writeResponse(httpExchange, "", 405);

        }
    }

    private void handlePostSubtasks(HttpExchange httpExchange) {
        try (InputStream is = httpExchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null) {
                try {
                    manager.createSubTask(subtask);
                    writeResponse(httpExchange, gson.toJson(subtask), 201);
                } catch (ValidationException | NotFoundException e) {
                    errorHandler.handle(httpExchange, e);
                }

            } else {
                try {
                    manager.updateSubTask(subtask);
                    writeResponse(httpExchange, gson.toJson(subtask) + subtask.getId(), 201);
                } catch (Exception e) {
                    errorHandler.handle(httpExchange, e);
                }

            }

        } catch (IOException e) {
            errorHandler.handle(httpExchange, e);
        }
    }

    private void handlePostEpics(HttpExchange httpExchange) {
        try (InputStream is = httpExchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            try {
                manager.createEpic(epic);
                writeResponse(httpExchange, gson.toJson(epic), 201);
            } catch (NotFoundException e) {
                errorHandler.handle(httpExchange, e);
            }

        } catch (IOException e) {
            errorHandler.handle(httpExchange, e);
        }

    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        long id;
        try {
            id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
        } catch (NumberFormatException e) {
            errorHandler.handle(httpExchange, new BadRequestException("Неправильный запрос"));
            return;
        }
        Optional<Epic> optEpic = manager.getEpic(id);
        if (optEpic.isEmpty()) {
            errorHandler.handle(httpExchange, new NotFoundException("Эпик по id=" + id + " не найден"));
        } else {
            List<Subtask> subs = optEpic.get().getSubTasksList();
            String response = gson.toJson(subs);
            writeResponse(httpExchange, response, 200);
        }


    }
}






