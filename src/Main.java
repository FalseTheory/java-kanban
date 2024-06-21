import model.Task;
import model.TaskStatus;
import server.HttpTaskServer;
import service.Managers;
import service.TaskManager;
import service.memory.InMemoryTaskManager;


public class Main {

    public static void main(String[] args) {

        //Пользовательский сценарий

        TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        manager.createTask(new Task("Task", "Description", TaskStatus.NEW));
        HttpTaskServer server = new HttpTaskServer(manager);

        server.start();


    }
}
