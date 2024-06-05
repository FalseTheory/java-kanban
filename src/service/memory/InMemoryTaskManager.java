package service.memory;

import exception.NotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.HistoryManager;
import service.TaskManager;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected long count = 0;

    protected final HistoryManager historyManager;
    protected final Map<Long, Task> tasks;
    protected final Map<Long, Subtask> subtasks;
    protected final Map<Long, Epic> epics;
    protected final Set<Task> prioritizedTasks;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(new TasksByPriorityComparator());

    }


    @Override
    public void clearAllTasks() {
        tasks.keySet()
                .forEach(this::removeTask);
    }

    @Override
    public void clearAllEpics() {
        clearAllSubtasks();
        epics.keySet()
                .forEach(this::removeEpicTask);
    }

    @Override
    public void clearAllSubtasks() {
        epics.values()
                .forEach(epic -> {
                    epic.getSubTasksList()
                            .forEach(subtask -> subtasks.remove(subtask.getId()));

                    epic.getSubTasksList().clear();
                    calculateStatus(epic);

                });
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
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public void removeTask(Long id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(Long id) {
        Subtask tempTask = subtasks.remove(id);
        Epic tempEpic = tempTask.getEpic();
        if (tempEpic != null) {
            tempEpic.removeTask(tempTask);
            calculateStatus(tempEpic);
        }
        historyManager.remove(id);

    }

    @Override
    public void removeEpicTask(Long id) {
        Epic tempEpic = epics.remove(id);
        for (Subtask subtask : tempEpic.getSubTasksList()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
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
            if (epics.get(tempEpic.getId()) == null) {
                throw new NotFoundException("Не найден эпик по id: " + tempEpic.getId());
            }
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
        boolean isNew = true;

        for (Subtask subtask : epicTasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                if (subtask.getStatus() != TaskStatus.NEW) {
                    isNew = false;
                }
                isDone = false;
            }

        }
        if (isDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        if (isNew) {
            epic.setStatus(TaskStatus.NEW);
        }


    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private long generateId() {
        return ++count;
    }


    @Override
    public Map<Long, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Map<Long, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Long, Epic> getEpics() {
        return epics;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private static class TasksByPriorityComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return -1;
            } else if (t1.getStartTime().isEqual(t2.getStartTime())) {
                return 0;
            } else {
                return 1;
            }
        }
    }


}
