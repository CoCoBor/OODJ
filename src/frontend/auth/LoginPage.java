package frontend.auth;


import backend.models.User;
import frontend.DashboardPage;
import frontend.MainPage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPage extends JPanel {

    private JTextField emailField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JButton loginBtn = new JButton("Login");

    public LoginPage(MainPage app) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // UI Components
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginBtn, gbc);

        // Login Logic
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passField.getPassword());

            try {
                System.out.println("DEBUG: login button clicked for email = " + email);
                User user = app.getAuthService().login(email, password);
                System.out.println("DEBUG: login success for role = " + user.getRole() + ", user = " + user.getUsername());
                DashboardPage dashboard = app.getDashboardPage();
                dashboard.setupDashboard(user, app.getUserService());
                JOptionPane.showMessageDialog(this,
                        "Welcome " + user.getUsername() + "",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                app.showPage("DASHBOARD");

                if (user.getRole() == backend.models.enums.Role.MANAGER) {
                    System.out.println("DEBUG: starting manager popup timer");
                    dashboard.triggerManagerCommentPopupAfterDelay(2000);
                }
            } catch (backend.service.ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

