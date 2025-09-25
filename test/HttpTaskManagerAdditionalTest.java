import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerAdditionalTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    public HttpTaskManagerAdditionalTest() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача для истории", "Описание исторической задачи",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        // Создаем историю просмотров
        manager.getTask(taskId);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length);
        assertEquals("Задача для истории", history[0].getName());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Тест", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Задача 2", "тесТ", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));

        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritized.length);
        // Должны быть отсортированы по времени
        assertTrue(prioritized[0].getStartTime().isBefore(prioritized[1].getStartTime()));
    }
}
