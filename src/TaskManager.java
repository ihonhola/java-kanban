import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    static HashMap<Integer, Task> tasks;
    static HashMap<Integer, EpicTask> epicTasks;
    static HashMap<Integer, SubTask> subTasks;
    Scanner scanner = new Scanner(System.in);
    Status status;

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

    public void createTask(){
        System.out.println("Введите название задачи: ");
        String name = scanner.nextLine();
        System.out.println("Введите описание задачи:");
        String description = scanner.nextLine();
        status = choiceStatus();
        addTask(name, description, status);
    }

    public void addTask(String name, String description, Status status){
        Task task = new Task(name, description, status);
        tasks.put(task.getId(), task);
    }

    public void createEpic(){
        System.out.println("Введите название эпика:");
        String name = scanner.nextLine();
        System.out.println("Введите описание эпика:");
        String description = scanner.nextLine();
        addEpic(name, description, Status.NEW);
    }

    public void addEpic(String name, String description, Status status){
        EpicTask epic = new EpicTask(name, description, status);
        epicTasks.put(epic.getId(), epic);
    }

    public void createSubTask(){
        System.out.println("Введите номер эпика:");
        int epicNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите название подзадачи:");
        String name = scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String description = scanner.nextLine();
        status = choiceStatus();
        addSubTask(name, description, status, epicNumber);
    }

    public void addSubTask(String name, String description, Status status, int epicNumber){
        if (!epicTasks.containsKey(epicNumber)) {
            System.out.println("Эпика с номером " + epicNumber + " не существует");
        }
        SubTask subTask = new SubTask(name, description, status, epicNumber);
        subTasks.put(subTask.getId(), subTask);

        EpicTask epic = epicTasks.get(epicNumber);
        epic.addSubtask(subTask);
    }

    public void deleteAllTasks(){
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все задачи, эпики и подзадачи удалены!");
    }

    static void printAllTasks () {
        HashMap<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epicTasks);
        allTasks.putAll(subTasks);

        for (Task task : allTasks.values()) {
            System.out.println(task);
        }
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

    public void updateTaskByNumber() {
        System.out.println("Введите номер задачи:");
        int number = scanner.nextInt();
        if (tasks.containsKey(number)) {
            System.out.println("Введите название задачи:");
            String name = scanner.nextLine();
            System.out.println("Введите описание задачи:");
            String description = scanner.nextLine();
            tasks.put(number, new Task (name, description, Status.NEW));
        } else if (epicTasks.containsKey(number)) {
            System.out.println("Введите название эпика:");
            String name = scanner.nextLine();
            System.out.println("Введите описание эпика:");
            String description = scanner.nextLine();
            epicTasks.put(number, new EpicTask (name, description, Status.NEW));
        } else if (subTasks.containsKey(number)){
            System.out.println("Введите номер эпика:");
            int epicNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Введите название подзадачи:");
            String name = scanner.nextLine();
            System.out.println("Введите описание подзадачи:");
            String description = scanner.nextLine();
            subTasks.put(number, new SubTask (name, description, Status.NEW, epicNumber));
        } else {
            System.out.println("Задачи под номером " + number + " не существует");
        }
    }

    public void deleteTaskByNumber(){
        System.out.println("Введите номер задачи:");
        int number = scanner.nextInt();

        if (tasks.containsKey(number)) {
            tasks.remove(number);
        } else if (epicTasks.containsKey(number)) {
            EpicTask epic = epicTasks.get(number);
            for (SubTask subTask : epic.getSubTasks()){
                subTasks.remove(subTask.getId());
            }
            epicTasks.remove(number);
        } else if (subTasks.containsKey(number)){
            SubTask subTask = subTasks.get(number);
            EpicTask epic = epicTasks.get(subTask.getEpicId());
            subTasks.remove(number);
            ArrayList<SubTask> thisEpicSubTasks = epic.getSubTasks();
            thisEpicSubTasks.remove(subTask);
            epic.updateStatus();
        } else {
            System.out.println("Задачи под номером " + number + " не существует");
        }
    }

    public void printSubTasksByEpic() {
        System.out.println("Введите номер эпика:");
        int epicId = scanner.nextInt();
        if (epicTasks.containsKey(epicId)) {
            EpicTask epic = epicTasks.get(epicId);
            epic.getSubTasks();
            System.out.println(epic.getSubTasks());
            // System.out.println(epicTasks.get(epicId).getSubtasks());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }
}



/* static void printAllTasks () {
        /* for (int i = 0; i <= Task.getId(); i++) {
            if (tasks.containsKey(i+1)) {
                System.out.println("Задача " + (i + 1));
                System.out.println(tasks.get(i+1));
            } else if (epicTasks.containsKey(i+1)) {
                System.out.println("Эпик " + (i + 1));
                System.out.println(epicTasks.get(i+1));
            } else if (subTasks.containsKey(i+1)) {
                System.out.println("Подзадача " + (i + 1));
                System.out.println(subTasks.get(i + 1));
            }
        } */