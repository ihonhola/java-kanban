import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

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

    @Test
    void shouldUpdateHistoryWhenGettingTasks() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        taskManager.getTask(taskId);
        assertEquals(1, taskManager.getHistory().size());

        taskManager.getTask(taskId); // Дубликат не должен добавляться
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        taskManager.getTask(taskId);
        taskManager.deleteTask(taskId);

        assertTrue(taskManager.getHistory().isEmpty());
    }
    @Test
    void shouldProtectTaskFromDirectModification() {
        Task original = new Task("Original", "Desc", Status.NEW);
        int taskId = taskManager.createTask(original);

        // Пытаемся изменить задачу через сеттер
        Task fromManager = taskManager.getTask(taskId);
        fromManager.setStatus(Status.DONE);

        // Проверяем, что в менеджере ничего не изменилось
        assertNotEquals(fromManager.getStatus(),
                taskManager.getTask(taskId).getStatus());
    }

    @Test
    void shouldCreateDefensiveCopies() {
        EpicTask epic = new EpicTask("Epic", "Desc");
        int epicId = taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Sub", "Desc", Status.NEW, epicId);
        int subTaskId = taskManager.createSubTask(subTask);

        // Модифицируем список подзадач эпика
        EpicTask fromManager = taskManager.getEpic(epicId);
        fromManager.getSubTasks().clear();

        // Проверяем, что реальный эпик не изменился
        assertEquals(1, taskManager.getEpic(epicId).getSubTasks().size());
    }
}