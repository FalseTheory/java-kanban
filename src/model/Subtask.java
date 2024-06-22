package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Long epicId;

    public Subtask(String name, String description, Long epicId, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Long epicId, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Long epicId, TaskStatus status, long id, Duration duration, LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Long epicId, TaskStatus status, long id) {
        super(name, description, status, id);
        this.epicId = epicId;
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
                ", mainTaskId=" + epicId +
                ", duration=" + super.duration +
                ", startTime=" + super.startTime +
                '}';
    }


    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    @Override
    public Long getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


}
