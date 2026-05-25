package frontend.customer;

import backend.models.Appointment;
import backend.models.enums.AppointmentStatus;
import backend.service.AppoinmentService;
import backend.service.ServiceException;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ViewServiceHistoryPage extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppoinmentService appointmentService;
    private final String customerId;

    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<AppointmentStatus> statusFilterComboBox;

    public ViewServiceHistoryPage(AppoinmentService appointmentService, String customerId) {
        this.appointmentService = appointmentService;
        this.customerId = customerId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("My Service History", SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(headerLabel, BorderLayout.NORTH);

        initControls();
        initTable();
        refreshAppointmentsTable(null);
    }

    private void initControls() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.add(new JLabel("Filter by Status:"));

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
        controlPanel.add(statusFilterComboBox);

        statusFilterComboBox.addActionListener(e -> refreshAppointmentsTable((AppointmentStatus) statusFilterComboBox.getSelectedItem()));

        add(controlPanel, BorderLayout.PAGE_START);
    }

    private void initTable() {
        String[] columns = {"Appointment ID", "Technician ID", "Service Type", "Status", "Scheduled Start", "Expected End", "Notes"};
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
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(200);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(sorter);

        add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
    }

    private void refreshAppointmentsTable(AppointmentStatus statusFilter) {
        tableModel.setRowCount(0);
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsListForCustomer(customerId, statusFilter);
            for (Appointment appointment : appointments) {
                tableModel.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getTechnicianId() != null ? appointment.getTechnicianId() : "-",
                    appointment.getServiceType(),
                    appointment.getStatus(),
                    appointment.getScheduledStartDateTime().format(DATE_TIME_FORMATTER),
                    appointment.getExpectedEndDateTime().format(DATE_TIME_FORMATTER),
                    appointment.getNotes()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
