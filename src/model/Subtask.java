package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String name, String description, Epic epic, TaskStatus status) {
        super(name, description, status);
        this.epic = epic;
    }
    public Subtask(String name, String description, Epic epic, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epic = epic;
    }

    public Subtask(String name, String description, Epic epic, TaskStatus status, long id) {
        super(name, description, status, id);
        this.epic = epic;
    }

    public Subtask(String name, String description, TaskStatus status, long id) {
        super(name, description, status, id);
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "name=" + super.getName() +
                ", description=" + super.getDescription() +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", mainTaskId=" + epic.getId() +
                ", duration=" + super.duration +
                ", startTime=" + super.startTime +
                '}';
    }


    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public Epic getEpic() {
        return epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


}
