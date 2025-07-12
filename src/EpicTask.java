import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTask extends Task {
    private ArrayList<SubTask> subTasksList;

    public EpicTask(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksList = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasksList;
    }

    public void addSubtask(SubTask subTask) {
        if (subTask != null) {
            subTasksList.add(subTask);
            updateStatus();
        }
    }

    private void updateStatus() {
        if (subTasksList.isEmpty()) {
            super.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subTasksList.stream()
                .allMatch(subTask -> subTask.getStatus() == Status.NEW);
        boolean allDone = subTasksList.stream()
                .allMatch(subTask -> subTask.getStatus() == Status.DONE);

        if (allNew) {
            super.setStatus(Status.NEW);
        } else if (allDone) {
            super.setStatus(Status.DONE);
        } else {
            super.setStatus(Status.IN_PROGRESS);
        }
    }

    public void addSubTask(SubTask updatedSubTask) {
        if (updatedSubTask == null || updatedSubTask.getEpicId() != this.getId()) {
            System.out.println("Ошибка - подзадача пуста или не тот номер эпика");
            return;
        }

        subTasksList.removeIf(subTask -> subTask.getId() == updatedSubTask.getId());
        subTasksList.add(updatedSubTask);
        updateStatus();
    }

    public void removeSubTask(int subTaskId) {
        if (subTasksList.removeIf(subTask -> subTask.getId() == subTaskId)) {
            updateStatus();
        }
    }

    public void removeAllSubTasks() {
        subTasksList.clear();
        updateStatus();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", id=" + super.getId() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", subTasksCount=" + subTasksList.size() +
                '}';
    }

    @Override
    public Duration getDuration() {
        if (subTasksList.isEmpty()) {
            return Duration.ZERO;
        }
        return Duration.between(getStartTime(), getEndTime());
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subTasksList.isEmpty()) {
            return null;
        }
        return subTasksList.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTasksList.isEmpty()) {
            return null;
        }
        return subTasksList.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}
