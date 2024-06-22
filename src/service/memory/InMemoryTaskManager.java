package service.memory;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.HistoryManager;
import service.TaskManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected static final DateTimeFormatter TASK_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy:MM:dd;HH:mm");
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
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
    public Optional<Task> getTask(Long id) {
        Optional<Task> task = Optional.ofNullable(tasks.getOrDefault(id, null));
        if (task.isPresent()) {
            historyManager.add(tasks.get(id));
        }
        return task;
    }

    @Override
    public Optional<Subtask> getSubTask(Long id) {
        Optional<Subtask> subtask = Optional.ofNullable(subtasks.getOrDefault(id, null));
        if (subtask.isPresent()) {
            historyManager.add(subtasks.get(id));
        }
        return subtask;
    }

    @Override
    public Optional<Epic> getEpic(Long id) {
        Optional<Epic> epic = Optional.ofNullable(epics.getOrDefault(id, null));
        if (epic.isPresent()) {
            historyManager.add(epics.get(id));
        }
        return epic;
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
        if (subtask.getStartTime() != null && !isTaskValid(subtask)) {
            throw new ValidationException("Задача пересекается с другой задачей");
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.addTask(subtask);
        subtasks.put(subtask.getId(), subtask);

        prioritizedTasks.add(subtask);
        calculateStatus(epic);

        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        if (task.getStartTime() != null && !isTaskValid(task)) {
            throw new ValidationException("Задача пересекается с другой задачей");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public void removeTask(Long id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void removeSubTask(Long id) {
        Subtask tempTask = subtasks.remove(id);
        Epic tempEpic = epics.get(tempTask.getEpicId());
        if (tempEpic != null) {
            tempEpic.removeTask(tempTask);
            calculateStatus(tempEpic);
        }
        historyManager.remove(id);
        prioritizedTasks.remove(tempTask);

    }

    @Override
    public void removeEpicTask(Long id) {
        Epic tempEpic = epics.remove(id);
        for (Subtask subtask : tempEpic.getSubTasksList()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        historyManager.remove(id);

    }

    @Override
    public void updateTask(Task task) {
        if (!isTaskValid(task)) {
            throw new ValidationException("Задача пересекается с другой задачей");
        }
        if (tasks.containsKey(task.getId())) {
            Task updatedTask = tasks.get(task.getId());
            updatedTask.setName(task.getName());
            updatedTask.setStatus(task.getStatus());
            updatedTask.setDescription(task.getDescription());
            updatedTask.setDuration(task.getDuration());
            updatedTask.setStartTime(task.getStartTime());

        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (!isTaskValid(subtask)) {
            throw new ValidationException("Задача пересекается с другой задачей");
        }
        if (subtasks.containsKey(subtask.getId())) {
            Epic tempEpic = epics.get(subtask.getEpicId());
            if (epics.get(tempEpic.getId()) == null) {
                throw new NotFoundException("Не найден эпик по id: " + tempEpic.getId());
            }
            Subtask updatedSubtask = subtasks.get(subtask.getId());
            updatedSubtask.setName(subtask.getName());
            updatedSubtask.setDescription(subtask.getDescription());
            updatedSubtask.setStatus(subtask.getStatus());
            updatedSubtask.setStartTime(subtask.getStartTime());
            updatedSubtask.setDuration(subtask.getDuration());


            calculateStatus(tempEpic);
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


    private boolean isTaskValid(Task task) {


        LocalDateTime endTime = task.getEndTime();
        Optional<Task> intersect = prioritizedTasks
                .stream()
                .filter(task1 -> task1.getId() != task.getId() && (
                                (!task.getStartTime().isAfter(task1.getStartTime()) && endTime.isAfter(task1.getStartTime()))
                                        || (endTime.isBefore(task1.getEndTime()) && task.getStartTime().isAfter(task1.getStartTime()))
                        )
                )
                .findFirst();

        return intersect.isEmpty();
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


}
