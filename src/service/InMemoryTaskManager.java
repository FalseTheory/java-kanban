package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;


import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private long count = 0;

    private final HistoryManager historyManager;
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, Subtask> subtasks;
    private final HashMap<Long, Epic> epics;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;

    }


    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksList().clear();
            calculateStatus(epic);
        }
    }

    @Override
    public Task getTask(Long id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubTask(Long id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(Long id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        calculateStatus(epic);
        return epic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        subtask.getEpic().addTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculateStatus(subtask.getEpic());
        return subtask;
    }

    @Override
    public Task createSimpleTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public void removeTask(Long id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubTask(Long id) {
        Subtask tempTask = subtasks.remove(id);
        Epic tempEpic = tempTask.getEpic();
        if (tempEpic != null) {
            tempEpic.removeTask(tempTask);
            calculateStatus(tempEpic);
        }

    }

    @Override
    public void removeEpicTask(Long id) {
        Epic tempEpic = epics.remove(id);
        for (Subtask subtask : tempEpic.getSubTasksList()) {
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic tempEpic = subtask.getEpic();
            tempEpic.removeTask(subtask);
            tempEpic.addTask(subtask);
            subtasks.put(subtask.getId(), subtask);
            calculateStatus(subtask.getEpic());
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());


    }

    @Override
    public List<Subtask> getSubtasksForEpic(Long id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        return epic.getSubTasksList();
    }

    private void calculateStatus(Epic epic) {
        List<Subtask> epicTasks = epic.getSubTasksList();
        if (epicTasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isDone = true;
        for (Subtask subtask : epicTasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                isDone = false;
                break;
            }

        }
        if (isDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        for (Subtask subtask : epicTasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }

        }
        epic.setStatus(TaskStatus.NEW);

    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private long generateId() {
        return ++count;
    }


    @Override
    public HashMap<Long, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public HashMap<Long, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Long, Epic> getEpics() {
        return epics;
    }


}
