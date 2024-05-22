package model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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


}
