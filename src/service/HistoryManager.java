package service;

import model.Task;

import java.util.List;

public interface HistoryManager {


    List<Task> getHistory();

    <T extends Task> void add(T task);

    void remove(long id);
}
