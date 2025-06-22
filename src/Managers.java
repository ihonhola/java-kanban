import java.io.File;
import java.io.IOException;

public class Managers {

    public static FileBackedTaskManager getDefault() {
        File defaultStorageFile;
        try {
            defaultStorageFile = File.createTempFile("File_Backed_Task_Manager", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл для хранения задач", e);
        }
        return new FileBackedTaskManager(defaultStorageFile);
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
