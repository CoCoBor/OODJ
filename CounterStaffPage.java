import java.awt.*;
import java.util.List;
import javax.swing.*;

public class CounterStaffPage extends JFrame {

    private User currentUser;
    private JTable table;
    private JPanel contentPanel;

    public CounterStaffPage(User user) {
        this.currentUser = user;

        // Frame
        setTitle("Counter Staff Dashboard");
        setSize(700, 500);
        setLayout(new BorderLayout());

        // ===== TOP TITLE =====
        JLabel title = new JLabel("Counter Staff Dashboard - Welcome, " + currentUser.getName());
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnProfile = new JButton("Edit Profile");
        JButton btnCustomers = new JButton("Manage Customers");
        JButton btnAppointments = new JButton("Appointments");
        JButton btnPayments = new JButton("Payments & Receipts");

        buttonPanel.add(btnProfile);
        buttonPanel.add(btnCustomers);
        buttonPanel.add(btnAppointments);
        buttonPanel.add(btnPayments);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // ===== CENTER AREA =====
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // ===== BOTTOM =====
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new MainFrame().initialize();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);

        // ===== ADD TO FRAME =====
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // =====================================================
        // EDIT PROFILE
        // =====================================================
        btnProfile.addActionListener(e -> {
            contentPanel.removeAll();

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField nameField = new JTextField(currentUser.getName());
            JTextField tpField = new JTextField(currentUser.getTp());
            tpField.setEditable(false);
            JTextField phoneField = new JTextField(currentUser.getPhone());
            JPasswordField passwordField = new JPasswordField(currentUser.getPassword());

            JButton btnUpdate = new JButton("Update");

            panel.add(new JLabel("Name:"));
            panel.add(nameField);

            panel.add(new JLabel("TP Number:"));
            panel.add(tpField);

            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);

            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            panel.add(new JLabel(""));
            panel.add(btnUpdate);

            btnUpdate.addActionListener(ev -> {
                String newName = nameField.getText();
                String newPhone = phoneField.getText();
                String newPassword = new String(passwordField.getPassword());

                try {
                    UserFileHandler.checkPhoneNumber(newPhone);
                    CounterHandler.updateUser(currentUser.getTp(), newName, newPhone, newPassword, currentUser.getRole());
                    currentUser.setName(newName);
                    currentUser.setPhone(newPhone);
                    currentUser.setPassword(newPassword);
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });


        btnCustomers.addActionListener(e -> {
            showCustomerPanel();
        });

       //Appointments (make appointement, view appointments)
        btnAppointments.addActionListener(e -> {

            contentPanel.removeAll();

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

            JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnCreate = new JButton("Create Appointment");
            JButton btnViewAll = new JButton("View All Appointments");
            JButton btnViewFeedback = new JButton("View Feedbacks");
            topBtnPanel.add(btnCreate);
            topBtnPanel.add(btnViewAll);
            topBtnPanel.add(btnViewFeedback);

            mainPanel.add(topBtnPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new BorderLayout());
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            contentPanel.add(mainPanel);
            contentPanel.revalidate();
            contentPanel.repaint();

            // ================= CREATE APPOINTMENT =================
            btnCreate.addActionListener(ev -> {

                centerPanel.removeAll();

                JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));

                JComboBox<String> customerBox =
                        new JComboBox<>(CounterHandler.getCustomersOnly());

                JComboBox<String> serviceBox =
                        new JComboBox<>(new String[]{
                                "Normal (1 Hour)",
                                "Major (3 Hours)"
                        });

                JComboBox<String> technicianBox =
                        new JComboBox<>(CounterHandler.getTechnicians());

                JComboBox<String> dayBox =
                        new JComboBox<>(generateRange(1, 31));

                JComboBox<String> monthBox =
                        new JComboBox<>(new String[]{
                                "01", "02", "03", "04", "05", "06",
                                "07", "08", "09", "10", "11", "12"
                        });

                JComboBox<String> yearBox =
                        new JComboBox<>(new String[]{
                                "2026", "2027", "2028", "2029", "2030"
                        });

                JComboBox<String> timeBox =
                        new JComboBox<>(new String[]{
                                "09:00", "10:00", "11:00",
                                "12:00", "14:00", "15:00", "16:00"
                        });

                formPanel.add(new JLabel("Customer:"));
                formPanel.add(customerBox);

                formPanel.add(new JLabel("Service:"));
                formPanel.add(serviceBox);

                formPanel.add(new JLabel("Date:"));

                JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                datePanel.add(dayBox);
                datePanel.add(monthBox);
                datePanel.add(yearBox);
                formPanel.add(datePanel);

                formPanel.add(new JLabel("Time:"));
                formPanel.add(timeBox);

                formPanel.add(new JLabel("Technician:"));
                formPanel.add(technicianBox);

                JButton createBtn = new JButton("Confirm Booking");
                formPanel.add(new JLabel(""));
                formPanel.add(createBtn);

                centerPanel.add(formPanel, BorderLayout.NORTH);
                centerPanel.revalidate();
                centerPanel.repaint();

                createBtn.addActionListener(x -> {

                    String customer = (String) customerBox.getSelectedItem();
                    String tp = customer.substring(
                            customer.indexOf("(") + 1,
                            customer.indexOf(")")
                    );

                    String plate = CounterHandler.getCarPlateByTP(tp);
                    String service = (String) serviceBox.getSelectedItem();
                    String technician = (String) technicianBox.getSelectedItem();
                    String techTP = technician.substring(
                            technician.indexOf("(") + 1,
                            technician.indexOf(")")
                    );

                    String time = (String) timeBox.getSelectedItem();
                    String date =
                            yearBox.getSelectedItem() + "-" +
                            monthBox.getSelectedItem() + "-" +
                            dayBox.getSelectedItem();

                    boolean available =
                            CounterHandler.technicianAvailability(techTP, date, time, service);

                    if (!available) {
                        JOptionPane.showMessageDialog(null, "Technician unavailable!");
                        return;
                    }

                    CounterHandler.addAppointment(
                            customer, tp, plate,
                            service,
                            technician, techTP,
                            date, time
                    );

                    JOptionPane.showMessageDialog(null, "Appointment Created!");
                });
            });

            // ================= VIEW ALL APPOINTMENTS =================
            btnViewAll.addActionListener(ev -> {
               centerPanel.removeAll();

                String[] columns = {
                        "Customer",
                        "Car Plate",
                        "Service",
                        "Technician",
                        "Date",
                        "Time"
                };

                List<Appointment> appointments = CounterHandler.getAllAppointmentsForTable();

                Object[][] data = new Object[appointments.size()][6];

                for (int i = 0; i < appointments.size(); i++) {
                    Appointment appt = appointments.get(i);
                    data[i][0] = appt.getCustomerName();
                    data[i][1] = appt.getPlate();
                    data[i][2] = appt.getService();
                    data[i][3] = appt.getTechnician();
                    data[i][4] = appt.getDate();
                    data[i][5] = appt.getTime();
                }

                JTable table = new JTable(data, columns);
                JScrollPane scroll = new JScrollPane(table);

                centerPanel.add(scroll, BorderLayout.CENTER);
                centerPanel.revalidate();
                centerPanel.repaint();
            });

            btnViewFeedback.addActionListener(ev -> {
                centerPanel.removeAll();
                String[] columns = {"Customer", "Service", "Technician", "Date", "Time", "Comment"};
                List<String[]> feedbacks = CounterHandler.getCustomerFeedback();

                Object[][] data = new Object[feedbacks.size()][6];

                for (int i = 0; i < feedbacks.size(); i++) {
                    String[] feedback = feedbacks.get(i);
                    data[i][0] = feedback[0];
                    data[i][1] = feedback[1];
                    data[i][2] = feedback[2];
                    data[i][3] = feedback[3];
                    data[i][4] = feedback[4];
                    data[i][5] = feedback[5];
                }

                JTable table = new JTable(data, columns);
                JScrollPane scroll = new JScrollPane(table);

                centerPanel.add(scroll, BorderLayout.CENTER);
                centerPanel.revalidate();
                centerPanel.repaint();
            });
        });

        // =====================================================
        // PAYMENTS & RECEIPTS
        // =====================================================
        btnPayments.addActionListener(e -> {
            contentPanel.removeAll();
            
            String[] columns = {
                "Customer","Car Plate","Service","Technician",
                "Date","Time","Price","Status"
            };
            
            List<Appointment> paidList = CounterHandler.getCompletedPaidAppointments();
            List<Appointment> unpaidList = CounterHandler.getCompletedUnpaidAppointments();
            
            JTable table = new JTable();
            
            loadPaymentTable(unpaidList, table, columns);
            
            JScrollPane scroll = new JScrollPane(table);
            JButton btnUnpaid = new JButton("Unpaid");
            JButton btnPaid = new JButton("Paid");
            
            JPanel buttonPay = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPay.add(btnUnpaid);
            buttonPay.add(btnPaid);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scroll, BorderLayout.CENTER);
            panel.add(buttonPay, BorderLayout.SOUTH);
            
            contentPanel.add(panel);
            contentPanel.revalidate();
            contentPanel.repaint();
            
            // Complete yet UNPAID 
            btnUnpaid.addActionListener(ev -> {
                loadPaymentTable(unpaidList, table, columns);
            });
            
            // Complete and PAID 
            btnPaid.addActionListener(ev -> {
                loadPaymentTable(paidList, table, columns);
            });
            
            // ================= ROW CLICK =================
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    
                    int row = table.getSelectedRow();
                    if (row == -1) return;
                    
                    String status = table.getValueAt(row, 7).toString();
                    
                    Appointment appt = (status.equals("PAID"))
                    ? paidList.get(row)
                    : unpaidList.get(row);
                    
                    if (status.equals("UNPAID")) {
                        
                        int option = JOptionPane.showOptionDialog(
                        null,
                        "Customer: " + appt.getCustomerName(),
                        "Unpaid Appointment",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Collect Payment"},
                        "Collect Payment"
                    );
                    
                    if (option == 0) {
                        CounterHandler.markPaymentAsPaid(
                            appt.getCustomerTP(),
                            appt.getDate(),
                            appt.getTime()
                        );
                        JOptionPane.showMessageDialog(null, "Payment Collected!");
                        btnPayments.doClick();
                    }
                }
                
                else if (status.equals("PAID")) {
                    int option = JOptionPane.showOptionDialog(
                        null,
                        "Customer: " + appt.getCustomerName(),
                        "Paid Appointment",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Generate Receipt"},
                        "Generate Receipt"
                    );
                    
                    if (option == 0) {
                        CounterHandler.generateReceipt(appt);
                        String receipt = CounterHandler.readReceipt(appt);
                        JTextArea textArea = new JTextArea(receipt);
                        textArea.setEditable(false);
                        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(400, 300));
                        JOptionPane.showMessageDialog(null, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    });
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
}

    // =====================================================
    // SHOW CUSTOMER PANEL
    // =====================================================
    private void showCustomerPanel() {

        contentPanel.removeAll();

        String[] columns = {"Name", "TP Number", "Phone"};
        List<User> users = UserFileHandler.getCustomersOnly();

        Object[][] data = new Object[users.size()][3];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = u.getName();
            data[i][1] = u.getTp();
            data[i][2] = u.getPhone();
        }

        table = new JTable(data, columns);
        JScrollPane scroll = new JScrollPane(table);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        btnPanel.add(btnAdd);

        btnAdd.addActionListener(a -> {
            CustomerAddUser addPage = new CustomerAddUser();
            addPage.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    showCustomerPanel();
                }
            });
            addPage.setVisible(true);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                int row = table.getSelectedRow();
                if (row == -1) return;

                User selectedUser = users.get(row);

                int option = JOptionPane.showOptionDialog(
                        null,
                        "Selected: " + selectedUser.getName(),
                        "Choose Action",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Update", "Delete"},
                        "Update"
                );

                if (option == 0) {
                    CustomerUpdateUser updatePage = new CustomerUpdateUser(
                            selectedUser.getName(),
                            selectedUser.getTp(),
                            selectedUser.getPhone()
                    );
                    updatePage.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            showCustomerPanel();
                        }
                    });
                    updatePage.setVisible(true);

                } else if (option == 1) {
                    DeleteUser delete = new DeleteUser();
                    try {
                        delete.deleteUserFromFile(selectedUser.getTp());
                        JOptionPane.showMessageDialog(null, "Customer deleted!");
                        showCustomerPanel();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // HELPER
    private String[] generateRange(int start, int end) {
        String[] arr = new String[end - start + 1];
        for (int i = start; i <= end; i++) {
            arr[i - start] = String.valueOf(i);
        }
        return arr;
    }

    private void loadPaymentTable(List<Appointment> list, JTable table, String[] columns) {
        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            Appointment appt = list.get(i);
            data[i][0] = appt.getCustomerName();
            data[i][1] = appt.getPlate();
            data[i][2] = appt.getService();
            data[i][3] = appt.getTechnician();
            data[i][4] = appt.getDate();
            data[i][5] = appt.getTime();
            data[i][6] = UserFileHandler.getPrice(appt.getService());
            data[i][7] = appt.getPaymentStatus();
        }
        table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
    }
}