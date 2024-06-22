package server;


import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты сервера")
public class HttpTaskServerTest {

    Gson gson = HttpTaskServer.getGson();
    TaskManager taskManager;
    HttpTaskServer taskServer;
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void init() {
        taskManager = Managers.getInMemoryManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    @DisplayName("Задача должна корректно создаваться")
    @Test
    public void shouldCreateTask() throws IOException, InterruptedException {
        Task expectedTask = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/tasks");
        String body = gson.toJson(expectedTask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        expectedTask.setId(1L);

        assertEquals(expectedTask, taskManager.getTask(1L).get());

    }

    @DisplayName("Задача по id должна корректно возвращаться")
    @Test
    public void shouldCorrectlyReturnTaskById() throws IOException, InterruptedException {
        Task createdTask = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/tasks/1");
        taskManager.createTask(createdTask);
        String expectedResponse = gson.toJson(createdTask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedResponse, response.body());
    }

    @DisplayName("Должен возвращаться корректная мапа задач")
    @Test
    public void shouldCorrectlyReturnListOfTasks() throws IOException, InterruptedException {
        Task createdTask = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Task createdTask2 = new Task("Task2", "Description2", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/tasks");
        taskManager.createTask(createdTask);
        taskManager.createTask(createdTask2);
        String expectedResponse = gson.toJson(taskManager.getTasks());

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedResponse, response.body());
    }

    @DisplayName("Задача должна корректно обновляться")
    @Test
    public void shouldCorrectlyUpdateTask() throws IOException, InterruptedException {
        Task createdTask = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Task updatedTask = new Task("Task2", "Description2", TaskStatus.NEW, 1L, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/tasks/1");

        taskManager.createTask(createdTask);

        String body = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(updatedTask, taskManager.getTask(1L).get());
    }

    @DisplayName("Подзадача должна корректно создаваться")
    @Test
    public void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask expectedSubTask = new Subtask("Task", "Description", 1L, TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/subtasks");
        String body = gson.toJson(expectedSubTask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        expectedSubTask.setId(2L);

        assertEquals(expectedSubTask, taskManager.getSubTask(2L).get());
    }

    @DisplayName("Подзадача должна корректно обновляться")
    @Test
    public void shouldCorrectyUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask createdSub = new Subtask("Task", "Description", 1L, TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Subtask updatedSub = new Subtask("Task2", "Description2", 1L, TaskStatus.NEW, 2L, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        URI url = URI.create("http://localhost:8080/tasks/1");

        taskManager.createSubTask(createdSub);

        String body = gson.toJson(updatedSub);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(updatedSub, taskManager.getSubTask(2L).get());
    }

    @DisplayName("Подзадача по id должна корректно выводиться")
    @Test
    public void shouldCorrectlyReturnSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask expectedSubTask = new Subtask("Task", "Description", 1L, TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        taskManager.createSubTask(expectedSubTask);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        String expected = gson.toJson(expectedSubTask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());

    }

    @DisplayName("Мапа всех подзадач должна корректно возвращаться")
    @Test
    public void shouldCorrectlyReturnSubtasksMap() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subTask1 = new Subtask("Task", "Description", 1L, TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Subtask subTask2 = new Subtask("Task2", "Description2", 1L, TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        String expected = gson.toJson(taskManager.getSubtasks());

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());


    }

    @DisplayName("Эпик должен корректно создаваться")
    @Test
    public void shouldCreateEpic() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        URI url = URI.create("http://localhost:8080/epics");
        String body = gson.toJson(expectedEpic);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        expectedEpic.setId(1L);

        assertEquals(expectedEpic, taskManager.getEpic(1L).get());
    }

    @DisplayName("Эпик должен корректно выводиться по id")
    @Test
    public void shouldCorrectlyReturnEpicById() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        URI url = URI.create("http://localhost:8080/epics/1");
        taskManager.createEpic(expectedEpic);
        String expectedResponse = gson.toJson(expectedEpic);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedResponse, response.body());
    }

    @DisplayName("Мапа всех эпиков должна корректно выводиться")
    @Test
    public void shouldCorrectlyReturnEpicsMap() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        URI url = URI.create("http://localhost:8080/epics");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expectedResponse = gson.toJson(taskManager.getEpics());

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedResponse, response.body());
    }

    @DisplayName("Список подзадач эпика должен корректно выводиться")
    @Test
    public void shouldCorrectlyReturnEpicSubtasksList() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("SUb", "description", epic.getId(), TaskStatus.NEW, Duration.ofMinutes(3)
                , LocalDateTime.of(2015, 11, 12, 4, 5));
        Subtask subtask2 = new Subtask("SUb", "description", epic.getId(), TaskStatus.NEW, Duration.ofMinutes(3)
                , LocalDateTime.of(2016, 11, 12, 4, 5));
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);

        String expectedResponse = gson.toJson(List.of(subtask1, subtask2));

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedResponse, response.body());


    }

    @DisplayName("История должна корректно выводиться")
    @Test
    public void shouldCorrectlyReturnHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");

        Task task1 = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Task task2 = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTask(2L);
        taskManager.getTask(1L);

        String expectedList = gson.toJson(taskManager.getHistory());

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(expectedList, response.body());


    }

    @DisplayName("Список приоритезированных задач должен корректно выводиться")
    @Test
    public void shouldCorrectlyReturnPrioritizedList() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8080/prioritized");

        Task task1 = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 12, 12, 12, 12));
        Task task2 = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2023, 12, 12, 12, 12));
        Task task3 = new Task("Task", "Description", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2025, 12, 12, 12, 12));

        List<Task> expectedList = List.of(task2, task1, task3);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(gson.toJson(expectedList), response.body());


    }

    @DisplayName("Неправильный путь должен корректно обрабатываться")
    @Test
    public void badRequestShouldBeCorrectlyHandled() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/somePath");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
