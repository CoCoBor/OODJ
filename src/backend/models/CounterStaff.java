package src.backend.models;

import src.backend.models.enums.Role;

public class CounterStaff extends User {
    
    public CounterStaff(String userId, String username, String password, String email, String phone) {
        super(userId, username, password, email, phone, Role.COUNTER_STAFF);
    }
}
