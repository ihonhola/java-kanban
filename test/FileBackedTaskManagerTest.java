import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private  File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

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
        manager.createSubTask(new SubTask("ПодЗадача", "Описание", Status.DONE, epicId,
                Duration.ofMinutes(25), LocalDateTime.of(2025, 7, 9 ,22, 40)));

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

    @Test
    void loadFromFile_shouldLinkSubtasksToEpics() throws IOException {
        File testFile = File.createTempFile("test_tasks", ".csv");

        String testContent = "id,type,name,status,description,duration,startTime,epic\n" +
                "1,EPIC,Эпик1,NEW,Описание1,,,,\n" +
                "2,SUBTASK,Подзадача1,NEW,Описание2,,,1\n" +
                "3,SUBTASK,Подзадача2,DONE,Описание3,,,1";
        Files.writeString(testFile.toPath(), testContent);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(1, manager.getEpicTasksList().size());
        assertEquals(2, manager.getSubTasksList().size());

        EpicTask epic = manager.getEpic(1);
        assertEquals(2, epic.getSubTasks().size(), "У эпика должно быть 2 подзадачи");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS (NEW + DONE)");
    }

    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        FileBackedTaskManager manager = createTaskManager();
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getTasksList().isEmpty());
        assertTrue(loaded.getEpicTasksList().isEmpty());
        assertTrue(loaded.getSubTasksList().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasksWithHistory() throws IOException {
        FileBackedTaskManager manager = createTaskManager();
        Task task = new Task("Задача", "Описание", Status.NEW);
        int taskId = manager.createTask(task);

        EpicTask epic = new EpicTask("Эпик", "Описание");
        int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW, epicId,
                Duration.ofMinutes(30), LocalDateTime.now());
        int subTaskId = manager.createSubTask(subTask);

        manager.getTask(taskId);
        manager.getEpic(epicId);
        manager.getSubTask(subTaskId);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getTasksList().size());
        assertEquals(1, loaded.getEpicTasksList().size());
        assertEquals(1, loaded.getSubTasksList().size());

        assertEquals(3, loaded.getHistory().size(),
                "Должно быть 3 задачи в истории (задача, эпик, подзадача)");

        // Проверяем связь подзадачи с эпиком
        SubTask loadedSubTask = loaded.getSubTasksList().get(0);
        EpicTask loadedEpic = loaded.getEpicTasksList().get(0);
        assertEquals(loadedEpic.getId(), loadedSubTask.getEpicId(),
                "Подзадача должна быть связана с эпиком");
    }
}
