package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private static long count = 0;

    private static HashMap<Long, Task> taskList;

    public TaskManager(){
        taskList = new HashMap<>();
    }


    public static long getCount() {
        return ++count;
    }

    public void clearAllTasks(){
        taskList.clear();
    }
    public Task getTaskById(Long id){

        return taskList.get(id);
    }
    public void createEpic(Epic epic){
        taskList.put(epic.getId(),epic);
    }
    public void createSubTask(Subtask subtask){
        taskList.put(subtask.getId(), subtask);
    }
    public void createSimpleTask(Task task){
        taskList.put(task.getId(),task);
    }
    public void removeTask(Long id){
        taskList.remove(id);
    }
    public void updateTask(){

    }
    public void printAllTasks(){
        for(Task task : taskList.values()){
            System.out.println(task);
        }
    }
}
