package frontend.counterstaff;

import backend.models.Customer;
import backend.models.User;
import backend.models.enums.Role;
import backend.service.ServiceException;
import backend.service.UserService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CustomerManagementPage extends JPanel {

    private final UserService userService;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    private JTextField idField = new JTextField(15);
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JTextField emailField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JTextField vehicleModelField = new JTextField(15);
    private JTextField vehiclePlateField = new JTextField(15);

    private JButton createBtn = new JButton("Create Customer");
    private JButton updateBtn = new JButton("Update Selected");
    private JButton deleteBtn = new JButton("Delete Selected");
    private JButton resetBtn = new JButton("Reset Password");
    private JButton clearBtn = new JButton("Clear");

    public CustomerManagementPage(UserService userService) {
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"User ID", "Username", "Email", "Phone", "Vehicle Model", "Vehicle Plate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Customer Account"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(formPanel, "User ID:", idField, gbc, row++);
        idField.setEditable(false);
        idField.setText("[Auto Generated]");
        addFormField(formPanel, "Username:", usernameField, gbc, row++);
        addFormField(formPanel, "Password:", passField, gbc, row++);
        addFormField(formPanel, "Email:", emailField, gbc, row++);
        addFormField(formPanel, "Phone:", phoneField, gbc, row++);
        addFormField(formPanel, "Vehicle Model:", vehicleModelField, gbc, row++);
        addFormField(formPanel, "Vehicle Plate:", vehiclePlateField, gbc, row++);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPanel.add(createBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        setupListeners();
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

    private void setupListeners() {
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting()) {
                int modelRow = customerTable.convertRowIndexToModel(row);
                idField.setText(tableModel.getValueAt(modelRow, 0).toString());
                usernameField.setText(tableModel.getValueAt(modelRow, 1).toString());
                passField.setText("");
                passField.setEnabled(false);
                emailField.setText(tableModel.getValueAt(modelRow, 2).toString());
                phoneField.setText(tableModel.getValueAt(modelRow, 3).toString());
                vehicleModelField.setText(tableModel.getValueAt(modelRow, 4).toString());
                vehiclePlateField.setText(tableModel.getValueAt(modelRow, 5).toString());

                createBtn.setEnabled(false);
                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }
        });

        createBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passField.getPassword());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String vehicleModel = vehicleModelField.getText().trim();
                String vehiclePlate = vehiclePlateField.getText().trim();

                userService.createCustomer(username, password, phone, email, vehicleModel, vehiclePlate);
                JOptionPane.showMessageDialog(this, "Customer account created successfully.");
                refreshTableData();
                clearForm();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a customer to update.");
                return;
            }
            try {
                String customerId = idField.getText();
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String vehicleModel = vehicleModelField.getText().trim();
                String vehiclePlate = vehiclePlateField.getText().trim();

                userService.updateCustomer(customerId, username, phone, email, vehicleModel, vehiclePlate);
                JOptionPane.showMessageDialog(this, "Customer profile updated successfully.");
                refreshTableData();
                clearForm();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
                return;
            }
            int option = JOptionPane.showConfirmDialog(this, "Delete selected customer permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String customerId = idField.getText();
                    userService.deleteCustomer(customerId);
                    JOptionPane.showMessageDialog(this, "Customer deleted.");
                    refreshTableData();
                    clearForm();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearBtn.addActionListener(e -> clearForm());
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getRole() == Role.CUSTOMER) {
                String vehicleModel = "-";
                String vehiclePlate = "-";
                if (user instanceof Customer customer) {
                    vehicleModel = customer.getVehicleModel();
                    vehiclePlate = customer.getVehiclePlate();
                }
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    vehicleModel,
                    vehiclePlate
                });
            }
        }
    }

    private void clearForm() {
        idField.setText("[Auto Generated]");
        usernameField.setText("");
        passField.setText("");
        passField.setEnabled(true);
        emailField.setText("");
        phoneField.setText("");
        vehicleModelField.setText("");
        vehiclePlateField.setText("");
        createBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        customerTable.clearSelection();
    }
}

