package backend.models;

import backend.models.enums.Role;
import java.time.LocalDateTime;

public class CounterStaff extends User {
    
    public CounterStaff(String userId, String username, String password, String email, String phone, LocalDateTime lastActiveTime) {
        super(userId, username, password, email, phone, lastActiveTime, Role.COUNTER_STAFF);
    }
}
