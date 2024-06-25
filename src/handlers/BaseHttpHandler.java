package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {


    protected void writeResponse(HttpExchange exchange, String text, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] response = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, response.length);
            os.write(response);
        }
    }


}
