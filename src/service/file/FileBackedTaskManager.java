package service.file;

import exception.ManagerIOException;
import model.*;
import service.HistoryManager;
import service.Managers;
import service.memory.InMemoryTaskManager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }


    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<Task> historyList = super.getHistory();
            if (historyList.isEmpty()) {
                writer.write("\n");
            } else {
                StringBuilder history = new StringBuilder();
                for (Task task : historyList) {
                    history.append(task.getId()).append(",").append(task.getType()).append(",");
                }
                history.deleteCharAt(history.length() - 1);
                writer.write(history + "\n");
            }
            writer.write("id,type,name,status,description,epic\n");
            for (Epic epic : super.epics.values()) {
                writer.write(toString(epic));
            }
            for (Subtask subtask : super.subtasks.values()) {
                writer.write(toString(subtask));
            }
            for (Task task : super.tasks.values()) {
                writer.write(toString(task));
            }

        } catch (IOException e) {
            throw new ManagerIOException("Произошла непредвиденная ошибка при сохранении");
        }

    }

    private Task fromString(String value) {
        String[] items = value.split(",");
        long id = Long.parseLong(items[0]);
        String name = items[2];
        TaskStatus taskStatus = TaskStatus.valueOf(items[3]);
        TaskType type = TaskType.valueOf(items[1]);
        String description = items[4];
        LocalDateTime startTime;
        Duration duration;

        switch (type) {
            case TASK:
                startTime = LocalDateTime.parse(items[6],TASK_DATE_TIME_FORMATTER);
                duration = Duration.ofMinutes(Long.parseLong(items[7]));
                return new Task(name, description, taskStatus, id, duration, startTime);
            case EPIC:
                Epic newEpic = new Epic(name, description, id);
                newEpic.setStatus(TaskStatus.valueOf(items[3]));
                return newEpic;
            case SUBTASK:
                long epicId = Long.parseLong(items[5]);
                startTime = LocalDateTime.parse(items[6],TASK_DATE_TIME_FORMATTER);
                duration = Duration.ofMinutes(Long.parseLong(items[7]));
                return new Subtask(name, description, new Epic(null, null, epicId),
                        taskStatus, id,duration,startTime);
            default:
                return null;
        }

    }

    private String toString(Task task) {
        Long epicId;
        String startTime ;
        Long durationInMinutes;
        if (task.getEpic() == null) {
            epicId = null;
        } else {
            epicId = task.getEpic().getId();
        }
        if(task.getStartTime()==null){
            startTime = null;
        }else{
            startTime = task.getStartTime().format(TASK_DATE_TIME_FORMATTER);
        }
        if(task.getDuration()==null){
            durationInMinutes = null;
        }else{
            durationInMinutes = task.getDuration().toMinutes();
        }
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d\n",
                task.getId(),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId,
                startTime,
                durationInMinutes);
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        Subtask createdSubtask = super.createSubTask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public void removeTask(Long id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(Long id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpicTask(Long id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public Task getTask(Long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubTask(Long id) {
        Subtask subT = super.getSubTask(id);
        save();
        return subT;
    }

    @Override
    public Epic getEpic(Long id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        long countCurrent = 0;
        String[] history;
        List<Subtask> subtaskList = new ArrayList<>();
        FileBackedTaskManager newManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            String line = bReader.readLine();
            if (line != null && !line.isEmpty()) {
                history = line.split(",");
            } else {
                history = new String[0];
            }
            bReader.readLine();
            while (bReader.ready()) {

                Task task = newManager.fromString(bReader.readLine());

                if (task == null) {
                    throw new ManagerIOException("Данные сохранены неправильно или повреждены");
                }
                TaskType type = task.getType();


                switch (type) {
                    case TASK:
                        newManager.tasks.put(task.getId(), task);
                        if (task.getId() > countCurrent) {
                            countCurrent = task.getId();
                        }
                        break;
                    case EPIC:
                        newManager.epics.put(task.getId(), (Epic) task);
                        if (task.getId() > countCurrent) {
                            countCurrent = task.getId();
                        }
                        break;
                    case SUBTASK:
                        if (task.getId() > countCurrent) {
                            countCurrent = task.getId();
                        }
                        subtaskList.add((Subtask) task);
                        break;
                    default:
                        break;
                }


            }
            for (Subtask task : subtaskList) {
                Epic epic = newManager.getEpic(task.getEpic().getId());
                task.setEpic(epic);
                epic.addTask(task);
                newManager.subtasks.put(task.getId(), task);
            }
            for (int i = 0; i < history.length - 1; i += 2) {
                TaskType type = TaskType.valueOf(history[i + 1]);
                switch (type) {
                    case TASK:
                        newManager.getTask(Long.parseLong(history[i]));
                        break;
                    case EPIC:
                        newManager.getEpic(Long.parseLong(history[i]));
                        break;
                    case SUBTASK:
                        newManager.getSubTask(Long.parseLong(history[i]));
                        break;
                    default:
                        break;
                }

            }
        } catch (IOException e) {
            throw new ManagerIOException("Ошибка при восстановлении менеджера из файла");
        } finally {
            newManager.count = countCurrent;
        }

        return newManager;
    }

}
