import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void shouldReturnInitializedManagers() {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefault();
        assertNotNull(fileBackedTaskManager, "Должен возвращаться проинициализированный FileBackedTaskManager");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Должен возвращаться проинициализированный HistoryManager");

        TaskManager taskManager = Managers.getDefaultTaskManager();
        assertNotNull(taskManager, "Должен возвращаться проинициализированный TaskManager");

    }
}