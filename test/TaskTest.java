import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    @Test
    void addNewTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void taskShouldNotChangeAfterAddingToManager() {
        TaskManager manager = Managers.getDefault();
        Task original = new Task("Оригинальная задача", "Описание задачи", Status.NEW);

        int taskId = manager.createTask(original);
        Task saved = manager.getTask(taskId);

        assertNotNull(saved, "Задача должна быть сохранена");
        assertEquals(original.getName(), saved.getName(), "Имя не должно меняться");
        assertEquals(original.getDescription(), saved.getDescription(), "Описание не должно меняться");
        assertEquals(original.getStatus(), saved.getStatus(), "Статус не должен меняться");
    }
}