package frontend.counterstaff;

import backend.models.Appointment;
import backend.models.Payment;
import backend.models.enums.PaymentMethod;
import backend.service.PaymentService;
import backend.service.ServiceException;
import backend.service.ServicePriceService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PaymentPage extends JPanel {

    private final PaymentService paymentService;
    private final ServicePriceService priceService;

    private JComboBox<String> appointmentCombo;
    private JComboBox<PaymentMethod> paymentMethodCombo;
    private JLabel priceLabel;
    private JButton createBtn;
    private JButton generateReceiptBtn;
    private JButton refreshBtn;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;

    private List<Appointment> availableAppointments;
    private List<Payment> payments;

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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        generateReceiptBtn = new JButton("Generate Receipt (Selected Payment)");
        refreshBtn = new JButton("Refresh");
        bottomPanel.add(generateReceiptBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        generateReceiptBtn.addActionListener(e -> onGenerateReceiptFromSelectedPayment());
        refreshBtn.addActionListener(e -> {
            refreshAppointments();
            refreshPaymentsTable();
        });
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
            paymentService.CreatePayment(sel.getAppointmentId(), method);
            JOptionPane.showMessageDialog(this, "Payment created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointments();
            refreshPaymentsTable();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGenerateReceiptFromSelectedPayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0 || payments == null || selectedRow >= payments.size()) {
            JOptionPane.showMessageDialog(this, "Please select a payment row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payment selectedPayment = payments.get(selectedRow);
        try {
            String receipt = paymentService.generateReceipt(selectedPayment);
            showReceiptDialog(receipt);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceiptDialog(String receipt) {
        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setLineWrap(true);
        receiptArea.setWrapStyleWord(true);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receiptArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        JOptionPane.showMessageDialog(this, scrollPane, "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshPaymentsTable() {
        tableModel.setRowCount(0);
        payments = paymentService.getAllPayments();
        for (Payment p : payments) {
            tableModel.addRow(new Object[]{p.getPaymentId(), p.getAppointmentId(), p.getAmount(), p.getPaymentMethod(), p.getPaidDateTime(), p.getReceiptNumber()});
        }
    }
}
