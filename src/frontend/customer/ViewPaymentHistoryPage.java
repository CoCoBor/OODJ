package frontend.customer;

import backend.models.Payment;
import backend.service.PaymentService;
import backend.service.ServiceException;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ViewPaymentHistoryPage extends JPanel {

    private final PaymentService paymentService;
    private final String customerId;
    private final JTable paymentTable;
    private final DefaultTableModel tableModel;

    public ViewPaymentHistoryPage(PaymentService paymentService, String customerId) {
        this.paymentService = paymentService;
        this.customerId = customerId;
        setLayout(new BorderLayout(8, 8));

        JLabel header = new JLabel("My Payment History", SwingConstants.CENTER);
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

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshPaymentsTable());
        add(refreshBtn, BorderLayout.SOUTH);

        refreshPaymentsTable();
    }

    private void refreshPaymentsTable() {
        tableModel.setRowCount(0);
        try {
            List<Payment> payments = paymentService.getAllPaymentsForCustomer(customerId);
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
