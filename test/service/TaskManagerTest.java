package service;

import exception.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Базовый класс тестов для менеджеров")
public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;


    @Test
    @DisplayName("Задача не должна пересекаться на границах")
    public void taskTimeShouldNotIntersect() {


        Task task = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 30));
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 13, 0));
        Task task2 = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 0));


        taskManager.createTask(task);
        assertDoesNotThrow(() -> taskManager.createTask(task1));
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    @DisplayName("Задача не должна быть создана при пересечении")
    public void taskShouldNotBeCreatedWhenIntersect() {
        Task task = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 30));

        Task task2 = new Task("Задача 1", "Описание", TaskStatus.NEW, task.getId(), Duration.ofMinutes(100),
                LocalDateTime.of(2015, 6, 7, 12, 15));

        taskManager.createTask(task);
        assertThrows(ValidationException.class, () -> taskManager.createTask(task2));
    }

    @Test
    @DisplayName("Задачи не могут начинаться в одно время")
    public void taskShouldNotHaveSameStartTime() {
        Task task = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 15));

        Task task2 = new Task("Задача 1", "Описание", TaskStatus.NEW, task.getId(), Duration.ofMinutes(100),
                LocalDateTime.of(2015, 6, 7, 12, 15));

        taskManager.createTask(task);
        assertThrows(ValidationException.class, () -> taskManager.createTask(task2));
    }

    @Test
    @DisplayName("Задача не должна пересекаться с собой")
    public void taskShouldNotIntersectWithSelf() {
        Task task = new Task("Задача 1", "Описание", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 30));
        taskManager.createTask(task);
        Task updatedTask = new Task("Задача 1", "Описание", TaskStatus.NEW, task.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2015, 6, 7, 12, 15));

        assertDoesNotThrow(() -> taskManager.updateTask(updatedTask));

    }

    @Test
    @DisplayName("Мапы каждого типа задач должны корректно добавлять задачи своих типов")
    public void tasksListsShouldNotBeEmptyAfterTaskAddition() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2013, 11, 12, 4, 5));
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2014, 11, 12, 4, 5));

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
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2013, 11, 12, 4, 5));
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2017, 11, 12, 4, 5));


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
    @DisplayName("Задачи должны корректно обновляться с сохранением id")
    public void shouldCorrectlyUpdateTasksOfAnyType() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2013, 11, 12, 4, 5));
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2013, 9, 12, 4, 5));

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subtask1);
        taskManager.createTask(task1);

        Epic epicUpdate = new Epic("Эпик", "Описание эпик", epic1.getId());
        Subtask subtaskUpdate = new Subtask("Подзадача", "Описание саб", epic1, TaskStatus.IN_PROGRESS,
                subtask1.getId(),
                subtask1.getDuration(),
                subtask1.getStartTime());
        Task taskUpdate = new Task("Задача", "Описание таск", TaskStatus.IN_PROGRESS, task1.getId(),
                task1.getDuration(),
                task1.getStartTime());

        taskManager.updateTask(taskUpdate);
        Task taskFromManager = taskManager.getTask(taskUpdate.getId()).get();
        assertEquals(taskUpdate, taskFromManager);
        assertEquals("Задача", taskFromManager.getName()); //отдельная проверка полей, поскольку equals по заданию проверяет только id
        assertEquals("Описание таск", taskFromManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus());

        taskManager.updateSubTask(subtaskUpdate);
        Subtask subtaskFromManager = taskManager.getSubTask(subtaskUpdate.getId()).get();
        assertEquals(subtaskUpdate, subtaskFromManager);
        assertEquals("Подзадача", subtaskFromManager.getName());
        assertEquals("Описание саб", subtaskFromManager.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, subtaskFromManager.getStatus());

        taskManager.updateEpic(epicUpdate);
        Epic epicFromManager = taskManager.getEpic(epicUpdate.getId()).get();
        assertEquals(epicUpdate, epicFromManager);
        assertEquals("Эпик", epicFromManager.getName());
        assertEquals("Описание эпик", epicFromManager.getDescription());

    }

    @Test
    @DisplayName("Статус эпика должен корректно перерасчитываться менеджером")
    public void epicStatusShouldBeRecalculatedAutomatically() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        assertNull(epic1.getStatus());
        taskManager.createEpic(epic1);
        assertEquals(epic1.getStatus(), TaskStatus.NEW);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1,
                TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2014, 12, 12, 12, 12));
        Subtask subtask2 = new Subtask("Подзадача 3", "Описание", epic1, TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(33),
                LocalDateTime.of(2015, 11, 11, 6, 4));

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);

        Subtask subtask1_change = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.DONE,
                subtask1.getId(),
                subtask1.getDuration(),
                subtask1.getStartTime());
        Subtask subtask2_change = new Subtask("Подзадача 3", "Описание", epic1, TaskStatus.DONE,
                subtask2.getId(),
                subtask2.getDuration(),
                subtask2.getStartTime());

        taskManager.updateSubTask(subtask1_change);
        taskManager.updateSubTask(subtask2_change);
        assertEquals(epic1.getStatus(), TaskStatus.DONE);

        taskManager.clearAllSubtasks();
        assertEquals(epic1.getStatus(), TaskStatus.NEW);

    }

    @Test
    @DisplayName("История должна записываться при обращению к любому виду задач")
    public void shouldRecordHistoryForTaskOfAnyType() {
        Epic epic1 = new Epic("Эпик 1", "Описание");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic1, TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2013, 11, 12, 4, 5));
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(3)
                , LocalDateTime.of(2015, 11, 12, 4, 5));

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





