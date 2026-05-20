package frontend.technician;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import backend.models.Appointment;
import backend.models.enums.AppointmentStatus;
import backend.service.AppoinmentService;
import backend.service.ServiceException;

public class UpdateAppointmentPage extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppoinmentService appointmentService;
    private final String technicianId;

    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<AppointmentStatus> statusFilterComboBox;
    private JButton updateStatusBtn;

    public UpdateAppointmentPage(AppoinmentService appointmentService, String technicianId) {
        this.appointmentService = appointmentService;
        this.technicianId = technicianId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Technician Appointments", SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.add(headerLabel, BorderLayout.NORTH);
        initControls(topContainer);

        add(topContainer, BorderLayout.NORTH);
        initTable();
        setupListeners();
        refreshAppointmentsTable(null);
    }

    private void initTable() {
        String[] columns = {"Appointment ID", "Customer ID", "Service Type", "Status", "Scheduled Start", "Expected End", "Notes"};
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
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(180);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(sorter);

        add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
    }

    private void initControls(JPanel container) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter by Status:"));

        statusFilterComboBox = new JComboBox<>();
        statusFilterComboBox.addItem(null);
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
        topPanel.add(statusFilterComboBox);

        updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.setEnabled(false);
        topPanel.add(updateStatusBtn);

        container.add(topPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        statusFilterComboBox.addActionListener(e -> refreshAppointmentsTable((AppointmentStatus) statusFilterComboBox.getSelectedItem()));

        appointmentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = appointmentTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
                        String statusValue = tableModel.getValueAt(modelRow, 3).toString();
                        AppointmentStatus status = AppointmentStatus.valueOf(statusValue);
                        updateStatusBtn.setEnabled(status != AppointmentStatus.COMPLETED && status != AppointmentStatus.CANCELLED);
                    } else {
                        updateStatusBtn.setEnabled(false);
                    }
                }
            }
        });

        updateStatusBtn.addActionListener(e -> updateSelectedAppointmentStatus());
    }

    private void refreshAppointmentsTable(AppointmentStatus statusFilter) {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentService.getAppointmentsListForTechnician(technicianId, statusFilter);
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getCustomerId(),
                appointment.getServiceType(),
                appointment.getStatus(),
                appointment.getScheduledStartDateTime().format(DATE_TIME_FORMATTER),
                appointment.getExpectedEndDateTime().format(DATE_TIME_FORMATTER),
                appointment.getNotes()
            });
        }
    }

    private void updateSelectedAppointmentStatus() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        String appointmentId = tableModel.getValueAt(modelRow, 0).toString();
        AppointmentStatus currentStatus = AppointmentStatus.valueOf(tableModel.getValueAt(modelRow, 3).toString());

        List<AppointmentStatus> allowedStatuses = new ArrayList<>();
        switch (currentStatus) {
            case PENDING, CONFIRMED -> {
                allowedStatuses.add(AppointmentStatus.IN_PROGRESS);
                allowedStatuses.add(AppointmentStatus.COMPLETED);
                allowedStatuses.add(AppointmentStatus.CANCELLED);
            }
            case IN_PROGRESS -> {
                allowedStatuses.add(AppointmentStatus.COMPLETED);
                allowedStatuses.add(AppointmentStatus.CANCELLED);
            }
            default -> {
            }
        }

        if (allowedStatuses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No further transitions are allowed for this appointment.", "No Action", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AppointmentStatus selectedStatus = (AppointmentStatus) JOptionPane.showInputDialog(
                this,
                "Select new status:",
                "Update Appointment Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                allowedStatuses.toArray(new AppointmentStatus[0]),
                allowedStatuses.get(0)
        );

        if (selectedStatus == null) {
            return;
        }

        String completionNote = "";
        if (selectedStatus == AppointmentStatus.COMPLETED) {
            completionNote = JOptionPane.showInputDialog(this, "Enter completion notes:", "Completion Notes", JOptionPane.PLAIN_MESSAGE);
            if (completionNote == null || completionNote.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completion notes are required to mark the appointment as completed.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            appointmentService.updateStatus(appointmentId, selectedStatus, completionNote);
            JOptionPane.showMessageDialog(this, "Appointment status updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointmentsTable((AppointmentStatus) statusFilterComboBox.getSelectedItem());
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
