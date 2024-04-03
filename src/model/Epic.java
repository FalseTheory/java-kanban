package model;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task{

    private final Set<Subtask> subTasksList; //set


    public Epic(String name, String description){
        super(name,description,null);
        subTasksList = new HashSet<>();
    }
    public Epic(String name, String description, long id){
        this(name,description);
        super.setId(id);
    }
    public void addTask(Subtask subtask){
        subTasksList.add(subtask);
    }
    public void removeTask(Subtask subtask){
        for(Subtask subtask1 : subTasksList){
            if(subtask1.equals(subtask)){
                subTasksList.remove(subtask1);
            }
        }

    }
    @Override
    public String toString() {
        return "Epic{" +
                "name="+super.getName() +
                ", description=" + super.getDescription() +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subTasksList=" + subTasksList +
                '}';
    }

    public Set<Subtask> getSubTasksList() {
        return subTasksList;
    }


}
