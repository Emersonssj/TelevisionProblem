///////////////////// Arquivo Resource.java /////////////////////////
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

    public String getName() {
        return name;
    }
    public int getTotalInstances() {
        return totalInstances;
    }
}