import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void shouldReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Должен возвращаться проинициализированный TaskManager");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Должен возвращаться проинициализированный HistoryManager");
    }
}