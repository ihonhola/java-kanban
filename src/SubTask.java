import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Status status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", id=" + super.getId() +
                ", epicId=" + epicId +
                ", duration=" + super.getDuration() +
                ", startTime=" + super.getStartTime() +
                ", epicId=" + epicId +
                '}';
    }
}
