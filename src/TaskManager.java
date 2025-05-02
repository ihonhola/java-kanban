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

    public void createSubTask(SubTask subTask, int epicId){
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпик под номером" + epicId + " не существует");
        }
        subTask.setId(nextId++);
        subTask.setEpicId(epicId);
        subTasks.put(subTask.getId(), subTask);
        EpicTask epic = epicTasks.get(epicId);
        epic.addSubtask(subTask);
    }

    public void deleteAllTasks(){
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Все задачи, эпики и подзадачи удалены!");
    }

    public void printAllTasks () { //в ТЗ 2.а.Получение списка всех задач.
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

    public void updateTask(Task updatedTask, int id) {
        if (tasks.containsKey(id)) {
            updatedTask.setId(id);
            tasks.put(id, updatedTask);
            System.out.println("Задача " + id + " обновлена.");
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void updateEpic(EpicTask updatedEpic, int id) {
        if (epicTasks.containsKey(id)) {
            EpicTask existingEpic = epicTasks.get(id);
            existingEpic.setId(id);
            existingEpic.setName(updatedEpic.getName());
            existingEpic.setDescription(updatedEpic.getDescription());
            System.out.println("Эпик " + id + " обновлен");
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    public void updateSubTask(SubTask updatedSubTask, int id) {
            if (!subTasks.containsKey(id)) {
                System.out.println("Подзадачa " + id + " не найдена");
                return;
            }

            SubTask currentSubTask = subTasks.get(id);
            int epicId = currentSubTask.getEpicId();

            updatedSubTask.setId(id);
            updatedSubTask.setEpicId(epicId);

            subTasks.put(id, updatedSubTask);

            EpicTask epic = epicTasks.get(epicId);

            ArrayList<SubTask> epicSubTask = epic.getSubTasks();
            epicSubTask.remove(currentSubTask);

            epic.addSubtask(updatedSubTask);

            System.out.println("Подзадача " + id + " обновлена");
    }

    public void deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача " + taskId + " удалена");
        } else {
            System.out.println("Задачи с номером " + taskId + " не существует");
        }
    }

    public void deleteEpicTask(int epicId) {
        if (!epicTasks.containsKey(epicId)) {
            System.out.println("Эпика с номером " + epicId + " не существует");
            return;
        }

        EpicTask epic = epicTasks.get(epicId);
        ArrayList<SubTask> subTasksOfEpic = epic.getSubTasks();

        for (int i = 0; i < subTasksOfEpic.size(); i++) {
            SubTask currentSubTask = subTasksOfEpic.get(i);
            int idToRemove = currentSubTask.getId();
            subTasks.remove(idToRemove);
        }

        subTasksOfEpic.clear();

        epicTasks.remove(epicId);
        System.out.println("Эпик " + epicId + " и все его подзадачи удалены");
    }

    public void deleteSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Подзадачи с ID " + subTaskId + " не существует");
            return;
        }

        SubTask subTask = subTasks.get(subTaskId);
        int epicId = subTask.getEpicId();
        EpicTask epic = epicTasks.get(epicId);
        subTasks.remove(subTaskId);

        ArrayList<SubTask> subTasksInEpic = epic.getSubTasks();
        for (int i = 0; i < subTasksInEpic.size(); i++) {
            if (subTasksInEpic.get(i).getId() == subTaskId) {
                subTasksInEpic.remove(i);
            }
        }

        epic.updateStatus();

        System.out.println("Подзадача " + subTaskId + " удалена из эпика " + epicId);
    }

    public void printSubTasksByEpic() { //это метод для получения списка всех подзадач определённого эпика
        System.out.println("Введите номер эпика:");
        int epicId = scanner.nextInt();
        if (epicTasks.containsKey(epicId)) {
            EpicTask epic = epicTasks.get(epicId);
            epic.getSubTasks();
            System.out.println(epic.getSubTasks());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    public void deleteSubTasksOfEpic(int epicNumber){ //нет в ТЗ
        if (!epicTasks.containsKey(epicNumber)) {
            System.out.println("Эпика с номером " + epicNumber + " не существует");
            return;
        }

        EpicTask epic = epicTasks.get(epicNumber);
        ArrayList<SubTask> thisEpicSubTasks = epic.getSubTasks();

        if (thisEpicSubTasks.isEmpty()) {
            System.out.println("У эпика " + epicNumber + " нет подзадач для удаления");
            return;
        }

        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }

        thisEpicSubTasks.clear();
        epic.updateStatus();

        System.out.println("Все подзадачи эпика " + epicNumber + " удалены");
    }

    public void deleteAllSubtasks() { //нет в ТЗ
        if (subTasks.isEmpty()){
            System.out.println("В системе нет подзадач для удаления");
            return;
        }

        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            ArrayList<SubTask> thisEpicSubTasks = epic.getSubTasks();
            thisEpicSubTasks.clear();
            //epic.getSubTasks().clear();
            epic.updateStatus(); // Обновляем статус (станет NEW)
        }

        System.out.println("Все подзадачи удалены");
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
}
/* public void deleteTaskByNumber(){
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
    } */

    /* public void updateTaskByNumber() {
        System.out.println("Введите номер задачи:");
        int number = scanner.nextInt();
        scanner.nextLine();

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

            EpicTask oldEpic = epicTasks.get(number);
            EpicTask newEpic = new EpicTask(name, description);

            for (SubTask subTask : oldEpic.getSubTasks()){
                newEpic.addSubtask(subTask);
            }

            newEpic.setId(number);
            epicTasks.put(number, newEpic);
            newEpic.updateStatus();

        } else if (subTasks.containsKey(number)){
            SubTask oldSubTask = subTasks.get(number);
            int epicNumber = oldSubTask.getEpicId();
            System.out.println("Введите название подзадачи:");
            String name = scanner.nextLine();
            System.out.println("Введите описание подзадачи:");
            String description = scanner.nextLine();
            System.out.println("Выберите статус подзадачи:");
            Status status = choiceStatus();

            SubTask newSubTask = new SubTask (name, description, status, epicNumber);
            newSubTask.setId(number);
            subTasks.put(number, newSubTask);

            EpicTask epic = epicTasks.get(epicNumber);
            ArrayList<SubTask> epicSubTasks = epic.getSubTasks();
            for (int i = 0; i < epicSubTasks.size(); i++) {
                SubTask currentTask = epicSubTasks.get(i);
                if (currentTask != null && currentTask.getId() == number) {
                    epicSubTasks.set(i, newSubTask);
                }
            }
            epic.updateStatus();
        } else {
            System.out.println("Задачи под номером " + number + " не существует");
        }
    } */

       /* System.out.println("Введите номер эпика:");
        int epicNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите название подзадачи:");
        String name = scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String description = scanner.nextLine();
        Status status = choiceStatus();
        addSubTask(name, description, status, epicNumber);
    } */

    /* public void addSubTask(String name, String description, Status status, int epicNumber){
        if (!epicTasks.containsKey(epicNumber)) {
            System.out.println("Эпика с номером " + epicNumber + " не существует");
        }
        SubTask subTask = new SubTask(name, description, status, epicNumber);
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);

        EpicTask epic = epicTasks.get(epicNumber);
        epic.addSubtask(subTask);
    } */

    /* public void addTask(String name, String description, Status status){
        Task task = new Task(name, description, status);
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    } */


    /* public void addEpic(String name, String description){
        EpicTask epic = new EpicTask(name, description);
        epic.setId(nextId++);
        epicTasks.put(epic.getId(), epic);
    } */
