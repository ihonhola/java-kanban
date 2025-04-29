class Task {
    protected String name;
    protected String description;
    public Status status;
    private static int nextId = 1;
    protected int id;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = nextId++;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}


