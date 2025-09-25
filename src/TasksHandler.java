import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
                    if (path.equals("/tasks")) {
                        handleGetTasks(exchange);
                    } else if (path.matches("/tasks/\\d+")) {
                        handleGetTaskById(exchange, path);
                    }
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        handleDeleteTask(exchange, path);
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

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasksList());
        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        Task task = taskManager.getTask(id);
        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            taskManager.createTask(task);
            sendText(exchange, "{\"id\": " + task.getId() + "}");
        } else {
            taskManager.updateTask(task);
            sendText(exchange, "{\"message\": \"Задача обновлена\"}");
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        int id = Integer.parseInt(path.split("/")[2]);
        taskManager.deleteTask(id);
        sendText(exchange, "{\"message\": \"Задача удалена\"}");
    }
}