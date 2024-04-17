package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    Task getTask(Long id);

    Subtask getSubTask(Long id);

    Epic getEpic(Long id);

    Epic createEpic(Epic epic);

    Subtask createSubTask(Subtask subtask);

    Task createTask(Task task);

    void removeTask(Long id);

    void removeSubTask(Long id);

    void removeEpicTask(Long id);

    void updateTask(Task task);

    void updateSubTask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Subtask> getSubtasksForEpic(Long id);

    HashMap<Long, Subtask> getSubtasks();

    HashMap<Long, Task> getTasks();

    HashMap<Long, Epic> getEpics();

}
