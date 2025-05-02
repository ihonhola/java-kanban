import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        System.out.println("Добавляем 2 задачи, эпик с двумя подзадачами, эпик с одной подзадачей");

        taskManager.createTask(new Task("Начать спринт 5", "Успеть в сроки", Status.NEW));
        taskManager.createTask(new Task("Выполнить ФЗ спринта 4", "Исправить замечания",
                Status.IN_PROGRESS));

        taskManager.createEpic(new EpicTask("Переезд в новую квартиру", "Перевезти все вещи"));
        taskManager.createSubTask(new SubTask("Собрать вещи", "Вещи из каждой комнаты",
                Status.IN_PROGRESS, 3), 3);
        taskManager.createSubTask(new SubTask("Заказать перевозку", "Яндекс доставка",
                Status.NEW, 3), 3);

        taskManager.createEpic(new EpicTask("Подготовка к командировке", "Конференция в Иркутске"));
        taskManager.createSubTask(new SubTask("Проживание", "Забронировать гостиницу",
                Status.DONE, 6), 6);

        System.out.println("Задачи:" + taskManager.getTasks());
        System.out.println("Задачи:" + taskManager.getEpicTasks());
        System.out.println("Задачи:" + taskManager.getSubTasks());

        System.out.println("Обновляем одну задачу, один эпик и две подзадачи к нему");

        taskManager.updateTask(new Task("Закончить спринт 4", "Потом приступить к спринту 5",
                Status.IN_PROGRESS), 1);
        taskManager.updateEpic(new EpicTask("Переезд в новую квартиру", "Найти новую квартиру"),
                3);
        taskManager.updateSubTask(new SubTask("Поиск квартиры", "Посмотреть квартиру на ЦИАНе",
                Status.DONE, 3), 4);
        taskManager.updateSubTask(new SubTask("Сбор вещей", "Купить коробки для переезда",
                Status.DONE, 3), 5);

        System.out.println("Задачи:" + taskManager.getTasks());
        System.out.println("Задачи:" + taskManager.getEpicTasks());
        System.out.println("Задачи:" + taskManager.getSubTasks());

        System.out.println("Удаляем одну задачу, один эпик и одну подзадачу эпика");

        taskManager.deleteTask(1);
        taskManager.deleteEpicTask(3);
        taskManager.deleteSubTask(7);

        System.out.println("Задачи:" + taskManager.getTasks());
        System.out.println("Задачи:" + taskManager.getEpicTasks());
        System.out.println("Задачи:" + taskManager.getSubTasks());

        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    taskManager.printAllTasks();
                    break;
                case 2:
                    taskManager.deleteAllTasks();
                    break;
                case 3:
                    taskManager.printTaskByNumber();
                    break;
                case 4:
                    taskManager.printSubTasksByEpic();
                    break;
                case 5:
                    System.out.println("Введите номер эпика:");
                    int epicNumber = scanner.nextInt();
                    taskManager.deleteSubTasksOfEpic(epicNumber);
                    break;
                case 6:
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
            System.out.println("1 - Посмотреть список задач");
            System.out.println("2 - Удалить все задачи");
            System.out.println("3 - Вывести задачу по номеру");
            System.out.println("4 - Посмотреть подзадачи эпика");
            System.out.println("5 - Удалить все подзадачи эпика");
            System.out.println("6 - Очистить список подзадач");
            System.out.println("0 - Выход");
    }
}