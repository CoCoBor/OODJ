import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class ManagerPage extends JFrame {

    JTable table;
    private Object[][] loadUserData() {
    java.util.List<Object[]> userData = new java.util.ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader("text/users_id.txt"))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 5) {
                String name = parts[0].trim();
                String tp = parts[1].trim();
                String phone = parts[2].trim();
                String password = parts[3].trim();
                String role = parts[4].trim().toLowerCase();
                
                if (!role.equals("customer")) {
                    userData.add(new Object[]{name, tp, phone, password, role});
                }
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    // Convert List → Array
    Object[][] dataArray = new Object[userData.size()][5];
    for (int i = 0; i < userData.size(); i++) {
        dataArray[i] = userData.get(i);
    }

    return dataArray;
}
    
    public ManagerPage() {

        // Title
        JLabel welcomeLabel = new JLabel("Manager Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new MainFrame().initialize();
        });

        // Buttons
        JButton btnSetPrice = new JButton("Set Service Prices");
        btnSetPrice.addActionListener(e -> {
            Prices pricePage = new Prices();
            pricePage.setVisible(true);
        });
        JButton btnViewReport = new JButton("View Reports");
        btnViewReport.addActionListener(e -> {
            ViewReport reportPage = new ViewReport();
            reportPage.setVisible(true);
        });
        JButton btnFeedback = new JButton("View Feedback");
         btnFeedback.addActionListener(e -> {
             ManagerFeedback feedbackPage = new ManagerFeedback();
            feedbackPage.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSetPrice);
        buttonPanel.add(btnViewReport);
        buttonPanel.add(btnFeedback);

        JButton btnAddUser = new JButton("Add User");
        btnAddUser.addActionListener(e -> {
            AddUser addUserPage = new AddUser();
            addUserPage.setVisible(true);
        });
        


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(welcomeLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(btnAddUser, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);

        //Show technician, counter staff and other manager details in a table (except customers) FOR MANAGER's VIEW
        String[] columnNames = {"Name", "TP Number", "Phone", "Password", "Role"};
        List<User> users = UserFileHandler.getStaffOnly();


        Object[][] data = new Object[users.size()][5];
        
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = u.getName();
            data[i][1] = u.getTp();
            data[i][2] = u.getPhone();
            data[i][3] = u.getPassword();
            data[i][4] = u.getRole();
        }

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent evt){
                int row = table.getSelectedRow();

                String name = table.getValueAt(row, 0).toString();
                String tp = table.getValueAt(row, 1).toString();
                String phone = table.getValueAt(row, 2).toString();
                String password = table.getValueAt(row, 3).toString();
                String role = table.getValueAt(row, 4).toString();

                //show options
                int option = JOptionPane.showOptionDialog(
                    null,
                    "Selected: " + name,
                    "Choose Action",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Update", "Delete"},
                    "Update"
                );
                
                if (option == 0){
                    UpdateUser updatePage = new UpdateUser(name, tp, phone, password, role);
                    updatePage.setVisible(true);
                    dispose();

                } else if (option == 1){
                    DeleteUser delete = new DeleteUser();
                    try {
                        delete.deleteUserFromFile(tp);
                        JOptionPane.showMessageDialog(null, "User deleted!");
                        setVisible(true);
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            }
        });

        // MAIN layout
        setLayout(new BorderLayout());

        add(topPanel, BorderLayout.NORTH);     
        add(scrollPane, BorderLayout.CENTER);  

        setTitle("Manager Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    private void refreshTable() {
        String[] columnNames = {"Name", "TP Number", "Phone", "Password", "Role"};
        table.setModel(new javax.swing.table.DefaultTableModel(loadUserData(), columnNames));
    }

    public void showPage(){
        setVisible(true);
    }
}