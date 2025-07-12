import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private String historyToString() {
        return getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,duration,startTime,epic");

            for (Task task : getTasksList()) {
                lines.add(toString(task));
            }

            for (EpicTask epic : getEpicTasksList()) {
                lines.add(toString(epic));
            }

            for (SubTask subTask : getSubTasksList()) {
                lines.add(toString(subTask));
            }

            lines.add(""); //разделитель
            lines.add(historyToString());
            Files.write(file.toPath(), lines);

            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения в файл", e);
            }
    }


    private String toString(Task task) {
        String durationStr;
        String startTimeStr;

        if (task.getDuration() != null) {
            durationStr = String.valueOf(task.getDuration().toMinutes());
        } else {
            durationStr = "";
        }

        if (task.getStartTime() != null) {
            startTimeStr = task.getStartTime().toString();
        } else {
            startTimeStr = "";
        }

        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            return String.join(",",
                    Integer.toString(subTask.getId()),
                    TaskType.SUBTASK.name(),
                    subTask.getName(),
                    subTask.getStatus().name(),
                    subTask.getDescription(),
                    durationStr,
                    startTimeStr,
                    Integer.toString(subTask.getEpicId())
            );
        } else if (task instanceof EpicTask) {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.EPIC.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr,
                    ""
            );
        } else {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.TASK.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr,
                    ""
            );
        }
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(EpicTask epicTask) {
        int id = super.createEpic(epicTask);
        save();
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int id = super.createSubTask(subTask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(EpicTask updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpicTask(int epicId) {
        super.deleteEpicTask(epicId);
        save();
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",", -1); // -1 сохраняет пустые значения в конце
        if (parts.length < 8) {
            throw new ManagerSaveException("Неверный формат строки: " + value);
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            TaskType type = TaskType.valueOf(parts[1].trim());
            String name = parts[2].trim();
            Status status = Status.valueOf(parts[3].trim());
            String description = parts[4].trim();

            Duration duration;
            LocalDateTime startTime;

            if (parts[5].trim().isEmpty()) {
                duration = null;
            } else {
                long minutes = Long.parseLong(parts[5].trim());
                duration = Duration.ofMinutes(minutes);
            }

            if (parts[6].trim().isEmpty()) {
                startTime = null;
            } else {
                startTime = LocalDateTime.parse(parts[6].trim());
            }

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status, duration, startTime);
                    task.setId(id);
                    return task;

                case EPIC:
                    EpicTask epic = new EpicTask(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;

                case SUBTASK:
                    if (parts[7].trim().isEmpty()) {
                        throw new ManagerSaveException("У подзадачи не указан эпик: " + value);
                    }
                    int epicId = Integer.parseInt(parts[7].trim());
                    SubTask subtask = new SubTask(name, description, status, epicId, duration, startTime);
                    subtask.setId(id);
                    return subtask;

                default:
                    throw new ManagerSaveException("Неизвестный тип задачи: " + type);
            }
        } catch (NumberFormatException e) {
            throw new ManagerSaveException("Ошибка преобразования числа в строке: " + value, e);
        }
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }

        public ManagerSaveException(String message) {
            super(message);
        }
    }

    private static void loadHistory(FileBackedTaskManager manager, String historyLine) {
        if (historyLine == null || historyLine.isEmpty()) {
            return;
        }

        String[] taskIds = historyLine.split(",");
        for (String idStr : taskIds) {
            try {
                int id = Integer.parseInt(idStr.trim());
                Task task = manager.tasks.get(id);
                if (task == null) task = manager.epicTasks.get(id);
                if (task == null) task = manager.subTasks.get(id);

                if (task != null) {
                    manager.historyManager.add(task);
                }
            } catch (NumberFormatException e) {
                System.err.println("Неверный ID в истории: " + idStr);
            }
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");
            boolean foundEmptyLine = false;

            for (int i = 1; i < lines.length; i++) { // Начинаем с 1, пропускаем заголовок
                String line = lines[i].trim();

                if (line.isEmpty()) {
                    foundEmptyLine = true;
                } else if (!foundEmptyLine) {
                    // Загружаем задачи до пустой строки
                    Task task = fromString(line);
                    if (task != null) {
                        if (task instanceof EpicTask) {
                            manager.epicTasks.put(task.getId(), (EpicTask) task);
                        } else if (task instanceof SubTask) {
                            manager.subTasks.put(task.getId(), (SubTask) task);
                        } else {
                            manager.tasks.put(task.getId(), task);
                        }
                    }
                } else {
                    // Загружаем историю после пустой строки
                    loadHistory(manager, line);
                    break;
                }
            }

            // Связываем подзадачи с эпиками
            for (SubTask subTask : manager.subTasks.values()) {
                EpicTask epic = manager.epicTasks.get(subTask.getEpicId());
                if (epic != null) {
                    epic.addSubTask(subTask);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }
}

