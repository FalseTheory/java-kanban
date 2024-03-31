import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Построить дом", "Проверка");
        Task task2 = new Task("Построить забор", "Проверка");

        Epic epicTask1 = new Epic("Мега дом", "Процесс пошел");
        Subtask sub2 = new Subtask("Взять дерево", "Нарубить",epicTask1);
        Subtask sub3 = new Subtask("Взять деревья", "Нарубить дров",epicTask1);
        Epic epicTask2 = new Epic("Мега дом", "Процесс пошел");
        Subtask sub4 = new Subtask("Взять дерево", "Нарубить",epicTask2);
        Subtask sub5 = new Subtask("Взять деревья", "Нарубить дров",epicTask2);
        taskManager.createEpic(epicTask1);
        taskManager.createSubTask(sub2);
        taskManager.createSubTask(sub3);
        taskManager.createEpic(epicTask2);
        taskManager.createSubTask(sub4);
        taskManager.createSubTask(sub5);
        taskManager.printAllTasks();
        System.out.println(epicTask1.equals(epicTask1));
    }
}
