package backend.models;

import backend.models.enums.Role;

public class Manager extends User {

    public Manager(String userId, String username, String password, String email, String phone) {
        super(userId, username, password, email, phone, Role.MANAGER);
    }
    
}