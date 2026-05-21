package frontend.manager;

import backend.models.Payment;
import backend.service.PaymentService;
import backend.service.ServiceException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ViewPaymentHistoryPage extends JPanel {

    private final PaymentService paymentService;
    private final JTable paymentTable;
    private final DefaultTableModel tableModel;
    private List<Payment> payments;

    public ViewPaymentHistoryPage(PaymentService paymentService) {
        this.paymentService = paymentService;
        setLayout(new BorderLayout(8, 8));

        JLabel header = new JLabel("Payment History", SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        String[] columns = {"Payment ID", "Appointment ID", "Amount", "Method", "Paid At", "Receipt"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JButton generateReceiptBtn = new JButton("Generate Receipt (Selected Payment)");
        generateReceiptBtn.addActionListener(e -> onGenerateReceiptFromSelectedPayment());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshPaymentsTable());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottomPanel.add(generateReceiptBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshPaymentsTable();
    }

    private void onGenerateReceiptFromSelectedPayment() {
        int selectedRow = paymentTable.getSelectedRow();
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
        scrollPane.setPreferredSize(new java.awt.Dimension(700, 500));
        JOptionPane.showMessageDialog(this, scrollPane, "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshPaymentsTable() {
        tableModel.setRowCount(0);
        try {
            payments = paymentService.getAllPayments();
            for (Payment payment : payments) {
                tableModel.addRow(new Object[] {
                    payment.getPaymentId(),
                    payment.getAppointmentId(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getPaidDateTime(),
                    payment.getReceiptNumber()
                });
            }
        } catch (ServiceException ex) {
            // Ignore errors while loading the payment history table
        }
    }
}
