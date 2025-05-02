import java.util.ArrayList;

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

    public void updateStatus(){
        if (subTasksList.isEmpty()){
            super.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subTasksList){
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            super.setStatus(Status.NEW);
        } else if (allDone) {
            super.setStatus(Status.DONE);
        } else {
            super.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", id=" + super.getId() +
                ", subTasksCount=" + subTasksList.size() +
                '}';
    }
}
