import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        LocalDateTime sub1Start = LocalDateTime.of(2025, 7, 9, 22, 33);
        Duration sub1Duratiton = Duration.ofMinutes(60);
        LocalDateTime sub2Start = LocalDateTime.of(2025, 7, 9, 23, 34);
        Duration sub2Duratiton = Duration.ofHours(1);
        epic = new EpicTask("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        subTask1 = new SubTask("Подзадача 1", "Описание 1", Status.NEW, epicId,
                sub1Duratiton, sub1Start);
        subTask2 = new SubTask("Подзадача 2", "Описание 2", Status.NEW, epicId,
                sub2Duratiton, sub2Start);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
    }

    @Test
    void shouldNotAllowEpicAsSubtask() { //по сути подходит и для класса SubTask
        SubTask invalidSubTask = new SubTask("Невалидная подзадача", "Описание невалидной подзадачи",
                Status.NEW, epic.getId(), Duration.ofMinutes(2), LocalDateTime.of(2025, 7, 9, 22, 37));
        invalidSubTask.setId(epic.getId());

        int result = taskManager.createSubTask(invalidSubTask);

        assertEquals(0, result, "Не должна добавляться подзадача с ID эпика");
        assertEquals(2, taskManager.getSubTasksList().size(), "Должно быть 2 подзадачи");
    }

    @Test
    void shouldRemoveSubTaskFromEpicWhenDeleted() {
        taskManager.deleteSubTask(subTask1.getId());

        EpicTask updatedEpic = taskManager.getEpic(epic.getId());
        assertEquals(1, updatedEpic.getSubTasks().size());
        assertFalse(updatedEpic.getSubTasks().contains(subTask1));
    }

    @Test
    void shouldUpdateEpicStatusWhenSubTaskChanges() {
        SubTask updated = new SubTask(subTask1.getName(), subTask1.getDescription(),
                Status.DONE, subTask1.getEpicId(), subTask1.getDuration(), subTask1.getStartTime().plusHours(6));
        updated.setId(subTask1.getId());

        taskManager.updateSubTask(updated);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldNotContainOrphanedSubTasks() {
        SubTask orphan = new SubTask("Сиротушка", "Описание", Status.NEW, 999,
                Duration.ofDays(999), LocalDateTime.of(2049, 7, 2, 0, 0));
        taskManager.createSubTask(orphan);

        assertNull(taskManager.getSubTask(orphan.getId()),
                "Подзадачи с несуществующим эпиком не должны создаваться");
    }
}
