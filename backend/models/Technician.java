package backend.models;

import backend.models.enums.Role;

public class Technician extends User {

    private Boolean isAvailable;
    private String specialization;


    public Technician(String userTp, String username, String password, String email, String phone, String specialization, Boolean isAvailable) {
        super(userTp, username, password, email, phone, Role.TECHNICIAN);
        setSpecialization(specialization);
        setIsAvailable(isAvailable);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = requireNonBlank(specialization, "specialization");
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
