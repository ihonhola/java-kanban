import java.util.ArrayList;

class EpicTask extends Task {
    private ArrayList<SubTask> subTasksList;

    public EpicTask(String name, String description, Status status) {
        super(name, description, status);
        this.subTasksList = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasksList;
    }

    public void addSubtask(SubTask subTask) {
        subTasksList.add(subTask);
        updateStatus();
    }

    public void updateStatus(){
        if (subTasksList.isEmpty()){
            this.status = Status.NEW;
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
            this.status = Status.NEW;
        } else if (allDone) {
            this.status = Status.DONE;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }
}
