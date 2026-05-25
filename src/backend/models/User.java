package backend.models;

import backend.models.enums.Role;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class User {

    private String userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private LocalDateTime lastActiveTime;
    private Role role;

    public User(String userId, String username, String password, String email, String phone, LocalDateTime lastActiveTime, Role role) {
        setUserId(userId);
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setPhone(phone);
        setLastActiveTime(lastActiveTime);
        setRole(role);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = requireNonBlank(userId, "userId");
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
        String emailChecking = requireNonBlank(email, "email");
        System.out.println("debugging1 email: " + email); // Debug statement
        System.out.println("debugging2 email: " + emailChecking); // Debug statement
        validateEmail(emailChecking);
        this.email = emailChecking;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = requireNonBlank(phone, "phone");
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = Objects.requireNonNull(lastActiveTime, "lastActiveTime cannot be null");
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role cannot be null");
    }

    public static String hashPassword(String password) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    private void validateEmail(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[c][o][m]$");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    protected String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
