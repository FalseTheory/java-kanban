package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.file.FileBackedTaskManager;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import java.io.File;

public class FileBackedTaskManagerTest {


    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getInMemoryManager();

        taskManager.createTask(new Task("Поесть", "Описание", TaskStatus.NEW));
        taskManager.createTask(new Task("Нарисовать дом", "Описание", TaskStatus.NEW));

        taskManager.createEpic(new Epic("Пустой эпик", "Пустота"));
        taskManager.createEpic(new Epic("Не пустой эпик", "Описание"));

        taskManager.createSubTask(new Subtask("Подзадача 1", "Описание", taskManager.getEpic(3L), TaskStatus.IN_PROGRESS));
        taskManager.createSubTask(new Subtask("Подзадача 2", "Описание", taskManager.getEpic(3L), TaskStatus.IN_PROGRESS));
        taskManager.createSubTask(new Subtask("Подзадача 3", "Описание", taskManager.getEpic(3L), TaskStatus.IN_PROGRESS));
    }

    @Test
    @DisplayName("Менеджер должен корректно восстанавливаться из файла") // Тест также реализует дополнительное задание (Сценарий использования)
    public void managerShouldBeCorrectlyRestoredFromFile(){
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new File("testResources/test1.csv"));



        assertEquals(taskManager.getEpics(),manager.getEpics());
        assertEquals(taskManager.getTasks(),manager.getTasks());
        assertEquals(taskManager.getSubtasks(),manager.getSubtasks());

        List<Subtask> expectedSubTasks4 = taskManager.getEpic(4L).getSubTasksList();
        List<Subtask> expectedSubTasks3 = taskManager.getEpic(3L).getSubTasksList();

        expectedSubTasks3.sort(new SubtaskComparator());
        expectedSubTasks4.sort(new SubtaskComparator());

        List<Subtask> actualSubTasks3 = manager.getEpic(3L).getSubTasksList();
        List<Subtask> actualSubTasks4 = manager.getEpic(4L).getSubTasksList();

        actualSubTasks3.sort(new SubtaskComparator());
        actualSubTasks4.sort(new SubtaskComparator());


        assertEquals(expectedSubTasks4,actualSubTasks4);
        assertEquals(expectedSubTasks3,actualSubTasks3);





    }
    @Test
    @DisplayName("Менеджер должен корректно восстанавливаться из файла с рандомно расположенными записями")
    public void managerShouldBeCorrectlyRestoredFromRandomFile(){
        FileBackedTaskManager manager =
                FileBackedTaskManager.loadFromFile(new File("testResources/testRandomWrite.csv"));



        assertEquals(taskManager.getEpics(),manager.getEpics());
        assertEquals(taskManager.getTasks(),manager.getTasks());
        assertEquals(taskManager.getSubtasks(),manager.getSubtasks());

        List<Subtask> expectedSubTasks4 = taskManager.getEpic(4L).getSubTasksList();
        List<Subtask> expectedSubTasks3 = taskManager.getEpic(3L).getSubTasksList();

        expectedSubTasks3.sort(new SubtaskComparator());
        expectedSubTasks4.sort(new SubtaskComparator());


        List<Subtask> actualSubTasks3 = manager.getEpic(3L).getSubTasksList();
        List<Subtask> actualSubTasks4 = manager.getEpic(4L).getSubTasksList();

        actualSubTasks3.sort(new SubtaskComparator());
        actualSubTasks4.sort(new SubtaskComparator());


        assertEquals(expectedSubTasks4,actualSubTasks4);
        assertEquals(expectedSubTasks3,actualSubTasks3);
    }

    @Test
    @DisplayName("Менеджер должен корректно создаваться из пустого файла")
    public void managerShouldBeProperlyCreatedFromEmptyFile() throws IOException {

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(File.createTempFile("testEmpty",".csv"));

        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());

    }




    private static class SubtaskComparator implements Comparator<Subtask> {
        @Override
        public int compare(Subtask s1, Subtask s2){
            return Long.compare(s1.getId(), s2.getId());
        }
    }
}


