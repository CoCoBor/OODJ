package src.backend.models;
    
import src.backend.models.enums.Role;

public class Customer extends User {
    private String vehicleModel;
    private String vehiclePlate;
    public Customer(String userId, String username, String password, String email, String phone, String vehicleModel, String vehiclePlate) {
        super(userId, username, password, email, phone, Role.CUSTOMER);
        this.vehicleModel = vehicleModel;
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = requireNonBlank(vehicleModel, "vehicleModel");
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = requireNonBlank(vehiclePlate, "vehiclePlate");
    }
}
