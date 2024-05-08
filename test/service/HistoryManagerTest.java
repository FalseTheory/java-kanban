package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("Менеджер истории")
public class HistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @DisplayName("Задачи должны корректно добавляться в историю")
    @Test
    public void shouldCorrectlyRecordHistory() {

        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой при инициализации");
        historyManager.add(new Task("Задача", "Описание", TaskStatus.NEW));

        assertEquals(1, historyManager.getHistory().size(), "Размер истории не соответствует ожидаемому");


    }

    @DisplayName("История должна корректно работать с дубликатами задач")
    @Test
    public void shouldCorrectlyWorkWithDuplicates() {

        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW, 1L);
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW, 2L);
        ArrayList<Task> expectedList = new ArrayList<>(List.of(task2, task1));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        assertEquals(expectedList, historyManager.getHistory());


    }

    @DisplayName("Должен корректно удалять задачу из истории по id")
    @Test
    public void shouldCorrectlyRemoveTaskFromHistoryById() {
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW, 1L);
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW, 2L);
        Task task3 = new Task("Задача", "Описание", TaskStatus.NEW, 3L);
        Task task4 = new Task("Задача", "Описание", TaskStatus.NEW, 4L);
        Task task5 = new Task("Задача", "Описание", TaskStatus.NEW, 5L);
        Task task6 = new Task("Задача", "Описание", TaskStatus.NEW, 6L);

        ArrayList<Task> expectedList = new ArrayList<>(List.of(task1, task2, task4, task5, task6));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);

        System.out.println(historyManager.getHistory());

        historyManager.remove(3L);
        System.out.println(historyManager.getHistory());

        assertEquals(expectedList, historyManager.getHistory());

    }

    @DisplayName("Должен корректно работать если список состоит из одной задачи")
    @Test
    public void shouldWorkWhenListContainOneElement() {

        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW, 1L);
        ArrayList<Task> expectedList = new ArrayList<>(Collections.singletonList(task1));

        historyManager.add(task1);
        assertEquals(expectedList, historyManager.getHistory());

        historyManager.add(task1);
        assertEquals(expectedList, historyManager.getHistory());

        historyManager.remove(1L);
        assertEquals(Collections.emptyList(), historyManager.getHistory());
    }
    @DisplayName("Задачи из головы истории должны корректно удаляться")
    @Test
    public void shouldCorrectlyRemoveTaskFromHead(){
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW, 1L);
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW, 2L);
        Task task3 = new Task("Задача", "Описание", TaskStatus.NEW, 3L);


        ArrayList<Task> expectedList = new ArrayList<>(List.of(task2, task3));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1L);

        assertEquals(expectedList,historyManager.getHistory(),"Задача из головы истории не удаляется корректно");

    }
    @DisplayName("Задачи из Хвоста истории должны корректно удаляться")
    @Test
    public void shouldCorrectlyRemoveTaskFromTail(){
        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW, 1L);
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW, 2L);
        Task task3 = new Task("Задача", "Описание", TaskStatus.NEW, 3L);


        ArrayList<Task> expectedList = new ArrayList<>(List.of(task1, task2));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3L);

        assertEquals(expectedList, historyManager.getHistory(),"Задача из хвоста истории не удаляется корректно");

    }


}
