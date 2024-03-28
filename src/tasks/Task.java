package tasks;

import managers.TaskManager;

import java.util.Objects;

public class Task {

    private final String name;
    private final String description;

    private final long id;
    private TaskStatus status;

    public Task(String name, String description){
        this.name = name;
        this.description = description;
        id = TaskManager.getCount();
        status = TaskStatus.NEW;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id);
    }

    public long getId() {
        return id;
    }
}
