package backend.models;

public class Vehicle {
    private String ownerTP;
    private String plate;
    private String type; // e.g., "Car", "Motorcycle"

    public Vehicle() {}

    public Vehicle(String ownerTP, String plate, String type) {
        this.ownerTP = ownerTP;
        this.plate = plate;
        this.type = type;
    }

    public String getOwnerTP() { return ownerTP; }
    public void setOwnerTP(String ownerTP) { this.ownerTP = ownerTP; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
