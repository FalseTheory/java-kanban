package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


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

        assertTrue(historyManager.getHistory().isEmpty(),"История должна быть пустой при инициализации");
        historyManager.add(new Task("Задача", "Описание", TaskStatus.NEW));

        assertEquals(1, historyManager.getHistory().size(),"Размер истории не соответствует ожидаемому");

    }

    @DisplayName("Размер истории не должен превышать 10")
    @Test
    public void historySizeMustNotBeHigherThan10() {

        for (int i = 0; i <= 9; i++) {
            historyManager.add(new Task("Задача", "Описание", TaskStatus.NEW));
        }
        assertEquals(10, historyManager.getHistory().size());

        Task task1 = new Task("Проверочная задача", "Описание 1", TaskStatus.DONE);
        historyManager.add(task1);

        assertEquals(10, historyManager.getHistory().size(),"Размер истории не совпадает с ожидаемым");
        assertEquals(historyManager.getHistory().getLast(), task1,"Новая задача должна добавляться в конец списка");

    }


}
