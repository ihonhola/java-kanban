import java.util.ArrayList;
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
                Status.IN_PROGRESS, 3));
        taskManager.createSubTask(new SubTask("Заказать перевозку", "Яндекс доставка",
                Status.NEW, 3));

        taskManager.createEpic(new EpicTask("Подготовка к командировке", "Конференция в Иркутске"));
        taskManager.createSubTask(new SubTask("Проживание", "Забронировать гостиницу",
                Status.DONE, 6));

        System.out.println("Задачи:" + taskManager.getTasksList());
        System.out.println("Задачи:" + taskManager.getEpicTasksList());
        System.out.println("Задачи:" + taskManager.getSubTasksList());

        System.out.println("Обновляем статусы задач и подзадач (-> эпиков)");

        Task updatedTask1 = new Task ("Начать спринт 5", "Успеть в сроки", Status.IN_PROGRESS);
        updatedTask1.setId(1);
        taskManager.updateTask(updatedTask1);
        Task updatedTask2 = new Task ("Выполнить ФЗ спринта 4", "Исправить замечания", Status.DONE);
        updatedTask2.setId(2);
        taskManager.updateTask(updatedTask2);
        SubTask updatedSubTask1 = new SubTask("Собрать вещи", "Вещи из каждой комнаты",
                Status.DONE, 3);
        updatedSubTask1.setId(4);
        taskManager.updateSubTask(updatedSubTask1);
        SubTask updatedSubTask2 = new SubTask("Заказать перевозку", "Яндекс доставка",
                Status.DONE, 3);
        updatedSubTask2.setId(5);
        taskManager.updateSubTask(updatedSubTask2);
        SubTask updatedSubTask3 = new SubTask("Проживание", "Гостиница забронирована", Status.DONE, 6);
        updatedSubTask3.setId(7);
        taskManager.updateSubTask(updatedSubTask3);

        System.out.println("Задачи:" + taskManager.getTasksList());
        System.out.println("Задачи:" + taskManager.getEpicTasksList());
        System.out.println("Задачи:" + taskManager.getSubTasksList());

        System.out.println("Удаляем одну задачу и один эпик");

        taskManager.deleteTask(1);
        taskManager.deleteEpicTask(3);

        System.out.println("Задачи:" + taskManager.getTasksList());
        System.out.println("Задачи:" + taskManager.getEpicTasksList());
        System.out.println("Задачи:" + taskManager.getSubTasksList());

        while (true) {
            printMenu();
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    ArrayList<Task> allTasks = taskManager.getAllTasksList();
                    for (Task task : allTasks) {
                        System.out.println(task);
                    }
                    break;
                case 2:
                    taskManager.deleteAll(); //можно заменить на удаление по отдельности
                    break;
                case 3:
                    taskManager.printTaskByNumber();
                    break;
                case 4:
                    System.out.println("Введите номер эпика:");
                    int epicId = scanner.nextInt();
                    ArrayList<SubTask> subTasks = taskManager.getSubTasksByEpic(epicId);

                    if (subTasks.isEmpty()) {
                        System.out.println("У эпика нет подзадач или он не существует");
                    } else {
                        System.out.println("Подзадачи эпика " + epicId + ":");
                        for (SubTask subTask : subTasks) {
                            System.out.println(subTask);
                        }
                    }
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
            System.out.println("1 - Посмотреть список всех задач");
            System.out.println("2 - Удалить все задачи");
            System.out.println("3 - Вывести задачу по номеру");
            System.out.println("4 - Посмотреть подзадачи эпика");
            System.out.println("0 - Выход");
    }
}