import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    taskManager.createTask();
                    break;
                case 2:
                    taskManager.createEpic();
                    break;
                case 3:
                    taskManager.createSubTask();
                    break;
                case 4:
                    taskManager.printAllTasks();
                    break;
                case 5:
                    taskManager.deleteAllTasks();
                    break;
                case 6:
                    taskManager.printTaskByNumber();
                    break;
                case 7:
                    taskManager.updateTaskByNumber();
                    break;
                case 8:
                   taskManager.deleteTaskByNumber();
                    break;
                case 9:
                    taskManager.printSubTasksByEpic();
                    break;
                case 10:
                    System.out.println("Введите номер эпика:");
                    int epicNumber = scanner.nextInt();
                    taskManager.deleteSubTasksOfEpic(epicNumber);
                    break;
                case 11:
                    taskManager.deleteAllSubtasks();
                    break;
                case 0:
                    System.out.println("Адиос амигос!");
                    return;
                default:
                    System.out.println("Неизвестная команда");
                    break;
            }
        }
    }

    public static void printMenu() {
            System.out.println("Введите одну из команд: ");
            System.out.println("1 - Добавить задачу");
            System.out.println("2 - Добавить эпик");
            System.out.println("3 - Добавить субзадачу в эпик");
            System.out.println("4 - Посмотреть список задач");
            System.out.println("5 - Удалить все задачи");
            System.out.println("6 - Вывести задачу по номеру");
            System.out.println("7 - Обновить задачу");
            System.out.println("8 - Удалить задачу по номеру");
            System.out.println("9 - Посмотреть подзадачи эпика");
            System.out.println("10 - Удалить все подзадачи эпика");
            System.out.println("11 - Очистить список подзадач");
            System.out.println("0 - Выход");
        }
}