package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;


import java.util.HashMap;
import java.util.Set;


public class TaskManager {
    private long count = 0;

    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, Subtask> subtasks;
    private final HashMap<Long, Epic> epics;

    public TaskManager(){
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }


    public void clearAllTasks(){
        tasks.clear();
    }
    public void clearAllEpics(){
        epics.clear();
        subtasks.clear();
    }
    public void clearAllSubtasks(){
        subtasks.clear();
        for(Epic epic : epics.values()){
            epic.getSubTasksList().clear();
        }
    }
    public void clearAll(){
        clearAllEpics();
        clearAllTasks();
    }
    public Task getTaskById(Long id){
        return tasks.get(id);
    }
    public Subtask getSubTask(Long id){
        return subtasks.get(id);
    }
    public Epic getEpic(Long id){
        return epics.get(id);
    }
    public Epic createEpic(Epic epic){
        epic.setId(generateId());
        epics.put(epic.getId(),epic);
        calculateStatus(epic);
        return epic;
    }

    public Subtask createSubTask(Subtask subtask){
        subtask.setId(generateId());
        subtask.getEpic().addTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculateStatus(subtask.getEpic());
        return subtask;
    }
    public Task createSimpleTask(Task task){
        task.setId(generateId());
        tasks.put(task.getId(),task);

        return task;
    }
    public void removeTask(Long id){
        tasks.remove(id);
    }
    public void removeSubTask(Long id){
        Subtask tempTask = subtasks.remove(id);
        Epic tempEpic = tempTask.getEpic();
        if(tempEpic!=null){
            tempEpic.removeTask(tempTask);
        }

    }
    public void removeEpicTask(Long id){
        Epic tempEpic = epics.remove(id);
        for(Subtask subtask : tempEpic.getSubTasksList()){
            subtasks.remove(subtask.getId());
        }
    }
    public void updateTask(Task task){
        if(tasks.containsKey(task.getId())){
            tasks.put(task.getId(),task);
        }
    }
    public void updateSubTask(Subtask subtask){
        if(subtasks.containsKey(subtask.getId())){
            Epic tempEpic = subtask.getEpic();
            tempEpic.removeTask(subtask);
            tempEpic.addTask(subtask);
            subtasks.put(subtask.getId(),subtask);
            calculateStatus(subtask.getEpic());
        }

    }
    public void updateEpic(Epic epic){
        Epic savedEpic = epics.get(epic.getId());
        if(savedEpic==null){
            return;
        }
        epic.setName(epic.getName());
        epic.setDescription(epic.getDescription());


    }
    public Set<Subtask> getSubtasksForEpic(Long id){
        Epic epic = epics.get(id);
        if(epic == null){
            return null;
        }
        return epic.getSubTasksList();
    }

    private void calculateStatus(Epic epic){
        Set<Subtask> epicTasks = epic.getSubTasksList();
        if(epicTasks.isEmpty()){
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isDone = true;
        for(Subtask subtask : epicTasks){
            if(subtask.getStatus()!=TaskStatus.DONE){
                epic.setStatus(TaskStatus.IN_PROGRESS);
                isDone= false;
                break;
            }

        }
        if(isDone){
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        for(Subtask subtask : epicTasks){
            if(subtask.getStatus()!=TaskStatus.NEW){
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }

        }
        epic.setStatus(TaskStatus.NEW);

    }
    private long generateId(){
        return ++count;
    }



    public HashMap<Long, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Long, Task> getTasks() {
        return tasks;
    }

    public HashMap<Long, Epic> getEpics() {
        return epics;
    }
}
