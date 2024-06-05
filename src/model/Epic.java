package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subTasksList;


    public Epic(String name, String description) {
        super(name, description, null,null,null);
        subTasksList = new ArrayList<>();
    }

    public Epic(String name, String description, long id) {
        this(name, description);
        super.setId(id);
    }

    public void addTask(Subtask subtask) {
        subTasksList.add(subtask);
    }

    public void removeTask(Subtask subtask) {
        subTasksList.removeIf(subtask1 -> subtask1.equals(subtask));

    }


    @Override
    public String toString() {
        return "Epic{" +
                "name=" + super.getName() +
                ", description=" + super.getDescription() +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subTasksList=" + subTasksList +
                '}';
    }

    public List<Subtask> getSubTasksList() {
        return subTasksList;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }


}
