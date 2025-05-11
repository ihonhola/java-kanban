import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        EpicTask epic = new EpicTask("Эпик", "Описание эпика");
        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());

        assertNotNull(task.getId(), "Не найдена задача");
        assertNotNull(epic.getId(), "Не найден эпик");
        assertNotNull(subTask.getId(), "Не найдена подзадача");
    }

    @Test
    void generatedAndManualIdsShouldNotConflict() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        int autoId = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        task2.setId(autoId + 1);
        int manualId = taskManager.createTask(task2);

        assertNotEquals(autoId, manualId, "ID не должны конфликтовать");
        assertEquals(2, taskManager.getTasksList().size(), "Обе задачи должны быть добавлены");
    }
}