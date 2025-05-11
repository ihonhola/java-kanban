import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
           System.out.println("Задача пуста");
           return;
        }

        if (history.size() == MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
