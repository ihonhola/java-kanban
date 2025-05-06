import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    private static int nextId = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, SubTask> subTasks;
    Scanner scanner = new Scanner(System.in);

    public TaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public Status choiceStatus(){
        System.out.println("Доступные статусы: ");
        System.out.println("1. " + Status.NEW);
        System.out.println("2. " + Status.IN_PROGRESS);
        System.out.println("3. " + Status.DONE);
        System.out.println("Введите номер статуса:");

        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice){
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

    public void createTask(Task task){
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        /* System.out.println("Введите название задачи: ");
        String name = scanner.nextLine();
        System.out.println("Введите описание задачи:");
        String description = scanner.nextLine();
        Status status = choiceStatus();
        addTask(name, description, status); */
    }

    public void createEpic(EpicTask epicTask){
        epicTask.setId(nextId++);
        epicTasks.put(epicTask.getId(), epicTask);
        /* System.out.println("Введите название эпика:");
        String name = scanner.nextLine();
        System.out.println("Введите описание эпика:");
        String description = scanner.nextLine();
        addEpic(name, description); */
    }

    public void createSubTask(SubTask subTask){
        int epicId = subTask.getEpicId();
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпик под номером" + epicId + " не существует");
            return;
        }

        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);
        EpicTask epic = epicTasks.get(epicId);
        epic.addSubtask(subTask);
    }

    public void deleteAll(){
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все задачи, эпики и подзадачи удалены!");
    }

    public ArrayList<Task> getAllTasksList() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getEpicTasksList() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public void printTaskByNumber() {
        System.out.println("Введите номер задачи:");
        int number = scanner.nextInt();
        if (tasks.containsKey(number)) {
            System.out.println(tasks.get(number));
        } else if (epicTasks.containsKey(number)) {
            System.out.println(epicTasks.get(number));
        } else if (subTasks.containsKey(number)){
            System.out.println(subTasks.get(number));
        } else {
            System.out.println("Задачи под номером " + number + " не существует");
        }
    }

    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();

        if (tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
            System.out.println("Задача " + id + " обновлена.");
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

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

    public void deleteTask(int taskId) {
        if (tasks.remove(taskId) == null) {
            System.out.println("Задачи с номером " + taskId + " не существует");
        } else {
            System.out.println("Задача " + taskId + " удалена");
        }
    }

    public void deleteEpicTask(int epicId) {
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпика с номером " + epicId + " не существует");
            return;
        }

        EpicTask epic = epicTasks.remove(epicId);

        ArrayList<SubTask> subTasksOfEpic = epic.getSubTasks();

        for (int i = 0; i < subTasksOfEpic.size(); i++) {
            SubTask currentSubTask = subTasksOfEpic.get(i);
            subTasks.remove(currentSubTask.getId());
        }

        System.out.println("Эпик " + epicId + " и все его подзадачи удалены");
    }

    public void deleteSubTask(int subTaskId) {
        SubTask subTask = subTasks.remove(subTaskId);

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

    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        if (!epicTasks.containsKey(epicId)) {
            return new ArrayList<>();
        }

        EpicTask epic = epicTasks.get(epicId);
        return new ArrayList<>(epic.getSubTasks());
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все обычные задачи удалены");
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epicTasks.clear();
        System.out.println("Все эпики и их подзадачи удалены");
    }

    public void deleteAllSubtasks() {
        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            epic.removeAllSubTasks();
        }

        System.out.println("Все подзадачи удалены");
    }
}