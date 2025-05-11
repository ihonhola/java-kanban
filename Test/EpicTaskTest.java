import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new EpicTask("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
    }

    @Test
    void shouldNotAllowEpicAsSubtask() { //по сути подходит и для класса SubTask
        SubTask invalidSubTask = new SubTask("Невалидная подзадача", "Описание невалидной подзадачи",
                Status.NEW, epic.getId());
        invalidSubTask.setId(epic.getId());

        int result = taskManager.createSubTask(invalidSubTask);

        assertEquals(0, result, "Не должна добавляться подзадача с ID эпика");
        assertEquals(0, taskManager.getSubTasksList().size(), "Список подзадач должен остаться пустым");
    }
}
