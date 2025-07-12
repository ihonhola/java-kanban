import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Status choiceStatus();

    int createTask(Task task);

    int createEpic(EpicTask epicTask);

    int createSubTask(SubTask subTask);

    void deleteAll();

    ArrayList<Task> getAllTasksList();

    ArrayList<Task> getTasksList();

    ArrayList<EpicTask> getEpicTasksList();

    ArrayList<SubTask> getSubTasksList();

    void printTaskByNumber();

    void updateTask(Task updatedTask);

    void updateEpic(EpicTask updatedEpic);

    void updateSubTask(SubTask updatedSubTask);

    void deleteTask(int taskId);

    void deleteEpicTask(int epicId);

    void deleteSubTask(int subTaskId);

    ArrayList<SubTask> getSubTasksByEpic(int epicId);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int id);

    SubTask getSubTask(int id);

    EpicTask getEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasTimeOverlap(Task task1, Task task2);

    boolean isTimeOverlapping(Task task);
}
