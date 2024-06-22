import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import server.HttpTaskServer;
import service.Managers;
import service.TaskManager;
import service.memory.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {

        //Пользовательский сценарий

        TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        manager.createTask(new Task("Task", "Description", TaskStatus.NEW));
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        manager.createSubTask(new Subtask("SUb", "description", epic.getId(), TaskStatus.NEW, Duration.ofMinutes(3)
                , LocalDateTime.of(2015, 11, 12, 4, 5)));

        HttpTaskServer server = new HttpTaskServer(manager);

        server.start();


    }
}
