import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setId(2);
        task3 = new Task("Задача 3", "Описание", Status.NEW);
        task3.setId(3);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void shouldRemoveDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Дубликат

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void shouldPreserveOrder() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Дубликат

        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task2, task1), history);
    }

    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldClearHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.clear();
        assertTrue(historyManager.getHistory().isEmpty(),
                "История должна быть пустой после очистки");
    }
}