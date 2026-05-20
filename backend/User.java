package backend;

public class User {

    private String name;
    private String tp;
    private String phone;
    private String password;
    private String role;

    // Constructor
    public User(String name, String tp, String phone, String password, String role) {
        this.name = name;
        this.tp = tp;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // GETTERS
    public String getName() { return name; }
    public String getTp() { return tp; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // SETTERS
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
