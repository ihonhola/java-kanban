import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private EpicTask epic;
    private SubTask subTask;
    private Task task;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        task = new Task("Задача", "Описание задачи", Status.NEW);
        epic = new EpicTask("Эпик", "Описание эпика");
        subTask = new SubTask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());

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
        task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        taskManager.getTask(taskId);
        assertEquals(1, taskManager.getHistory().size());

        taskManager.getTask(taskId); // Дубликат не должен добавляться
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);

        taskManager.getTask(taskId);
        taskManager.deleteTask(taskId);

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldCreateDefensiveCopies() {
        epic = new EpicTask("Epic", "Desc");
        int epicId = taskManager.createEpic(epic);

        subTask = new SubTask("Sub", "Desc", Status.NEW, epicId);
        int subTaskId = taskManager.createSubTask(subTask);

        EpicTask fromManager = taskManager.getEpic(epicId);
        fromManager.getSubTasks().clear();

        assertEquals(0, taskManager.getEpic(epicId).getSubTasks().size());
    }

    @Test
    void shouldUpdateEpicStatusAutomatically() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId);
        taskManager.createSubTask(subTask);
        SubTask updated = new SubTask(subTask.getName(), subTask.getDescription(),
                Status.DONE, subTask.getEpicId());
        updated.setId(subTask.getId());

        taskManager.updateSubTask(updated);
        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldNotAllowCircularDependencies() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId);
        taskManager.createSubTask(subTask);

        SubTask invalid = new SubTask("Ошибка", "Описание", Status.NEW, epic.getId());
        invalid.setId(epic.getId());

        assertEquals(0, taskManager.createSubTask(invalid));
    }

    @Test
    void shouldKeepConsistentStateAfterDeletion() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId);
        taskManager.createSubTask(subTask);

        int subTaskId = subTask.getId();
        taskManager.deleteSubTask(subTaskId);

        assertNull(taskManager.getSubTask(subTaskId));
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubTasks().size());
    }
}