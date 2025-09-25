import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testGetEpics() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask("Тестовый эпик", "Описание тестового эпика");
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        EpicTask[] epics = gson.fromJson(response.body(), EpicTask[].class);
        assertEquals(1, epics.length);
        assertEquals("Тестовый эпик", epics[0].getName());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask("Тестовый эпик", "Описание тестового эпика");
        int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи", Status.NEW, epicId,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask[] subtasks = gson.fromJson(response.body(), SubTask[].class);
        assertEquals(1, subtasks.length);
        assertEquals("Подзадача", subtasks[0].getName());
    }
}