import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) {
           System.out.println("Задача пуста");
           return;
        }
        // Удаляем старую позицию задачи в истории, если она уже есть
        remove(task.getId());

        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> result = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

    @Override
    public void clear() {
        historyMap.clear();
        head = null;
        tail = null;
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        final Node<Task> next = node.next;
        final Node<Task> prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.data = null;
    }
}
