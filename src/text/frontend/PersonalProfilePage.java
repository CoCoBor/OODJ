package src.text.frontend;

import java.awt.*;
import javax.swing.*;
import models.User;
import service.ServiceException;
import service.UserService;
import util.SessionManager;

public class PersonalProfilePage extends JPanel {

    private final UserService userService;
    private final SessionManager sessionManager;
    private User currentUser;

    private JTextField idField = new JTextField(15);
    private JTextField usernameField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);

    private JButton saveBtn = new JButton("Save Changes");
    private JButton resetBtn = new JButton("Reset");

    public PersonalProfilePage(UserService userService) {
        this.userService = userService;
        this.sessionManager = SessionManager.getInstance();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("My Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(formPanel, "User ID:", idField, gbc, row++);
        idField.setEditable(false);
        addFormField(formPanel, "Username:", usernameField, gbc, row++);
        usernameField.setEditable(false);
        addFormField(formPanel, "Email:", emailField, gbc, row++);
        addFormField(formPanel, "Phone:", phoneField, gbc, row++);
        addFormField(formPanel, "New Password:", passwordField, gbc, row++);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(saveBtn);
        buttons.add(resetBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttons, gbc);

        add(formPanel, BorderLayout.NORTH);

        setupListeners();
        loadProfile();
    }

    private void addFormField(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gbc);
    }

    private void setupListeners() {
        saveBtn.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "No user is currently logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String newPassword = new String(passwordField.getPassword());

                userService.updateProfile(currentUser.getUserId(), phone, email, newPassword);
                JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                loadProfile();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetBtn.addActionListener(e -> loadProfile());
    }

    public void loadProfile() {
        currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            idField.setText("");
            idField.setEnabled(false);
            usernameField.setText("");
            usernameField.setEnabled(false);
            emailField.setText("");
            phoneField.setText("");
            passwordField.setText("");
            saveBtn.setEnabled(false);
            return;
        }

        idField.setText(currentUser.getUserId());
        idField.setEnabled(false);
        usernameField.setText(currentUser.getUsername());
        usernameField.setEnabled(false);
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());
        passwordField.setText("");
        saveBtn.setEnabled(true);
    }
}
