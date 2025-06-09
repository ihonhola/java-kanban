import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerAdvancedTest {
    private TaskManager taskManager;
    private EpicTask epic;
    private SubTask subTask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new EpicTask("Эпик", "Описание");
        int epicId = taskManager.createEpic(epic);
        subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId);
        taskManager.createSubTask(subTask);
    }

    @Test
    void shouldUpdateEpicStatusAutomatically() {
        SubTask updated = new SubTask(subTask.getName(), subTask.getDescription(),
                Status.DONE, subTask.getEpicId());
        updated.setId(subTask.getId());

        taskManager.updateSubTask(updated);
        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldNotAllowCircularDependencies() {
        SubTask invalid = new SubTask("Ошибка", "Описание", Status.NEW, epic.getId());
        invalid.setId(epic.getId());

        assertEquals(0, taskManager.createSubTask(invalid));
    }

    @Test
    void shouldKeepConsistentStateAfterDeletion() {
        int subTaskId = subTask.getId();
        taskManager.deleteSubTask(subTaskId);

        assertNull(taskManager.getSubTask(subTaskId));
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubTasks().size());
    }
}