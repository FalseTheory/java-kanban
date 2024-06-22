package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private LocalDateTime endTime;

    private final List<Subtask> subTasksList;


    public Epic(String name, String description) {
        super(name, description, null, null, null);
        subTasksList = new ArrayList<>();
    }

    public Epic(String name, String description, long id) {
        this(name, description);
        super.setId(id);
    }

    public void addTask(Subtask subtask) {
        subTasksList.add(subtask);
        calculateTimeAndDuration();
    }

    public void removeTask(Subtask subtask) {
        subTasksList.removeIf(subtask1 -> subtask1.equals(subtask));
        calculateTimeAndDuration();

    }


    @Override
    public String toString() {
        return "Epic{" +
                "name=" + super.getName() +
                ", description=" + super.getDescription() +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", subTasksList=" + subTasksList +
                ", duration=" + super.duration +
                ", startTime=" + super.startTime +
                ", endTime=" + endTime +
                '}';
    }

    public List<Subtask> getSubTasksList() {
        return subTasksList;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void calculateTimeAndDuration() {
        if (subTasksList.isEmpty()) {
            endTime = null;
            startTime = null;
            duration = null;
        } else {
            endTime = LocalDateTime.MIN;
            startTime = LocalDateTime.MAX;
            duration = Duration.ZERO;

            subTasksList.forEach(subtask -> {
                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
                duration = duration.plus(subtask.getDuration());
            });
        }
    }
}
