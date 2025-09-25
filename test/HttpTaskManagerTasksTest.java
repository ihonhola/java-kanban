import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.Gson;

public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Тест", "Тестовая задача", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, tasks.length);
        assertEquals("Тест", tasks[0].getName());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Тест", "Тестовая задача", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals("Тест", responseTask.getName());
        assertEquals(taskId, responseTask.getId());
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Новая задача", "Описание новой задачи", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getTasksList().size());
        assertEquals("Новая задача", manager.getTasksList().get(0).getName());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("На удаление", "Будет удалена", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        int taskId = manager.createTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasksList().size());
    }
}
