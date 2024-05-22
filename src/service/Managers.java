package service;

import service.file.FileBackedTaskManager;
import service.memory.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;

import java.io.File;

public class Managers {


    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory(), new File("resources/task22.csv"));
    }


    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }
}
