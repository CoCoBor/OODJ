import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TechnicianPage extends JFrame {

    private User currentUser;
    private JPanel contentPanel;

    public TechnicianPage(User user) {
        this.currentUser = user;

        setTitle("Technician Dashboard");
        setSize(750, 550);
        setLayout(new BorderLayout());

        // ===== TOP TITLE =====
        JLabel title = new JLabel("Technician Dashboard - Welcome, " + currentUser.getName());
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton btnProfile      = new JButton("Edit Profile");
        JButton btnAppointments = new JButton("My Appointments");
        JButton btnFeedback     = new JButton("My Feedbacks");

        buttonPanel.add(btnProfile);
        buttonPanel.add(btnAppointments);
        buttonPanel.add(btnFeedback);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // ===== LOGOUT =====
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new MainFrame().initialize();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);

        // ===== CENTER CONTENT =====
        contentPanel = new JPanel(new BorderLayout());

        // ===== ADD TO FRAME =====
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        btnProfile.addActionListener(e -> showEditProfile());
        btnAppointments.addActionListener(e -> showMyAppointments());
        btnFeedback.addActionListener(e -> showMyFeedbacks());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ============================================================
    //  EDIT PROFILE
    // ============================================================
    private void showEditProfile() {
        contentPanel.removeAll();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField nameField     = new JTextField(currentUser.getName());
        JTextField tpField       = new JTextField(currentUser.getTp());
        tpField.setEditable(false);
        JTextField phoneField    = new JTextField(currentUser.getPhone());
        JPasswordField passField = new JPasswordField(currentUser.getPassword());

        panel.add(new JLabel("Name:"));      panel.add(nameField);
        panel.add(new JLabel("TP Number:")); panel.add(tpField);
        panel.add(new JLabel("Phone:"));     panel.add(phoneField);
        panel.add(new JLabel("Password:"));  panel.add(passField);

        JButton btnUpdate = new JButton("Update");
        panel.add(new JLabel("")); panel.add(btnUpdate);

        btnUpdate.addActionListener(e -> {
            String newName  = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newPass  = new String(passField.getPassword()).trim();

            if (newName.isEmpty() || newPhone.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            try {
                UserFileHandler.checkPhoneNumber(newPhone);
                CounterHandler.updateUser(currentUser.getTp(), newName, newPhone, newPass, currentUser.getRole());
                currentUser.setName(newName);
                currentUser.setPhone(newPhone);
                currentUser.setPassword(newPass);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(panel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ============================================================
    //  MY APPOINTMENTS
    // ============================================================
    private void showMyAppointments() {
        contentPanel.removeAll();

        List<String[]> appointments = TechnicianHandler.getAppointmentsForTechnician(currentUser.getTp());

        String[] cols = {"Customer", "Car Plate", "Service", "Date", "Time", "Status", "Comment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String[] row : appointments) {
            String status  = row.length > 8 ? row[8].trim() : "Pending";
            String comment = row.length > 9 ? row[9].trim() : "";
            model.addRow(new Object[]{
                row[0], row[2], row[3], row[6], row[7], status, comment
            });
        }

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() < 2) return;
                int row = table.getSelectedRow();
                if (row == -1) return;

                String[] appt = appointments.get(row);
                String currentStatus = appt.length > 8 ? appt[8].trim() : "Pending";

                if (currentStatus.equalsIgnoreCase("Completed")) {
                    JOptionPane.showMessageDialog(TechnicianPage.this,
                        "This appointment is already completed.");
                    return;
                }

                showAppointmentDetail(appt, row);
            }
        });

        JLabel hint = new JLabel("Double-click a pending appointment to update or give feedback.");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));

        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(hint, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ============================================================
    //  APPOINTMENT DETAIL DIALOG
    // ============================================================
    private void showAppointmentDetail(String[] appt, int index) {
        JDialog dialog = new JDialog(this, "Appointment Detail", true);
        dialog.setSize(450, 380);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel info = new JPanel(new GridLayout(7, 2, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String comment = appt.length > 9 ? appt[9].trim() : "(none)";
        String status  = appt.length > 8 ? appt[8].trim() : "Pending";

        info.add(new JLabel("Customer:"));         info.add(new JLabel(appt[0]));
        info.add(new JLabel("Car Plate:"));        info.add(new JLabel(appt[2]));
        info.add(new JLabel("Service:"));          info.add(new JLabel(appt[3]));
        info.add(new JLabel("Date:"));             info.add(new JLabel(appt[6]));
        info.add(new JLabel("Time:"));             info.add(new JLabel(appt[7]));
        info.add(new JLabel("Status:"));           info.add(new JLabel(status));
        info.add(new JLabel("Customer Comment:")); info.add(new JLabel(comment));

        JLabel feedbackLabel = new JLabel("Your Feedback for this Job:");
        String existingFeedback = TechnicianHandler.getFeedbackForAppointment(appt[1], appt[6], appt[7]);
        JTextArea feedbackArea = new JTextArea(existingFeedback, 3, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);

        JPanel feedbackPanel = new JPanel(new BorderLayout(5, 5));
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        feedbackPanel.add(feedbackLabel, BorderLayout.NORTH);
        feedbackPanel.add(feedbackScroll, BorderLayout.CENTER);

        JButton btnComplete     = new JButton("Mark as Completed");
        JButton btnSaveFeedback = new JButton("Save Feedback");
        JButton btnClose        = new JButton("Close");

        JPanel btnRow = new JPanel(new FlowLayout());
        btnRow.add(btnComplete);
        btnRow.add(btnSaveFeedback);
        btnRow.add(btnClose);

        btnComplete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Mark this appointment as Completed?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                TechnicianHandler.updateAppointmentStatus(appt[1], appt[6], appt[7], "Completed");
                JOptionPane.showMessageDialog(dialog, "Appointment marked as Completed.");
                dialog.dispose();
                showMyAppointments();
            }
        });

        btnSaveFeedback.addActionListener(e -> {
            String fb = feedbackArea.getText().trim();
            if (fb.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter feedback before saving.");
                return;
            }
            TechnicianHandler.saveFeedback(appt[1], appt[6], appt[7], currentUser.getTp(), currentUser.getName(), fb);
            JOptionPane.showMessageDialog(dialog, "Feedback saved successfully.");
            dialog.dispose();
        });

        btnClose.addActionListener(e -> dialog.dispose());

        dialog.add(info, BorderLayout.NORTH);
        dialog.add(feedbackPanel, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============================================================
    //  MY FEEDBACKS
    // ============================================================
    private void showMyFeedbacks() {
        contentPanel.removeAll();

        List<String[]> feedbacks = TechnicianHandler.getFeedbacksByTechnician(currentUser.getTp());

        String[] cols = {"Customer TP", "Date", "Time", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (String[] fb : feedbacks) {
            model.addRow(new Object[]{ fb[0], fb[1], fb[2], fb[5] });
        }

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JLabel hint = new JLabel("These are feedbacks you have provided for completed appointments.");
        hint.setFont(new Font("Arial", Font.ITALIC, 12));

        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(hint, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}