import tasks.Task;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Построить дом", "Проверка");
        Task task2 = new Task("Построить забор", "Проверка");
        System.out.println(task1.equals(task2));
        System.out.println(task1.getId());
        System.out.println(task2.getId());
    }
}
