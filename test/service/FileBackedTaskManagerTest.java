package service;

import exception.ManagerIOException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Менеджер должен корректно восстанавливаться из файла")
    public void managerShouldBeCorrectlyRestoredFromFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new File("testResources/test1.csv"));


        assertEquals(taskManager.getEpics(), manager.getEpics());
        assertEquals(taskManager.getTasks(), manager.getTasks());
        assertEquals(taskManager.getSubtasks(), manager.getSubtasks());

        List<Subtask> expectedSubTasks4 = taskManager.getEpic(4L).getSubTasksList();
        List<Subtask> expectedSubTasks3 = taskManager.getEpic(3L).getSubTasksList();

        expectedSubTasks3.sort(new SubtaskComparator());
        expectedSubTasks4.sort(new SubtaskComparator());

        List<Subtask> actualSubTasks3 = manager.getEpic(3L).getSubTasksList();
        List<Subtask> actualSubTasks4 = manager.getEpic(4L).getSubTasksList();

        actualSubTasks3.sort(new SubtaskComparator());
        actualSubTasks4.sort(new SubtaskComparator());


        assertEquals(expectedSubTasks4, actualSubTasks4);
        assertEquals(expectedSubTasks3, actualSubTasks3);


    }

    @Test
    @DisplayName("Менеджер должен корректно восстанавливаться из файла с рандомно расположенными записями")
    public void managerShouldBeCorrectlyRestoredFromRandomFile() {
        FileBackedTaskManager manager =
                FileBackedTaskManager.loadFromFile(new File("testResources/testRandomWrite.csv"));


        assertEquals(taskManager.getEpics(), manager.getEpics());
        assertEquals(taskManager.getTasks(), manager.getTasks());
        assertEquals(taskManager.getSubtasks(), manager.getSubtasks());

        List<Subtask> expectedSubTasks4 = taskManager.getEpic(4L).getSubTasksList();
        List<Subtask> expectedSubTasks3 = taskManager.getEpic(3L).getSubTasksList();

        expectedSubTasks3.sort(new SubtaskComparator());
        expectedSubTasks4.sort(new SubtaskComparator());


        List<Subtask> actualSubTasks3 = manager.getEpic(3L).getSubTasksList();
        List<Subtask> actualSubTasks4 = manager.getEpic(4L).getSubTasksList();

        actualSubTasks3.sort(new SubtaskComparator());
        actualSubTasks4.sort(new SubtaskComparator());


        assertEquals(expectedSubTasks4, actualSubTasks4);
        assertEquals(expectedSubTasks3, actualSubTasks3);
    }

    @Test
    @DisplayName("Менеджер должен корректно создаваться из пустого файла")
    public void managerShouldBeProperlyCreatedFromEmptyFile() throws IOException {

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(File.createTempFile("testEmpty", ".csv"));

        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());

    }

    @Test
    @DisplayName("Файл должен корректно записывать задачу")
    public void managerShouldCorrectlySaveTasksToFile() throws IOException {
        File file = File.createTempFile("testWrite", ".csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.createTask(new Task("Поесть", "Описание", TaskStatus.NEW));


        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            bReader.readLine();
            bReader.readLine();

            String line = bReader.readLine();
            assertEquals("1,TASK,Поесть,NEW,Описание,null", line);

        } catch (IOException e) {
            throw new ManagerIOException("Ошибка при восстановлении менеджера из файла");
        }

    }

    @Test
    @DisplayName("Файл должен корректно записывать Эпик и подзадачи")
    public void managerShouldCorrectlySaveEpicToFile() throws IOException {
        File file = File.createTempFile("testWrite", ".csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.createEpic(new Epic("Не пустой эпик", "Описание"));

        manager.createSubTask(new Subtask("Подзадача 1", "Описание", manager.getEpic(1L), TaskStatus.IN_PROGRESS));


        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            bReader.readLine();
            bReader.readLine();

            String line = bReader.readLine();
            String line2 = bReader.readLine();

            assertEquals("1,EPIC,Не пустой эпик,IN_PROGRESS,Описание,null", line);
            assertEquals("2,SUBTASK,Подзадача 1,IN_PROGRESS,Описание,1", line2);

        } catch (IOException e) {
            throw new ManagerIOException("Ошибка при восстановлении менеджера из файла");
        }


    }

    @Test
    @DisplayName("Удаление должно отражаться в файле")
    public void deletionShouldBeCorrectlyHandled() throws IOException {

        File file = File.createTempFile("testDeletion", ".csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.createTask(new Task("Поесть", "Описание", TaskStatus.NEW));

        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            bReader.readLine();
            bReader.readLine();

            String line = bReader.readLine();
            assertEquals("1,TASK,Поесть,NEW,Описание,null", line);

        } catch (IOException e) {
            throw new ManagerIOException("Ошибка при восстановлении менеджера из файла");
        }

        manager.removeTask(1L);
        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            bReader.readLine();
            bReader.readLine();

            String line = bReader.readLine();
            assertNull(line);

        } catch (IOException e) {
            throw new ManagerIOException("Ошибка при восстановлении менеджера из файла");
        }

    }

    @Test
    @DisplayName("История должна корректно восстанавливаться из файла")
    public void historyShouldBeCorrectlyRecorded() {
        taskManager.getTask(1L);
        taskManager.getSubTask(6L);
        taskManager.getTask(2L);
        taskManager.getEpic(3L);
        taskManager.getEpic(4L);


        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new File("testResources/testHistory.csv"));

        assertEquals(taskManager.getHistory(), manager.getHistory());
    }


    private static class SubtaskComparator implements Comparator<Subtask> {
        @Override
        public int compare(Subtask s1, Subtask s2) {
            return Long.compare(s1.getId(), s2.getId());
        }
    }
}


