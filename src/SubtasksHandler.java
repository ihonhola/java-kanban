import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                    if (path.equals("/subtasks")) {
                        handleGetSubtasks(exchange);
                    } else if (path.matches("/subtasks/\\d+")) {
                        handleGetSubtaskById(exchange, path);
                    }
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        handleDeleteSubtask(exchange, path);
                    }
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubTasksList());
        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        SubTask subtask = taskManager.getSubTask(id);
        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        SubTask subtask = gson.fromJson(body, SubTask.class);

        if (subtask.getId() == 0) {
            taskManager.createSubTask(subtask);
            sendText(exchange, "{\"id\": " + subtask.getId() + "}");
        } else {
            taskManager.updateSubTask(subtask);
            sendText(exchange, "{\"message\": \"Подзадача обновлена\"}");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        taskManager.deleteSubTask(id);
        sendText(exchange, "{\"message\": \"Подзадача удалена\"}");
    }
}