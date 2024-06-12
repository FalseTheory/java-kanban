
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;
import service.file.FileBackedTaskManager;

import java.io.File;


public class Main {

    public static void main(String[] args) {

        //Пользовательский сценарий
        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask(new Task("Поесть", "Описание", TaskStatus.NEW));
        taskManager.createTask(new Task("Нарисовать дом", "Описание", TaskStatus.NEW));

        taskManager.createEpic(new Epic("Пустой эпик", "Пустота"));
        taskManager.createEpic(new Epic("Не пустой эпик", "Описание"));

        taskManager.createSubTask(new Subtask("Подзадача 1", "Описание", taskManager.getEpic(3L).get(), TaskStatus.IN_PROGRESS));
        taskManager.createSubTask(new Subtask("Подзадача 2", "Описание", taskManager.getEpic(3L).get(), TaskStatus.IN_PROGRESS));
        taskManager.createSubTask(new Subtask("Подзадача 3", "Описание", taskManager.getEpic(3L).get(), TaskStatus.IN_PROGRESS));

        System.out.println(taskManager.getHistory()); //выводит только эпик только 1 раз
        taskManager.getTask(1L);
        taskManager.getTask(2L);
        taskManager.getSubTask(6L);
        System.out.println(taskManager.getHistory());
        taskManager.getTask(2L);
        taskManager.getSubTask(6L);
        taskManager.getTask(1L);
        System.out.println(taskManager.getHistory());


        TaskManager restoredManager = FileBackedTaskManager.loadFromFile(new File("resources/manager.csv"));


    }
}
