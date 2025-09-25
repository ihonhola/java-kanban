import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod()) && "/prioritized".equals(exchange.getRequestURI().getPath())) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}