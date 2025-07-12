import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        task = new Task("Задача", "Описание задачи", Status.NEW);
        epic = new EpicTask("Эпик", "Описание эпика");
        subTask = new SubTask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.of(2025, 7, 9, 22, 41));

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
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);

        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId, Duration.ofMinutes(2),
                LocalDateTime.of(2025, 7, 9, 22, 41));
        int subTaskId = taskManager.createSubTask(subTask);

        EpicTask fromManager = taskManager.getEpic(epicId);
        fromManager.getSubTasks().clear();

        assertEquals(0, taskManager.getEpic(epicId).getSubTasks().size());
    }

    @Test
    void shouldUpdateEpicStatusAutomatically() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId,
                Duration.ofMinutes(5), LocalDateTime.of(2025, 7, 9, 22, 42));
        taskManager.createSubTask(subTask);
        SubTask updated = new SubTask(subTask.getName(), subTask.getDescription(),
                Status.DONE, subTask.getEpicId(), subTask.getDuration(), subTask.getStartTime().plusHours(12));
        updated.setId(subTask.getId());

        taskManager.updateSubTask(updated);
        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldNotAllowCircularDependencies() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId,
                Duration.ofMinutes(5), LocalDateTime.of(2025, 7, 9, 22, 42));
        taskManager.createSubTask(subTask);

        SubTask invalid = new SubTask("Ошибка", "Описание", Status.NEW, epic.getId(),
                Duration.ofMinutes(1), LocalDateTime.of(2025, 7, 9, 22, 43));
        invalid.setId(epic.getId());

        assertEquals(0, taskManager.createSubTask(invalid));
    }

    @Test
    void shouldKeepConsistentStateAfterDeletion() {
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId,
                Duration.ofMinutes(2), LocalDateTime.of(2025, 7, 9, 22, 44));
        taskManager.createSubTask(subTask);

        int subTaskId = subTask.getId();
        taskManager.deleteSubTask(subTaskId);

        assertNull(taskManager.getSubTask(subTaskId));
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubTasks().size());
    }

    @Test
    void deleteAllShouldClearHistory() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = taskManager.createTask(task);
        taskManager.getTask(taskId);

        EpicTask epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        taskManager.getEpic(epicId);

        assertFalse(taskManager.getHistory().isEmpty());

        taskManager.deleteAll();

        assertTrue(taskManager.getHistory().isEmpty());
        assertTrue(taskManager.getTasksList().isEmpty());
        assertTrue(taskManager.getEpicTasksList().isEmpty());
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedList() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 10, 0));
        Task task2 = new Task("Задача 2", "Описание", Status.NEW,
                Duration.ofHours(2), LocalDateTime.of(2025, 7, 11, 9, 0));
        Task taskWithoutTime = new Task("Задача 3", "Описание", Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(taskWithoutTime);

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void shouldDetectTimeOverlaps() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 12, 0));

        Task task2 = new Task("Задача 2", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 12, 30));

        assertTrue(manager.hasTimeOverlap(task1, task2));
    }

    @Test
    void shouldPreventOverlappingTasks() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 12, 0));

        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 12, 30));

        assertThrows(FileBackedTaskManager.ManagerSaveException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 12, 12));

        Task task2 = new Task("Задача 2", "Описание", Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 7, 12, 13, 12));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getPrioritizedTasks().size());
    }
}