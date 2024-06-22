package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.BadRequestException;
import exception.ManagerIOException;
import exception.NotFoundException;
import exception.ValidationException;

import java.io.IOException;

public class ErrorHandler extends BaseHttpHandler {
    private final Gson gson;

    public ErrorHandler(Gson gson) {
        this.gson = gson;
    }

    private void handle(HttpExchange exchange, ManagerIOException e) throws IOException {
        e.printStackTrace();
        writeResponse(exchange, gson.toJson(e.getMessage()), 500);

    }

    private void handle(HttpExchange exchange, NotFoundException e) throws IOException {
        e.printStackTrace();
        writeResponse(exchange, gson.toJson(e.getMessage()), 404);
    }

    private void handle(HttpExchange exchange, ValidationException e) throws IOException {
        e.printStackTrace();
        writeResponse(exchange, gson.toJson(e.getMessage()), 406);
    }

    private void handle(HttpExchange exchange, BadRequestException e) throws IOException {
        e.printStackTrace();
        writeResponse(exchange, gson.toJson(e.getMessage()), 400);
    }

    public void handle(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof ManagerIOException) {
                handle(exchange, (ManagerIOException) e);
                return;
            }
            if (e instanceof NotFoundException) {
                handle(exchange, (NotFoundException) e);
                return;
            }
            if (e instanceof ValidationException) {
                handle(exchange, (ValidationException) e);
                return;
            }
            if (e instanceof BadRequestException) {
                handle(exchange, (BadRequestException) e);
                return;
            }
            e.printStackTrace();
            writeResponse(exchange, gson.toJson(e.getMessage()), 500);

        } catch (Exception unexpected) {
            unexpected.printStackTrace();
        }
    }
}
