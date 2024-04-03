package model;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String name, String description, Epic epic, TaskStatus status) {
        super(name, description, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, Epic epic, TaskStatus status, long id) {
        super(name, description, status, id);
        this.epic = epic;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "name=" + super.getName() +
                ", description=" + super.getDescription() +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", mainTaskId=" + epic.getId() +
                '}';
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
