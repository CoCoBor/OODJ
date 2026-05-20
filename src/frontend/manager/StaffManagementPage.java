package frontend.manager;

import backend.models.Technician;
import backend.models.User;
import backend.models.enums.Role;
import backend.service.ServiceException;
import backend.service.UserService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class StaffManagementPage extends JPanel {
    

    private UserService userService; // Use your explicit UserService
    private JTable staffTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField idField = new JTextField(15);
    private JTextField nameField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JComboBox<Role> roleCombo = new JComboBox<>(new Role[]{Role.MANAGER, Role.COUNTER_STAFF, Role.TECHNICIAN});

    // Extra fields specifically for Technician roles
    private JTextField specField = new JTextField(15);
    private JCheckBox availBox = new JCheckBox("Is Available", true);

    private JButton addBtn = new JButton("Add New");
    private JButton updateBtn = new JButton("Update Selected");
    private JButton deleteBtn = new JButton("Delete Selected");
    private JButton clearBtn = new JButton("Clear Form");

    public StaffManagementPage(UserService userService) {
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Setup Table Side
        String[] columns = {"User ID", "Username", "Role", "Email", "Phone", "Specialisation", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        
        // Add sorting functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);
        
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        // 2. Setup Form Panel Layout Grid
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Staff Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(formPanel, "User ID:", idField, gbc, 0);
        idField.setEditable(false); // Let your IdGenerator handle assignments
        idField.setText("[Auto Generated]");

        addFormField(formPanel, "Username:", nameField, gbc, 1);
        addFormField(formPanel, "Password:", passField, gbc, 2);
        addFormField(formPanel, "Role:", roleCombo, gbc, 3);
        addFormField(formPanel, "Email:", emailField, gbc, 4);
        addFormField(formPanel, "Phone:", phoneField, gbc, 5);
        addFormField(formPanel, "Specs (Tech Only):", specField, gbc, 6);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(availBox, gbc);

        // 3. Command Action Row Panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.EAST);

        setupBusinessLogicListeners();
        refreshTableData();
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

    private void refreshTableData() {
        tableModel.setRowCount(0);

        List<User> users = userService.getAllUsers();
        for (User u : users) {
            if (u.getRole() != Role.CUSTOMER) {
                String spec = (u instanceof Technician t) ? t.getSpecialization() : "-";
                String avail = (u instanceof Technician t) ? String.valueOf(t.getIsAvailable()) : "-";

                tableModel.addRow(new Object[]{
                    u.getUserId(), u.getUsername(), u.getRole(), u.getEmail(), u.getPhone(), spec, avail
                });
            }
        }
    }

    private void setupBusinessLogicListeners() {
        // Form field configuration toggles depending on selected role
        roleCombo.addActionListener(e -> {
            boolean isTech = roleCombo.getSelectedItem() == Role.TECHNICIAN;
            specField.setEnabled(isTech);
            availBox.setEnabled(isTech);
        });

        // Row Selection synchronization
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting()) {
                idField.setText(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                roleCombo.setSelectedItem(tableModel.getValueAt(row, 2));
                emailField.setText(tableModel.getValueAt(row, 3).toString());
                phoneField.setText(tableModel.getValueAt(row, 4).toString());

                String specVal = tableModel.getValueAt(row, 5).toString();
                specField.setText(specVal.equals("-") ? "" : specVal);
                availBox.setSelected(tableModel.getValueAt(row, 6).toString().equals("true"));

                passField.setEnabled(false); // Hide password input field during updates
                roleCombo.setEnabled(false); // Disable role change for existing staff
                addBtn.setEnabled(false);
            }
        });

        // CREATE OPERATION Integration
        addBtn.addActionListener(e -> {
            try {
                Role selectedRole = (Role) roleCombo.getSelectedItem();
                String plainPassword = new String(passField.getPassword());

                User created = userService.createStaff(
                        selectedRole,
                        nameField.getText(),
                        plainPassword,
                        phoneField.getText(),
                        emailField.getText(),
                        specField.getText(),
                        availBox.isSelected()
                );

                if (created != null) {
                    JOptionPane.showMessageDialog(this, "Staff user registered successfully: " + created.getUserId());
                    refreshTableData();
                    clearForm();
                }
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // UPDATE OPERATION Integration
        updateBtn.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a staff member from the list.");
                return;
            }
            try {
                userService.updateStaff(
                        idField.getText(),
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        specField.getText(),
                        availBox.isSelected()
                );
                JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                refreshTableData();
                clearForm();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // DELETE OPERATION Integration
        deleteBtn.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a record to remove.");
                return;
            }
            int option = JOptionPane.showConfirmDialog(this, "Delete staff entry permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    userService.deleteUser(idField.getText());
                    JOptionPane.showMessageDialog(this, "User purged from records.");
                    refreshTableData();
                    clearForm();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Action Blocked", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearBtn.addActionListener(e -> clearForm());
    }

    private void clearForm() {
        idField.setText("[Auto Generated]");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passField.setText("");
        passField.setEnabled(true);
        specField.setText("");
        availBox.setSelected(true);
        roleCombo.setSelectedIndex(0);
        roleCombo.setEnabled(true);
        addBtn.setEnabled(true);
        staffTable.clearSelection();
    }
}
