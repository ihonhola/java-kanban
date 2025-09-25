import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        handleGetEpics(exchange);
                    } else if (path.matches("/epics/\\d+")) {
                        handleGetEpicById(exchange, path);
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        handleGetEpicSubtasks(exchange, path);
                    }
                    break;
                case "POST":
                    handlePostEpic(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        handleDeleteEpic(exchange, path);
                    }
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpicTasksList());
        sendText(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        EpicTask epic = taskManager.getEpic(id);
        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        String response = gson.toJson(taskManager.getSubTasksByEpic(id));
        sendText(exchange, response);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        EpicTask epic = gson.fromJson(body, EpicTask.class);

        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
            sendText(exchange, "{\"id\": " + epic.getId() + "}");
        } else {
            taskManager.updateEpic(epic);
            sendText(exchange, "{\"message\": \"Эпик обновлен\"}");
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        taskManager.deleteEpicTask(id);
        sendText(exchange, "{\"message\": \"Эпик удален\"}");
    }
}