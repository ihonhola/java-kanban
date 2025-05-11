import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
    }

    @Test
    void shouldPreserveTaskStateInHistory() {
        historyManager.add(task);
        Task fromHistory = historyManager.getHistory().get(0);

        assertEquals(task.getId(), fromHistory.getId(), "ID должен сохраняться");
        assertEquals(task.getName(), fromHistory.getName(), "Имя должно сохраняться");
        assertEquals(task.getStatus(), fromHistory.getStatus(), "Статус должен сохраняться");
    }

    @Test
    void shouldNotExceedMaxSize() {
        for (int i = 0; i < 15; i++) {
            Task t = new Task("Task" + i, "Desc", Status.NEW);
            t.setId(i);
            historyManager.add(t);
        }

        assertEquals(10, historyManager.getHistory().size(),
                "История не должна превышать 10 элементов");
    }
    @Test
    void add() {
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}