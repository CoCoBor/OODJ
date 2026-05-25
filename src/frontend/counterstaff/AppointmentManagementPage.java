package frontend.counterstaff;

import backend.models.Appointment;
import backend.models.Technician;
import backend.models.User;
import backend.models.enums.AppointmentStatus;
import backend.models.enums.Role;
import backend.models.enums.ServiceType;
import backend.repository.UserRepository;
import backend.service.AppoinmentService;
import backend.service.ServiceException;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AppointmentManagementPage extends JPanel {

    private final AppoinmentService appointmentService;
    private final UserRepository userRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private final JComboBox<String> customerComboBox;
    private final JComboBox<ServiceType> serviceTypeComboBox;
    private final JTextField scheduledStartField;
    private final JComboBox<String> timeComboBox;
    private final JTextField expectedEndField;
    private final JTextArea notesArea;
    private final JComboBox<AppointmentStatus> statusFilterComboBox;
    private JButton createBtn = new JButton("Create Appointment");
    private JButton assignTechnicianBtn = new JButton("Assign Technician");
    private JButton clearBtn = new JButton("Clear");

    private List<User> customers = new ArrayList<>();
    private List<User> technicians = new ArrayList<>();

    public AppointmentManagementPage(AppoinmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadCustomersAndTechnicians();

        // Initialize table
        String[] columns = {"Appointment ID", "Customer ID", "Technician ID", "Service Type", "Status", "Scheduled Start", "Expected End", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(7).setPreferredWidth(150);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(sorter);

        JPanel tableFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableFilterPanel.add(new JLabel("Filter by Status:"));
        statusFilterComboBox = new JComboBox<>();
        statusFilterComboBox.addItem(null); // "ALL"
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusFilterComboBox.addItem(status);
        }
        statusFilterComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "ALL";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        tableFilterPanel.add(statusFilterComboBox);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableFilterPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        assignTechnicianBtn.setEnabled(false);
        tableButtonPanel.add(assignTechnicianBtn);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        // Initialize form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Appointment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Customer ComboBox
        customerComboBox = new JComboBox<>();
        populateCustomerComboBox();
        addFormField(formPanel, "Customer:", customerComboBox, gbc, row++);

        // Service Type ComboBox
        serviceTypeComboBox = new JComboBox<>(ServiceType.values());
        addFormField(formPanel, "Service Type:", serviceTypeComboBox, gbc, row++);

        // Scheduled Start Date (default to today) and Time selector
        scheduledStartField = new JTextField(10);
        timeComboBox = new JComboBox<>(new String[]{LocalDateTime.now().withHour(9).withMinute(0).format(TIME_FORMATTER), LocalDateTime.now().withHour(11).withMinute(0).format(TIME_FORMATTER), LocalDateTime.now().withHour(15).withMinute(0).format(TIME_FORMATTER)});
        // default date to today
        scheduledStartField.setText(LocalDate.now().format(DATE_FORMATTER));
        addFormField(formPanel, "Scheduled Date (yyyy-MM-dd):", scheduledStartField, gbc, row++);
        addFormField(formPanel, "Time:", timeComboBox, gbc, row++);

        // Expected End DateTime
        expectedEndField = new JTextField(15);
        expectedEndField.setEditable(false);
        expectedEndField.setBackground(Color.LIGHT_GRAY);
        addFormField(formPanel, "Expected End:", expectedEndField, gbc, row++);

        // Notes
        JLabel notesLabel = new JLabel("Notes:");
        notesArea = new JTextArea(3, 15);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(notesArea), gbc);
        row++;

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        buttonPanel.add(createBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);

        setupListeners();
        refreshAppointmentsTable(null);
    }

    private void loadCustomersAndTechnicians() {
        List<User> allUsers = userRepository.findAll();
        customers.clear();
        technicians.clear();
        for (User user : allUsers) {
            if (user.getRole() == Role.CUSTOMER) {
                customers.add(user);
            } else if (user.getRole() == Role.TECHNICIAN) {
                technicians.add(user);
            }
        }
    }

    private void populateCustomerComboBox() {
        customerComboBox.removeAllItems();
        for (User customer : customers) {
            customerComboBox.addItem(customer.getUserId() + " - " + customer.getUsername());
        }
    }

    private void addFormField(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    private void setupListeners() {
        // Service type change listener to auto-calculate end time
        serviceTypeComboBox.addActionListener(e -> updateEndTimeField());

        // Scheduled start field & time selector listeners
        scheduledStartField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateEndTimeField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEndTimeField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateEndTimeField();
            }
        });
        scheduledStartField.addActionListener(e -> updateEndTimeField());
        timeComboBox.addActionListener(e -> updateEndTimeField());

        // Status filter listener
        statusFilterComboBox.addActionListener(e -> {
            AppointmentStatus selectedStatus = (AppointmentStatus) statusFilterComboBox.getSelectedItem();
            refreshAppointmentsTable(selectedStatus);
        });

        // Table selection listener
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting()) {
                int modelRow = appointmentTable.convertRowIndexToModel(row);
                String status = tableModel.getValueAt(modelRow, 4).toString();
                assignTechnicianBtn.setEnabled(status.equals("PENDING"));
            } else {
                assignTechnicianBtn.setEnabled(false);
            }
        });

        // Create appointment button
        createBtn.addActionListener(e -> createAppointment());

        // Assign technician button
        assignTechnicianBtn.addActionListener(e -> assignTechnician());

        // Clear button
        clearBtn.addActionListener(e -> clearForm());
    }

    private void updateEndTimeField() {
        try {
            String datePart = scheduledStartField.getText().trim();
            String timePart = (String) timeComboBox.getSelectedItem();
            if (datePart.isEmpty() || timePart == null || timePart.isEmpty()) {
                expectedEndField.setText("");
                return;
            }

            String startTimeStr = datePart + " " + timePart;
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DATE_TIME_FORMATTER);
            ServiceType serviceType = (ServiceType) serviceTypeComboBox.getSelectedItem();

            switch (serviceType) {
                case NORMAL_SERVICE -> {
                    LocalDateTime endTime = startTime.plusHours(1);
                    expectedEndField.setEditable(false);
                    expectedEndField.setBackground(Color.LIGHT_GRAY);
                    expectedEndField.setText(endTime.format(DATE_TIME_FORMATTER));
                }
                case MAJOR_SERVICE -> {
                    LocalDateTime endTime = startTime.plusHours(3);
                    expectedEndField.setEditable(false);
                    expectedEndField.setBackground(Color.LIGHT_GRAY);
                    expectedEndField.setText(endTime.format(DATE_TIME_FORMATTER));
                }
                case OTHER -> {
                    expectedEndField.setEditable(true);
                    expectedEndField.setBackground(Color.WHITE);
                }
            }
        } catch (DateTimeParseException e) {
            // Invalid date format, ignore
        }
    }

    private void createAppointment() {
        try {
            // Validation
            if (customerComboBox.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Please select a customer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String datePart = scheduledStartField.getText().trim();
            String timePart = (String) timeComboBox.getSelectedItem();
            if (datePart.isEmpty() || timePart == null || timePart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select scheduled date and time.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String endTimeStr = expectedEndField.getText().trim();

            // Parse scheduled start date/time
            LocalDateTime startTime = LocalDateTime.parse(datePart + " " + timePart, DATE_TIME_FORMATTER);
            LocalDateTime endTime;
            ServiceType serviceType = (ServiceType) serviceTypeComboBox.getSelectedItem();

            if (serviceType == ServiceType.OTHER) {
                if (endTimeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter expected end time.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                endTime = LocalDateTime.parse(endTimeStr, DATE_TIME_FORMATTER);
                if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
                    JOptionPane.showMessageDialog(this, "Start time must be before end time.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                // For NORMAL_SERVICE and MAJOR_SERVICE the service will calculate the expected end time.
                endTime = startTime;
            }

            // Extract customer ID
            String customerComboBoxValue = customerComboBox.getSelectedItem().toString();
            String customerId = customerComboBoxValue.split(" - ")[0];

            String notes = notesArea.getText().trim();

            appointmentService.createAppointment(customerId, serviceType, startTime, endTime, notes);
            JOptionPane.showMessageDialog(this, "Appointment created successfully.");
            refreshAppointmentsTable((AppointmentStatus) statusFilterComboBox.getSelectedItem());
            clearForm();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Use yyyy-MM-dd HH:mm", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignTechnician() {
        try {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to assign a technician.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            String appointmentId = tableModel.getValueAt(modelRow, 0).toString();

            // Get available technicians
            loadCustomersAndTechnicians();
            List<User> availableTechnicians = new ArrayList<>();
            for (User tech : technicians) {
                if (tech instanceof Technician technician) {
                    if (technician.getIsAvailable()) {
                        availableTechnicians.add(tech);
                    }
                }
            }

            if (availableTechnicians.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available technicians at the moment.", "No Technicians", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Show dialog to select technician
            String[] technicianOptions = new String[availableTechnicians.size()];
            for (int i = 0; i < availableTechnicians.size(); i++) {
                User tech = availableTechnicians.get(i);
                technicianOptions[i] = tech.getUserId() + " - " + tech.getUsername();
            }

            String selectedTechnician = (String) JOptionPane.showInputDialog(
                this,
                "Select a technician to assign:",
                "Assign Technician",
                JOptionPane.QUESTION_MESSAGE,
                null,
                technicianOptions,
                technicianOptions[0]
            );

            if (selectedTechnician != null) {
                String technicianId = selectedTechnician.split(" - ")[0];
                appointmentService.assignTechnician(appointmentId, technicianId);
                JOptionPane.showMessageDialog(this, "Technician assigned successfully.");
                refreshAppointmentsTable((AppointmentStatus) statusFilterComboBox.getSelectedItem());
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Assignment Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAppointmentsTable(AppointmentStatus statusFilter) {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentService.getAllAppointments(statusFilter);
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getCustomerId(),
                appointment.getTechnicianId() != null ? appointment.getTechnicianId() : "-",
                appointment.getServiceType(),
                appointment.getStatus(),
                appointment.getScheduledStartDateTime().format(DATE_TIME_FORMATTER),
                appointment.getExpectedEndDateTime().format(DATE_TIME_FORMATTER),
                appointment.getNotes()
            });
        }
    }

    private void clearForm() {
        customerComboBox.setSelectedIndex(0);
        serviceTypeComboBox.setSelectedIndex(0);
        scheduledStartField.setText(LocalDate.now().format(DATE_FORMATTER));
        timeComboBox.setSelectedIndex(0);
        notesArea.setText("");
        appointmentTable.clearSelection();
        assignTechnicianBtn.setEnabled(false);
        updateEndTimeField();
    }
}

