package src.text.frontend.auth;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import models.User;
import src.text.frontend.DashboardPage;
import src.text.frontend.MainPage;

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
                User user = app.getAuthService().login(email, password);
                DashboardPage dashboard = app.getDashboardPage();
                dashboard.setupDashboard(user, app.getUserService());
                JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername());
                app.showPage("DASHBOARD");
            } catch (service.ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

