package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{

    private ArrayList<Subtask> subTasksList;

    public Epic(String name, String description){
        super(name,description);
        subTasksList = new ArrayList<>();
    }
    public void addTask(Subtask subtask){
        subTasksList.add(subtask);
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

    public ArrayList<Subtask> getSubTasksList() {
        return subTasksList;
    }
}
