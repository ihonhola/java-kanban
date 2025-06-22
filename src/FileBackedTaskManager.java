import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic");

            for (Task task : getTasksList()) {
                lines.add(toString(task));
            }

            for (EpicTask epic : getEpicTasksList()) {
                lines.add(toString(epic));
            }

            for (SubTask subTask : getSubTasksList()) {
                lines.add(toString(subTask));
            }

            Files.write(file.toPath(), lines);

            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения в файл", e);
            }
    }


    private String toString(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            return String.join(",",
                    Integer.toString(subTask.getId()),
                    TaskType.SUBTASK.name(),
                    subTask.getName(),
                    subTask.getStatus().name(),
                    subTask.getDescription(),
                    Integer.toString(subTask.getEpicId())
            );
        } else if (task instanceof EpicTask) {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.EPIC.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    ""
            );
        } else {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.TASK.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
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
        if (parts.length < 6) {
            throw new ManagerSaveException("Неверный формат строки: " + value);
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            TaskType type = TaskType.valueOf(parts[1].trim());
            String name = parts[2].trim();
            Status status = Status.valueOf(parts[3].trim());
            String description = parts[4].trim();

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    return task;

                case EPIC:
                    EpicTask epic = new EpicTask(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;

                case SUBTASK:
                    if (parts[5].trim().isEmpty()) {
                        throw new ManagerSaveException("У подзадачи не указан эпик: " + value);
                    }
                    int epicId = Integer.parseInt(parts[5].trim());
                    SubTask subtask = new SubTask(name, description, status, epicId);
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task != null) {
                    if (task instanceof EpicTask) {
                        manager.epicTasks.put(task.getId(), (EpicTask) task);
                    } else if (task instanceof SubTask) {
                        manager.subTasks.put(task.getId(), (SubTask) task);
                        for (SubTask subTask : manager.subTasks.values()) {
                            EpicTask epic = manager.epicTasks.get(subTask.getEpicId());
                            if (epic != null) {
                                epic.addSubTask(subTask);
                            } else {
                                throw new ManagerSaveException("Эпик с id=" + subTask.getEpicId() +
                                        " не найден для подзадачи " + subTask.getId());
                            }
                        }
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }
}

