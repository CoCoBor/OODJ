package frontend.customer;

import backend.models.CustomerFeedback;
import backend.models.Payment;
import backend.service.FeedbackService;
import backend.service.PaymentService;
import backend.service.ServiceException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FeedbackPage extends JPanel {

    private final FeedbackService feedbackService;
    private final PaymentService paymentService;
    private final String customerId;

    private JComboBox<String> appointmentCombo;
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentsArea;
    private JButton submitBtn;
    private JTable feedbackTable;
    private DefaultTableModel tableModel;

    private List<Payment> customerPayments = new ArrayList<>();

    public FeedbackPage(FeedbackService feedbackService, PaymentService paymentService, String customerId) {
        this.feedbackService = feedbackService;
        this.paymentService = paymentService;
        this.customerId = customerId;
        setLayout(new BorderLayout(8,8));
        initControls();
        initTable();
        refreshPayments();
        refreshFeedbackTable();
    }

    private void initControls() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        top.add(new JLabel("Select Paid Appointment:"), gbc);
        appointmentCombo = new JComboBox<>();
        gbc.gridx = 1;
        top.add(appointmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        top.add(new JLabel("Rating (1-5):"), gbc);
        ratingCombo = new JComboBox<>(new Integer[]{1,2,3,4,5});
        gbc.gridx = 1;
        top.add(ratingCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        top.add(new JLabel("Comments:"), gbc);
        commentsArea = new JTextArea(3, 30);
        gbc.gridx = 1;
        top.add(new JScrollPane(commentsArea), gbc);

        submitBtn = new JButton("Submit Feedback");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        top.add(submitBtn, gbc);

        add(top, BorderLayout.NORTH);

        submitBtn.addActionListener(e -> onSubmit());
    }

    private void initTable() {
        String[] cols = {"Feedback ID", "Appointment ID", "Rating", "Comments", "Created At"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        feedbackTable = new JTable(tableModel);
        add(new JScrollPane(feedbackTable), BorderLayout.CENTER);
    }

    private void refreshPayments() {
        appointmentCombo.removeAllItems();
        try {
            customerPayments = paymentService.getAllPaymentsForCustomer(customerId);
            for (Payment p : customerPayments) {
                appointmentCombo.addItem(p.getAppointmentId() + " (" + p.getReceiptNumber() + ")");
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshFeedbackTable() {
        tableModel.setRowCount(0);
        List<CustomerFeedback> feedbacks = feedbackService.getCustomerFeedback(customerId);
        for (CustomerFeedback f : feedbacks) {
            tableModel.addRow(new Object[]{f.getFeedbackId(), f.getAppointmentId(), f.getRating(), f.getComment(), f.getcommentDateTime()});
        }
    }

    private void onSubmit() {
        int idx = appointmentCombo.getSelectedIndex();
        if (idx < 0 || idx >= customerPayments.size()) {
            JOptionPane.showMessageDialog(this, "Please select a paid appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String appointmentId = customerPayments.get(idx).getAppointmentId();
        int rating = (Integer) ratingCombo.getSelectedItem();
        String comments = commentsArea.getText();
        try {
            feedbackService.customerFeedback(appointmentId, customerId, rating, comments);
            JOptionPane.showMessageDialog(this, "Feedback submitted", "Success", JOptionPane.INFORMATION_MESSAGE);
            commentsArea.setText("");
            refreshFeedbackTable();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
