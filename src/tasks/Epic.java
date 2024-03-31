package tasks;

import java.util.HashMap;

public class Epic extends Task{

    private HashMap<Long,Subtask> subTasksList;

    public Epic(String name, String description){
        super(name,description);
        subTasksList = new HashMap<>();
    }
    public void addTask(Subtask subtask){
        subTasksList.put(subtask.getId(), subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name="+super.getName() +
                ", description=" + super.getDescription() +
                ", status=" + super.getStatus() +
                ", subTasksList=" + subTasksList +
                '}';
    }

    public HashMap<Long, Subtask> getSubTasksList() {
        return subTasksList;
    }
}
