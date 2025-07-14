import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected EpicTask epic;
    protected SubTask subTask;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected Task task;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 10, 10, 0);
        Duration duration = Duration.ofHours(1);

        epic = new EpicTask("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        subTask1 = new SubTask("Подзадача 1", "Описание 1", Status.NEW, epicId,
                duration, startTime);
        subTask2 = new SubTask("Подзадача 2", "Описание 2", Status.NEW, epicId,
                duration, startTime.plusHours(2));
        task = new Task("Задача", "Описание задачи", Status.NEW,
                duration, startTime.plusHours(4));
    }

    @Test
    void shouldCreateAndGetTask() {
        int taskId = taskManager.createTask(task);
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void shouldCreateAndGetEpic() {
        int epicId = taskManager.createEpic(epic);
        EpicTask savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void shouldCreateAndGetSubTask() {
        int subTaskId = taskManager.createSubTask(subTask1);
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена");
        assertEquals(subTask1, savedSubTask, "Подзадачи не совпадают");
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubTasks() {
        // a. Все подзадачи NEW
        int epicId = epic.getId();
        int subTaskId1 = taskManager.createSubTask(subTask1);
        int subTaskId2 = taskManager.createSubTask(subTask2);

        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus(),
                "Статус должен быть NEW");

        // b. Все подзадачи DONE
        SubTask updatedSubTask1 = new SubTask(subTask1.getName(), subTask1.getDescription(),
                Status.DONE, subTask1.getEpicId(), subTask1.getDuration(), subTask1.getStartTime());
        updatedSubTask1.setId(subTaskId1);

        SubTask updatedSubTask2 = new SubTask(subTask2.getName(), subTask2.getDescription(),
                Status.DONE, subTask2.getEpicId(), subTask2.getDuration(), subTask2.getStartTime());
        updatedSubTask2.setId(subTaskId2);

        taskManager.updateSubTask(updatedSubTask1);
        taskManager.updateSubTask(updatedSubTask2);

        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus(),
                "Статус должен быть DONE");

        // c. Подзадачи NEW и DONE
        updatedSubTask1.setStatus(Status.NEW);
        taskManager.updateSubTask(updatedSubTask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Статус должен быть IN_PROGRESS");

        // d. Подзадачи IN_PROGRESS
        updatedSubTask1.setStatus(Status.IN_PROGRESS);
        updatedSubTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(updatedSubTask1);
        taskManager.updateSubTask(updatedSubTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Статус должен быть IN_PROGRESS");
    }

    @Test
    void shouldPreventTimeOverlaps() {
        taskManager.createTask(task);

        Task overlappingTask = new Task("Конфликтная задача", "Описание", Status.NEW,
                Duration.ofHours(2), task.getStartTime().plusMinutes(30));

        assertThrows(FileBackedTaskManager.ManagerSaveException.class,
                () -> taskManager.createTask(overlappingTask),
                "Должно выбрасываться исключение при пересечении времени");
    }

    @Test
    void shouldAddTasksToHistory() {
        int taskId = taskManager.createTask(task);
        int epicId = taskManager.createEpic(epic);
        int subTaskId = taskManager.createSubTask(subTask1);

        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getSubTask(subTaskId);

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "В истории должно быть 3 задачи");
        assertEquals(task, history.get(0), "Первая задача в истории не совпадает");
        assertEquals(epic, history.get(1), "Вторая задача в истории не совпадает");
        assertEquals(subTask1, history.get(2), "Третья задача в истории не совпадает");
    }

    @Test
    void shouldRemoveTasksFromHistoryWhenDeleted() {
        int taskId = taskManager.createTask(task);
        int epicId = taskManager.createEpic(epic);

        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);

        taskManager.deleteTask(taskId);
        assertEquals(1, taskManager.getHistory().size(),
                "После удаления задачи история должна уменьшиться");

        taskManager.deleteEpicTask(epicId);
        assertTrue(taskManager.getHistory().isEmpty(),
                "После удаления эпика история должна быть пуста");
    }
}