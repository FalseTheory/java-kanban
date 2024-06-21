package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private final HttpServer server;
    private Gson gson;

    private final TaskManager taskManager;


    public HttpTaskServer() {
        this(Managers.getInMemoryManager());
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));


    }


    public void start() {
        System.out.println("Started user server on port:" + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server has been closed");
    }


    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            private static final DateTimeFormatter TASK_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy:MM:dd;HH:mm");

            @Override
            public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
                jsonWriter.value(localDateTime.format(TASK_DATE_TIME_FORMATTER));
            }

            @Override
            public LocalDateTime read(final JsonReader jsonReader) throws IOException {
                return LocalDateTime.parse(jsonReader.nextString(), TASK_DATE_TIME_FORMATTER);
            }
        });
        gsonBuilder.registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {

            @Override
            public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
                jsonWriter.value(duration.toMinutes());
            }

            @Override
            public Duration read(final JsonReader jsonReader) throws IOException {
                return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
            }
        });
        return gsonBuilder.create();
    }


}

