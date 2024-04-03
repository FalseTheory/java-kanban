import service.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {

    public static void main(String[] args) {


        //тесты

        //обычная задача
        TaskManager taskManager = new TaskManager();

        Task task = taskManager.createSimpleTask(new Task("Тестовая задача","Описание",TaskStatus.NEW));
        System.out.println("Created task: " + task);

        Task taskFrom = taskManager.getTaskById(task.getId());

        Task updated = new Task("Обновлённая задача", "Новое описание", TaskStatus.IN_PROGRESS,taskFrom.getId());
        taskManager.updateTask(updated);
        System.out.println("Updated task: "+taskManager.getTaskById(task.getId()));

        taskManager.removeTask(taskFrom.getId());
        System.out.println("Removed task: " + task);

        //эпики и подзадачи
        Epic epic1 = new Epic("Эпик1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1,TaskStatus.NEW);
        System.out.println("Создание эпика");
        System.out.println(taskManager.createEpic(epic1));
        System.out.println(taskManager.createSubTask(subtask1));
        System.out.println(taskManager.getEpic(epic1.getId()));

        System.out.println("Изменение подзадачи и статуса эпика");
        taskManager.updateSubTask(new Subtask("Подзадача1 изменённая","Описание new",subtask1.getEpic(),TaskStatus.DONE, subtask1.getId()));
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        //проверка рассчёта статуса
        taskManager.createSubTask(new Subtask("Подзадача 2", "Описание",epic1,TaskStatus.NEW));
        System.out.println(taskManager.getEpic(epic1.getId()));
        //Подзадачи эпика
        System.out.println("Подзадачи эпика: " + taskManager.getSubtasksForEpic(epic1.getId()));

        taskManager.clearAll();

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());



    }
}
