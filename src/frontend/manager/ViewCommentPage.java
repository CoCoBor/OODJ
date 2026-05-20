package frontend.manager;

import backend.models.CustomerFeedback;
import backend.service.FeedbackService;
import backend.service.ServiceException;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewCommentPage extends JPanel {

    private final FeedbackService feedbackService;
    private JTable table;
    private DefaultTableModel model;

    public ViewCommentPage(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
        setLayout(new BorderLayout(8,8));
        initTable();
        refresh();
    }

    private void initTable() {
        String[] cols = {"Feedback ID", "Appointment ID", "Customer ID", "Technician ID", "Counter Staff ID", "Rating", "Comments", "Created At"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<CustomerFeedback> all = feedbackService.getAllFeedback();
            for (CustomerFeedback f : all) {
                model.addRow(new Object[]{f.getFeedbackId(), f.getAppointmentId(), f.getCustomerId(), f.getTechnicianId(), f.getCounterStaffId(), f.getRating(), f.getComment(), f.getcommentDateTime()});
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
