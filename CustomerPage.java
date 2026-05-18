import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerPage extends JFrame {

    private User currentUser;
    private JTable table;
    private JPanel contentPanel;

    public CustomerPage(User user) {
        this.currentUser = user;

        // ================= FRAME =================
        setTitle("Customer Dashboard");
        setSize(750, 550);
        setLayout(new BorderLayout());

        // ================= TITLE =================
        JLabel title = new JLabel("Customer Dashboard - Welcome, " + currentUser.getName());
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // ================= BUTTON PANEL (2x2 grid like CounterStaffPage) =================
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnProfile  = new JButton("Edit Profile");
        JButton btnHistory  = new JButton("Service History");
        JButton btnPayment  = new JButton("Payment History");
        JButton btnFeedback = new JButton("View Feedbacks");

        buttonPanel.add(btnProfile);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnPayment);
        buttonPanel.add(btnFeedback);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new MainFrame().initialize();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);

        contentPanel = new JPanel(new BorderLayout());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnProfile.addActionListener(e -> showEditProfile());
        btnHistory.addActionListener(e -> showServiceHistory());
        btnPayment.addActionListener(e -> showPaymentHistory());
        btnFeedback.addActionListener(e -> showFeedbacks());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showEditProfile() {

        contentPanel.removeAll();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField nameField = new JTextField(currentUser.getName());
        JTextField tpField = new JTextField(currentUser.getTp());
        tpField.setEditable(false);

        JTextField phoneField = new JTextField(currentUser.getPhone());
        JPasswordField passField = new JPasswordField(currentUser.getPassword());

        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("TP Number:")); panel.add(tpField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Password:")); panel.add(passField);

        JButton btnUpdate = new JButton("Update");
        panel.add(new JLabel("")); panel.add(btnUpdate);

        btnUpdate.addActionListener(e -> {

            try {
                UserFileHandler.checkPhoneNumber(phoneField.getText());
                CounterHandler.updateUser(currentUser.getTp(), nameField.getText(),phoneField.getText(),
                new String(passField.getPassword()),currentUser.getRole());
                currentUser.setName(nameField.getText());
                currentUser.setPhone(phoneField.getText());
                currentUser.setPassword(new String(passField.getPassword()));

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(panel, BorderLayout.CENTER);
        refresh();
    }

    //Show service 
    private void showServiceHistory() {

        contentPanel.removeAll();

        List<String[]> appointments =
                CustomerHandler.getAppointmentsForCustomer(currentUser.getTp());

        String[] cols = {"Service", "Technician", "Date", "Time", "Status"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String[] row : appointments) {

            String status = row.length > 8 ? row[8].trim() : "Pending";

            model.addRow(new Object[]{
                    row[3], row[4], row[6], row[7], status
            });
        }

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() < 2) return;
                int row = table.getSelectedRow();
                if (row == -1) return;
                showCommentDialog(appointments.get(row));
            }
        });
        JLabel hint = new JLabel("Double-click a completed appointment to give feedback.");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));

        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(hint, BorderLayout.SOUTH);
        refresh();
    }

    private void showPaymentHistory() {

        contentPanel.removeAll();

        List<String[]> appointments =
                CustomerHandler.getAppointmentsForCustomer(currentUser.getTp());

        String[] cols = {"Service", "Date", "Price (RM)", "Status"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String[] row : appointments) {

            String status = row.length > 8 ? row[8].trim() : "Pending";

            if (status.equalsIgnoreCase("Completed")) {

                String price =
                        CustomerHandler.getPriceForService(row[3]);

                model.addRow(new Object[]{
                        row[3], row[6], price, "Paid"
                });
            }
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    // ============================================================
    // FEEDBACK
    // ============================================================
    private void showFeedbacks() {

        contentPanel.removeAll();

        List<String[]> feedbacks =
                TechnicianHandler.getAllFeedbacks();

        String[] cols = {"Date", "Time", "Technician", "Feedback"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String[] fb : feedbacks) {

            if (fb[0].trim().equals(currentUser.getTp())) {

                model.addRow(new Object[]{
                        fb[1], fb[2], fb[4], fb[5]
                });
            }
        }

        JTable table = new JTable(model);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    // ============================================================
    // COMMENT DIALOG
    // ============================================================
    private void showCommentDialog(String[] appt) {

        JDialog dialog = new JDialog(this, "Appointment Details", true);
        dialog.setSize(420, 370);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel info = new JPanel(new GridLayout(6, 2, 5, 5));

        String status = appt.length > 8 ? appt[8].trim() : "Pending";

        info.add(new JLabel("Service:")); info.add(new JLabel(appt[3]));
        info.add(new JLabel("Car Plate:")); info.add(new JLabel(appt[2]));
        info.add(new JLabel("Technician:")); info.add(new JLabel(appt[4]));
        info.add(new JLabel("Date:")); info.add(new JLabel(appt[6]));
        info.add(new JLabel("Time:")); info.add(new JLabel(appt[7]));
        info.add(new JLabel("Status:")); info.add(new JLabel(status));

        JTextArea commentArea = new JTextArea(3, 30);
        JScrollPane scroll = new JScrollPane(commentArea);

        JButton btnSave = new JButton("Save Comment");
        JButton btnClose = new JButton("Close");

        JPanel btnRow = new JPanel(new FlowLayout());
        btnRow.add(btnSave);
        btnRow.add(btnClose);

        btnSave.addActionListener(e -> {
            TechnicianHandler.saveCustomerComment(
                    appt[1], appt[6], appt[7], commentArea.getText()
            );
            JOptionPane.showMessageDialog(dialog, "Saved!");
            dialog.dispose();
        });

        btnClose.addActionListener(e -> dialog.dispose());

        dialog.add(info, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============================================================
    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}