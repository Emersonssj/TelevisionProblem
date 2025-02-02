package adapters;

public class Resource {
    private int id;
    private String name;
    private int totalInstances;

    public Resource(int id, String name, int totalInstances) {
        this.id = id;
        this.name = name;
        this.totalInstances = totalInstances;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getTotalInstances() {
        return totalInstances;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTotalInstances(int totalInstances) {
        this.totalInstances = totalInstances;
    }
}