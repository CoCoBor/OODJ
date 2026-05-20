package backend.models;

import backend.models.enums.Role;

public class Manager extends User {

    public Manager(String userTp, String username, String password, String email, String phone) {
        super(userTp, username, password, email, phone, Role.MANAGER);
    }
    
}