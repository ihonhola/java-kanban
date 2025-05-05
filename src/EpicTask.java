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

    private void updateStatus(){
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

    public void addSubTask(SubTask updatedSubTask) {
        if (updatedSubTask == null || updatedSubTask.getEpicId() != this.getId()) {
            System.out.println("Ошибка - подзадача пуста или не тот номер эпика");
            return;
        }

        for (int i = 0; i < subTasksList.size(); i++) {
            SubTask currentSubTask = subTasksList.get(i);
            if (currentSubTask.getId() == updatedSubTask.getId()) {
                subTasksList.remove(i);
                break;
            }
        }

        subTasksList.add(updatedSubTask);
        updateStatus();
    }

    public void removeSubTask(int subTaskId) {
        for (int i = 0; i < subTasksList.size(); i++) {
            SubTask current = subTasksList.get(i);
            if (current.getId() == subTaskId) {
                subTasksList.remove(i);
                updateStatus();
                return;
            }
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
                ", subTasksCount=" + subTasksList.size() +
                '}';
    }
}
