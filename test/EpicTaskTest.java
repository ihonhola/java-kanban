import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new EpicTask("Эпик", "Описание эпика");
        int epicId = taskManager.createEpic(epic);

        subTask1 = new SubTask("Подзадача 1", "Описание 1", Status.NEW, epicId);
        subTask2 = new SubTask("Подзадача 2", "Описание 2", Status.NEW, epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
    }

    @Test
    void shouldNotAllowEpicAsSubtask() { //по сути подходит и для класса SubTask
        SubTask invalidSubTask = new SubTask("Невалидная подзадача", "Описание невалидной подзадачи",
                Status.NEW, epic.getId());
        invalidSubTask.setId(epic.getId());

        int result = taskManager.createSubTask(invalidSubTask);

        assertEquals(0, result, "Не должна добавляться подзадача с ID эпика");
        //assertTrue(taskManager.getSubTasksList().isEmpty());
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
                Status.DONE, subTask1.getEpicId());
        updated.setId(subTask1.getId());

        taskManager.updateSubTask(updated);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldNotContainOrphanedSubTasks() {
        SubTask orphan = new SubTask("Сиротушка", "Описание", Status.NEW, 999);
        taskManager.createSubTask(orphan);

        assertNull(taskManager.getSubTask(orphan.getId()),
                "Подзадачи с несуществующим эпиком не должны создаваться");
    }
}
