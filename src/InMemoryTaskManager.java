

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private static int nextId = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, SubTask> subTasks;
    Scanner scanner = new Scanner(System.in);

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Status choiceStatus() {
        System.out.println("Доступные статусы: ");
        System.out.println("1. " + Status.NEW);
        System.out.println("2. " + Status.IN_PROGRESS);
        System.out.println("3. " + Status.DONE);
        System.out.println("Введите номер статуса:");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                return Status.NEW;
            case 2:
                return Status.IN_PROGRESS;
            case 3:
                return Status.DONE;
            default:
                System.out.println("Такого статуса нет. Выберите заново");
        }
        return null;
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }

        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(EpicTask epicTask) {
        if (epicTask == null) {
            return 0;
        }

        epicTask.setId(nextId++);
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (subTask == null || subTask.getEpicId() == 0) {
            return 0;
        }

        if (subTask.getId() == subTask.getEpicId()) {
            System.out.println("Ошибка: Id подзадачи не может совпадать с Id эпика");
            return 0;
        }

        int epicId = subTask.getEpicId();
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпик под номером " + epicId + " не существует");
            return 0;
        }

        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        EpicTask epic = epicTasks.get(epicId);
        epic.addSubtask(subTask);
        return subTask.getId();
    }

    @Override
    public void deleteAll() {
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все задачи, эпики и подзадачи удалены!");
    }

    @Override
    public ArrayList<Task> getAllTasksList() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getEpicTasksList() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void printTaskByNumber() {
        System.out.println("Введите номер задачи:");
        int number = scanner.nextInt();
        scanner.nextLine();

        Task task = getTask(number);
        if (task != null) {
            System.out.println(task);
            return;
        }

        EpicTask epic = getEpic(number);
        if (epic != null) {
            System.out.println(epic);
            return;
        }

        SubTask subTask = getSubTask(number);
        if (subTask != null) {
            System.out.println(subTask);
            return;
        }

        System.out.println("Задачи под номером " + number + " не существует");
    }

    @Override
    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();

        if (tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
            System.out.println("Задача " + id + " обновлена.");
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void updateEpic(EpicTask updatedEpic) {
        int id = updatedEpic.getId();

        if (epicTasks.containsKey(id)) {
            EpicTask existingEpic = epicTasks.get(id);
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            System.out.println("Эпик " + id + " обновлен");
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        int id = updatedSubTask.getId();
        int epicId = updatedSubTask.getEpicId();

        if (!subTasks.containsKey(id)) {
            System.out.println("Ошибка: подзадача с ID=" + id + " не найдена");
            return;
        }

        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Ошибка: эпик с ID=" + epicId + " не существует");
            return;
        }

        subTasks.put(id, updatedSubTask);

        EpicTask epic = epicTasks.get(epicId);
        epic.addSubTask(updatedSubTask);

        System.out.println("Подзадача " + id + " обновлена");
    }

    @Override
    public void deleteTask(int taskId) {
        if (tasks.remove(taskId) == null) {
            System.out.println("Задачи с номером " + taskId + " не существует");
        } else {
            historyManager.remove(taskId);
            System.out.println("Задача " + taskId + " удалена");
        }
    }

    @Override
    public void deleteEpicTask(int epicId) {
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпика с номером " + epicId + " не существует");
            return;
        }

        EpicTask epic = epicTasks.remove(epicId);
        historyManager.remove(epicId);

        ArrayList<SubTask> subTasksOfEpic = epic.getSubTasks();

        for (int i = 0; i < subTasksOfEpic.size(); i++) {
            SubTask currentSubTask = subTasksOfEpic.get(i);
            subTasks.remove(currentSubTask.getId());
            historyManager.remove(currentSubTask.getId());
        }

        System.out.println("Эпик " + epicId + " и все его подзадачи удалены");
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        SubTask subTask = subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);

        if (subTask == null) {
            System.out.println("Подзадачи с ID " + subTaskId + " не существует");
            return;
        }

        EpicTask epic = epicTasks.get(subTask.getEpicId());
        if (epic != null) {
            epic.removeSubTask(subTaskId);
        }

        System.out.println("Подзадача " + subTaskId + " успешно удалена");
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        if (!epicTasks.containsKey(epicId)) {
            return new ArrayList<>();
        }

        EpicTask epic = epicTasks.get(epicId);
        return new ArrayList<>(epic.getSubTasks());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все обычные задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        subTasks.clear();
        epicTasks.clear();
        System.out.println("Все эпики и их подзадачи удалены");
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            epic.removeAllSubTasks();
        }

        System.out.println("Все подзадачи удалены");
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public EpicTask getEpic(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task: historyManager.getHistory()) {
            if (tasks.containsKey(task.getId()) ||
            epicTasks.containsKey(task.getId()) ||
            subTasks.containsKey(task.getId())) {
            result.add(task);
            }
        }
        return result;
    }
}