import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
           System.out.println("Задача пуста");
           return;
        }

        if (history.size() == MAX_HISTORY_SIZE) {
            history.remove(0);
            /* я читал, что может быть лучше использовать LinkedList и метод removeFirst,
            но я его ещё не изучал и не совсем понимаю его отличия от ArrayList
             */
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
