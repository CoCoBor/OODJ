package backend.models;

import backend.models.enums.Role;
import java.util.Objects;

public abstract class User {

    private String userTp;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Role role;

    public User(String userTp, String username, String password, String email, String phone, Role role) {
        setuserTp(userTp);
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setPhone(phone);
        setRole(role);
    }

    public String getuserTp() {
        return userTp;
    }

    public void setuserTp(String userTp) {
        this.userTp = requireNonBlank(userTp, "userTp");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = requireNonBlank(username, "username");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = requireNonBlank(password, "password");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireNonBlank(email, "email");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = requireNonBlank(phone, "phone");
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role cannot be null");
    }

    protected String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
