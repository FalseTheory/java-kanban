package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    private void handleGetTaskById(HttpExchange httpExchange) {
        try (httpExchange) {
            long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
            Optional<Task> optTask = manager.getTask(id);
            try {
                if (optTask.isEmpty()) {
                    throw new NotFoundException("Такой подзадачи не существует");
                } else {
                    writeResponse(httpExchange, gson.toJson(optTask.get()), 200);
                }
            } catch (Exception e) {
                errorHandler.handle(httpExchange, e);
            }
        }

    }

    private void handleGetEpicById(HttpExchange httpExchange) {
        try (httpExchange) {
            long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
            Optional<Epic> optEpic = manager.getEpic(id);
            try {
                if (optEpic.isEmpty()) {
                    throw new NotFoundException("Такого эпика не существует");
                } else {
                    writeResponse(httpExchange, gson.toJson(optEpic.get()), 200);
                }
            } catch (Exception e) {
                errorHandler.handle(httpExchange, e);
            }

        }

    }

    private void handleGetSubtaskById(HttpExchange httpExchange) {
        try (httpExchange) {
            long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
            Optional<Subtask> optSub = manager.getSubTask(id);
            try {
                if (optSub.isEmpty()) {
                    throw new NotFoundException("Такой подзадачи не существует");
                } else {
                    writeResponse(httpExchange, gson.toJson(optSub.get()), 200);
                }
            } catch (Exception e) {
                errorHandler.handle(httpExchange, e);
            }

        }

    }


    private void handlePostTasks(HttpExchange httpExchange) {
        try (InputStream is = httpExchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null) {
                try {
                    manager.createTask(task);
                    writeResponse(httpExchange, "Создана задача с id=" + task.getId(), 201);
                } catch (ValidationException e) {
                    errorHandler.handle(httpExchange, e);
                }

            } else {
                try {
                    manager.updateTask(task);
                    writeResponse(httpExchange, "Обновлена задача с id=" + task.getId(), 201);
                } catch (ValidationException e) {
                    errorHandler.handle(httpExchange, e);
                }

            }

        } catch (IOException e) {
            errorHandler.handle(httpExchange, e);
        }
    }

    private void handlePrioritizedTasks(HttpExchange httpExchange) {
        try (httpExchange) {
            if (httpExchange.getRequestMethod().equals("GET")) {
                List<Task> prioritized = manager.getPrioritizedTasks();
                String response = gson.toJson(prioritized);
                writeResponse(httpExchange, response, 200);
            } else {
                throw new BadRequestException("Неправильный запрос");
            }
        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        }
    }

    private void handleHistory(HttpExchange httpExchange) {
        try (httpExchange) {
            if (httpExchange.getRequestMethod().equals("GET")) {
                List<Task> history = manager.getHistory();
                String response = gson.toJson(history);
                writeResponse(httpExchange, response, 200);
            } else {
                throw new BadRequestException("Неправильный запрос");
            }
        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        }

    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
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
                writeResponse(httpExchange, "", 204);
                break;
            default:
                writeResponse(httpExchange, "Такого метода не существует", 400);
                break;
        }
    }

    private void handleEpics(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                int length = httpExchange.getRequestURI().getPath().split("/").length;
                if (length == 2) {
                    Map<Long, Epic> epicMap = manager.getEpics();
                    String response = gson.toJson(epicMap);
                    writeResponse(httpExchange, response, 200);
                } else if (length == 3) {
                    handleGetEpicById(httpExchange);
                } else if (length == 4) {
                    handleGetEpicSubtasks(httpExchange);
                }
                break;
            case "POST":
                handlePostEpics(httpExchange);
                break;
            case "DELETE":
                long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
                manager.removeEpicTask(id);
                writeResponse(httpExchange, "", 204);
                break;
            default:
                throw new BadRequestException("Неправильный запрос");
        }
    }

    private void handleSubtasks(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                if (httpExchange.getRequestURI().getPath().split("/").length == 2) {
                    writeResponse(httpExchange, gson.toJson(manager.getSubtasks()), 200);
                } else {
                    handleGetSubtaskById(httpExchange);
                }
                break;
            case "POST":
                handlePostSubtasks(httpExchange);
                break;
            case "DELETE":
                long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
                manager.removeSubTask(id);
                writeResponse(httpExchange, "", 204);
                break;
            default:
                throw new BadRequestException("Неправильный запрос");

        }
    }

    private void handlePostSubtasks(HttpExchange httpExchange) {
        try (InputStream is = httpExchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null) {
                try {
                    manager.createSubTask(subtask);
                    writeResponse(httpExchange, "Создана подзадача с id=" + subtask.getId(), 201);
                } catch (ValidationException | NotFoundException e) {
                    errorHandler.handle(httpExchange, e);
                }

            } else {
                try {
                    manager.updateSubTask(subtask);
                    writeResponse(httpExchange, "Обновлена подзадача с id=" + subtask.getId(), 201);
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
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                throw new BadRequestException("Bad request");
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            Epic epic = new Epic(name, description);

            try {
                manager.createEpic(epic);
                writeResponse(httpExchange, "Создан эпик с id=" + epic.getId(), 201);
            } catch (NotFoundException e) {
                errorHandler.handle(httpExchange, e);
            }

        } catch (IOException e) {
            errorHandler.handle(httpExchange, e);
        }

    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange) {
        try (httpExchange) {

            long id = Long.parseLong(httpExchange.getRequestURI().getPath().split("/")[2]);
            Optional<Epic> optEpic = manager.getEpic(id);
            if (optEpic.isEmpty()) {
                throw new NotFoundException("Эпик по id=" + id + " не найден");
            }
            List<Subtask> subs = optEpic.get().getSubTasksList();
            String response = gson.toJson(subs);
            writeResponse(httpExchange, response, 200);
        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        }
    }
}






