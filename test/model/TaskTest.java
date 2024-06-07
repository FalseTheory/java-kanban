package model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Все классы задач")
public class TaskTest {

    @DisplayName("Задачи должны быть равны при одинаковом id")
    @Test
    public void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Проверка", "Описание", TaskStatus.NEW, 1L);
        Task task2 = new Task("Test", "Description", TaskStatus.NEW, 1L);
        assertEquals(task1, task2, "Задачи с одинаковым айди должны считаться одинаковыми для менеджеров");
    }

    @Test
    @DisplayName("Задачи разного типа не должны быть равны, имея одинаковый id")
    public void tasksOfDifferentClassWithSameIdShouldNotBeEqual() {
        Task task1 = new Task("Проверка", "Описание", TaskStatus.NEW, 1L);
        Epic epic1 = new Epic("Test", "Description", 1L);
        assertNotEquals(task1, epic1);
    }

    @Test
    @DisplayName("Наследники оригинального класса задач должны корректно наследовать метод equals")
    public void taskChildsShouldBeEqualWithSameId() {
        Epic epic1 = new Epic("Test", "Description", 1L);
        Epic epic2 = new Epic("Test2", "Description2", 1L);
        assertEquals(epic2, epic1);
    }

    @Test
    @DisplayName("Статус эпика должен быть null при создании")
    public void epicStatusMustBeNullUponCreation() {
        Epic epic1 = new Epic("Test", "Description", 1L);
        assertNull(epic1.getStatus());
    }

    @Test
    @DisplayName("Эпик должен хранить в себе список своих подзадач")
    public void epicMustContainListOfHisSubtasks() {
        Epic epic1 = new Epic("Test", "Description", 1L);
        Subtask subtask = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 2L);

        assertTrue(epic1.getSubTasksList().isEmpty());

        epic1.addTask(subtask);

        assertEquals(1, epic1.getSubTasksList().size());
        assertEquals(subtask, epic1.getSubTasksList().getFirst());


    }

    @Test
    @DisplayName("Эпик должен корректно рассчитывать дилтельность")
    public void epicShouldCorrectlyCalculateDuration() {
        Epic epic1 = new Epic("Test", "Description", 1L);

        assertNull(epic1.getDuration(),"Длительность не должна быть задана при инициализации");

        Subtask subtask = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 2L,
                Duration.ofMinutes(30),
                LocalDateTime.of(2012, 11, 11, 4, 5));

        epic1.addTask(subtask);
        assertEquals(subtask.getDuration(), epic1.getDuration(), "Длительность вычисляется неправильно при добавлении первой задачи");

        Subtask subtask1 = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 3L,
                Duration.ofMinutes(60),
                LocalDateTime.of(2013, 11, 11, 4, 5));

        epic1.addTask(subtask1);
        assertEquals(Duration.ofMinutes(90), epic1.getDuration(), "Длительность вычисляется неправильно при нескольких задачах");

        epic1.removeTask(subtask);
        assertEquals(Duration.ofMinutes(60), epic1.getDuration(), "При удалении задачи длительность вычисляется неверно");

        epic1.removeTask(subtask1);
        assertNull(epic1.getDuration(), "При удалении всех задач поле длительности должно быть null");
    }
    @Test
    @DisplayName("Время начала и окончания эпика должно рассчитываться корректно при одной подзадаче")
    public void shouldCorrectlyCalculateEpicStartAndEndTimeWithOneSubtask(){
        Epic epic1 = new Epic("Test", "Description", 1L);

        assertNull(epic1.getStartTime(),"Эпик неправильно инициализируется");
        assertNull(epic1.getEndTime(),"Эпик неправильно инициализируется");

        Subtask subtask = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 2L,
                Duration.ofMinutes(30),
                LocalDateTime.of(2012, 11, 11, 4, 5));

        epic1.addTask(subtask);

        assertEquals(subtask.getStartTime(),epic1.getStartTime(),"Время начала вычисляется неверно");
        assertEquals(subtask.getEndTime(),epic1.getEndTime(),"Время окончания вычисляется неверно");

    }
    @Test
    @DisplayName("Время начала и окончания эпика корректно рассчитывается при нескольких подзадачах")
    public void shoudlCorrectlyCalculateEpicStartAndEndTimeWithManySubtasks(){
        Epic epic1 = new Epic("Test", "Description", 1L);
        Subtask subtask = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 2L,
                Duration.ofMinutes(30),
                LocalDateTime.of(2012, 11, 11, 4, 5));

        Subtask subtask1 = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 3L,
                Duration.ofMinutes(30),
                LocalDateTime.of(2014, 11, 11, 4, 5));

        Subtask subtask2 = new Subtask("Test name", "test Desc", epic1, TaskStatus.NEW, 4L,
                Duration.ofMinutes(60),
                LocalDateTime.of(2010, 11, 11, 4, 5));

        epic1.addTask(subtask);
        epic1.addTask(subtask2);
        epic1.addTask(subtask1);

        assertEquals(subtask2.getStartTime(),epic1.getStartTime(),"Время начала вычисляется некорректно");
        assertEquals(subtask1.getStartTime().plus(subtask1.getDuration()), epic1.getEndTime(),
                "Время окончания вычисляется некорретно");

    }


}
