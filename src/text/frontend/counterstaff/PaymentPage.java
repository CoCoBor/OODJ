package src.text.frontend.counterstaff;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Appointment;
import models.Payment;
import models.enums.PaymentMethod;
import service.PaymentService;
import service.ServiceException;
import service.ServicePriceService;

public class PaymentPage extends JPanel {

    private final PaymentService paymentService;
    private final ServicePriceService priceService;

    private JComboBox<String> appointmentCombo;
    private JComboBox<PaymentMethod> paymentMethodCombo;
    private JLabel priceLabel;
    private JButton createBtn;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;

    private List<Appointment> availableAppointments;

    public PaymentPage(PaymentService paymentService, ServicePriceService priceService) {
        this.paymentService = paymentService;
        this.priceService = priceService;
        setLayout(new BorderLayout(8, 8));
        initControls();
        initTable();
        refreshAppointments();
        refreshPaymentsTable();
    }

    private void initControls() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        top.add(new JLabel("Select Appointment:"), gbc);
        appointmentCombo = new JComboBox<>();
        gbc.gridx = 1;
        top.add(appointmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        top.add(new JLabel("Payment Method:"), gbc);
        paymentMethodCombo = new JComboBox<>(PaymentMethod.values());
        gbc.gridx = 1;
        top.add(paymentMethodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        top.add(new JLabel("Price:"), gbc);
        priceLabel = new JLabel("-" );
        gbc.gridx = 1;
        top.add(priceLabel, gbc);

        createBtn = new JButton("Create Payment");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        top.add(createBtn, gbc);

        add(top, BorderLayout.NORTH);

        appointmentCombo.addActionListener(e -> updatePriceLabel());
        createBtn.addActionListener(e -> onCreatePayment());
    }

    private void initTable() {
        String[] columns = {"Payment ID", "Appointment ID", "Amount", "Method", "Paid At", "Receipt"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentsTable = new JTable(tableModel);
        add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
    }

    private void refreshAppointments() {
        appointmentCombo.removeAllItems();
        try {
            availableAppointments = paymentService.getCompletedButUnpaidAppointment();
            for (Appointment app : availableAppointments) {
                String display = app.getAppointmentId() + " (" + app.getServiceType() + ")";
                appointmentCombo.addItem(display);
            }
            updatePriceLabel();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePriceLabel() {
        int idx = appointmentCombo.getSelectedIndex();
        if (idx < 0 || availableAppointments == null || idx >= availableAppointments.size()) {
            priceLabel.setText("-");
            return;
        }
        Appointment sel = availableAppointments.get(idx);
        try {
            int price = priceService.getPriceByServiceType(sel.getServiceType());
            priceLabel.setText(String.valueOf(price));
        } catch (ServiceException ex) {
            priceLabel.setText("N/A");
        }
    }

    private void onCreatePayment() {
        int idx = appointmentCombo.getSelectedIndex();
        if (idx < 0 || availableAppointments == null || idx >= availableAppointments.size()) {
            JOptionPane.showMessageDialog(this, "No appointment selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Appointment sel = availableAppointments.get(idx);
        PaymentMethod method = (PaymentMethod) paymentMethodCombo.getSelectedItem();
        try {
            Payment p = paymentService.CreatePayment(sel.getAppointmentId(), method);
            String receipt = paymentService.generateReceipt(p);
            JTextArea receiptArea = new JTextArea(receipt);
            receiptArea.setEditable(false);
            receiptArea.setLineWrap(true);
            receiptArea.setWrapStyleWord(true);
            receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            receiptArea.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea), "Payment Created\nReceipt", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointments();
            refreshPaymentsTable();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshPaymentsTable() {
        tableModel.setRowCount(0);
        List<Payment> payments = paymentService.getAllPayments();
        for (Payment p : payments) {
            tableModel.addRow(new Object[]{p.getPaymentId(), p.getAppointmentId(), p.getAmount(), p.getPaymentMethod(), p.getPaidDateTime(), p.getReceiptNumber()});
        }
    }
}

