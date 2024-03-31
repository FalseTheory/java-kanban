package tasks;

public class Subtask extends Task{

    private Epic mainTask;

    public Subtask(String name, String description,Epic mainTask){
        super(name, description);
        this.mainTask = mainTask;
        this.mainTask.addTask(this);
    }
    public long getOwnerId(){
        return mainTask.getId();
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name=" + super.getName() +
                ", description=" + super.getDescription() +
                ", mainTask="+mainTask.getName() +
                ", status="+super.getStatus() +
                '}';
    }
}
