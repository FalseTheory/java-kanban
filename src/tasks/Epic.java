package tasks;

import java.util.HashMap;

public class Epic extends Task{

    HashMap<Long,Subtask> subTasksList;

    public Epic(String name, String description){
        super(name,description);
    }
}
