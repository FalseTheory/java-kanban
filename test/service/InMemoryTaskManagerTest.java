package service;


import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Менеджер задач")
public class InMemoryTaskManagerTest {


    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getInMemoryManager();
    }


    @Test
    @DisplayName("Мапы каждого типа задач должны корректно добавлять задачи своих типов")
    public void tasksListsShouldNotBeEmptyAfterTaskAddition() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subtask1);

        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
    }

    @Test
    @DisplayName("Мапы каждого типа задач должны корректно удалять элементы по id из мапов и истории")
    public void shouldCorrectlyRemoveElementsByIdForEveryMapAndHistory() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW);


        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subtask1);
        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        assertFalse(taskManager.getSubtasks().isEmpty());

        taskManager.getTask(1L);
        taskManager.getEpic(2L);
        taskManager.getSubTask(3L);
        assertEquals(taskManager.getHistory().size(), 3, "История неверно заполняется");

        taskManager.removeTask(task1.getId());
        taskManager.removeSubTask(subtask1.getId());
        taskManager.removeEpicTask(epic1.getId());
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty(), "История не должна хранить удалённые задачи");


    }

    @Test
    @DisplayName("Статус эпика должен корректно перерасчитываться менеджером")
    public void epicStatusShouldBeRecalculatedAutomatically() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        assertNull(epic1.getStatus());
        taskManager.createEpic(epic1);
        assertEquals(epic1.getStatus(), TaskStatus.NEW);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Подзадача 3", "Описание", epic1, TaskStatus.IN_PROGRESS);

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);

        Subtask subtask1_change = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.DONE, subtask1.getId());
        Subtask subtask2_change = new Subtask("Подзадача 3", "Описание", epic1, TaskStatus.DONE, subtask2.getId());

        taskManager.updateSubTask(subtask1_change);
        taskManager.updateSubTask(subtask2_change);
        assertEquals(epic1.getStatus(), TaskStatus.DONE);

        taskManager.clearAllSubtasks();
        assertEquals(epic1.getStatus(), TaskStatus.NEW);

    }

    @Test
    @DisplayName("Задачи должны корректно обновляться с сохранением id")
    public void shouldCorrectlyUpdateTasksOfAnyType() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW);
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subtask1);
        taskManager.createTask(task1);

        Epic epicUpdate = new Epic("Эпик", "Описание эпик", epic1.getId());
        Subtask subtaskUpdate = new Subtask("Подзадача", "Описание саб", epic1, TaskStatus.IN_PROGRESS, subtask1.getId());
        Task taskUpdate = new Task("Задача", "Описание таск", TaskStatus.IN_PROGRESS, task1.getId());

        taskManager.updateTask(taskUpdate);
        Task taskFromManager = taskManager.getTask(taskUpdate.getId());
        assertEquals(taskUpdate, taskFromManager);
        assertEquals("Задача", taskFromManager.getName()); //отдельная проверка полей, поскольку equals по заданию проверяет только id
        assertEquals("Описание таск", taskFromManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus());

        taskManager.updateSubTask(subtaskUpdate);
        Subtask subtaskFromManager = taskManager.getSubTask(subtaskUpdate.getId());
        assertEquals(subtaskUpdate, subtaskFromManager);
        assertEquals("Подзадача", subtaskFromManager.getName());
        assertEquals("Описание саб", subtaskFromManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, subtaskFromManager.getStatus());

        taskManager.updateEpic(epicUpdate);
        Epic epicFromManager = taskManager.getEpic(epicUpdate.getId());
        assertEquals(epicUpdate, epicFromManager);
        assertEquals("Эпик", epicFromManager.getName());
        assertEquals("Описание эпик", epicFromManager.getDescription());

    }

    @Test
    @DisplayName("История должна записываться при обращению к любому виду задач")
    public void shouldRecordHistoryForTaskOfAnyType() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW);
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subtask1);
        taskManager.createTask(task1);

        assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пустой при инициализации");

        taskManager.getEpic(epic1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubTask(subtask1.getId());

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(epic1);
        expectedList.add(task1);
        expectedList.add(subtask1);


        assertEquals(expectedList, taskManager.getHistory());


    }


}
