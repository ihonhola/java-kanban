import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testEmptyFile() throws IOException {
        File file = File.createTempFile("Тест", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Сохраняем пустой менеджер
        manager.save();

        // Загружаем обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getTasksList().isEmpty());
        assertTrue(loaded.getEpicTasksList().isEmpty());
        assertTrue(loaded.getSubTasksList().isEmpty());

        file.delete();
    }

    @Test
    void testSaveAndLoadTasks() throws IOException {
        File file = File.createTempFile("Тест", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Добавляем задачи
        manager.createTask(new Task("Задача", "Описание", Status.NEW));
        int epicId = manager.createEpic(new EpicTask("Эпик", "Описание"));
        manager.createSubTask(new SubTask("ПодЗадача", "Описание", Status.DONE, epicId));

        String fileContent = Files.readString(file.toPath());
        System.out.println("Содержимое файла:\n" + fileContent);
        assertTrue(fileContent.contains("EPIC"));
        assertTrue(fileContent.contains("SUBTASK"));
        assertTrue(fileContent.contains("TASK"));

        // Загружаем обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getTasksList().size(), "Неверное количество задач");
        assertEquals(1, loaded.getEpicTasksList().size(), "Неверное количество эпиков");
        assertEquals(1, loaded.getSubTasksList().size(), "Неверное количество подзадач");

        file.delete();
    }
}
