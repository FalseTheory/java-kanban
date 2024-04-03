package model;

import java.util.Objects;

public class Task {

    private String name;
    private String description;

    private long id;
    protected TaskStatus status;

    public Task(String name, String description, TaskStatus status){
        this.name=name;
        this.description = description;
        this.status = status;
    }
    public Task(String name, String description, TaskStatus status, long id){
        this(name,description,status);
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, description, id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }




}