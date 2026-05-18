import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class MainFrame extends JFrame {
    /*Glboal var*? */
    final private Font mainFont = new Font("Arial", Font.PLAIN, 18);
    JTextField Welcome, tFirstName, tPassword, tTpNum;
    JLabel lbWelcome;
    JComboBox<String> dropRole;

    private boolean validateLogin(String firstName, String tpNum, String password, String role) {
        try (BufferedReader br = new BufferedReader(new FileReader("text/users_id.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String fileFirstName = parts[0].trim();
                    String fileTpNum = parts[1].trim();
                    String filePassword = parts[3].trim();
                    String fileRole = parts[4].trim();

                    if (fileFirstName.equals(firstName) && fileTpNum.equals(tpNum) &&
                        filePassword.equals(password) && fileRole.equals(role)) {
                        return true; // Valid login
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Invalid login
    }

    public void initialize(){
        /****** Form Panel */
        JLabel welcome = new JLabel("APU Automotive Service Centre");
        welcome.setFont(mainFont);

        JLabel lbFirstName = new JLabel("First Name: ");
        lbFirstName.setFont(mainFont);

        tFirstName = new JTextField();
        tFirstName.setFont(mainFont);

        /*tFirstName = new JTextField();
        tFirstName.setFont(mainFont);*/
        JLabel lbTpNum = new JLabel("TP Number: ");
        lbTpNum.setFont(mainFont);

        tTpNum = new JTextField();
        tTpNum.setFont(mainFont);

        JLabel lbPassword = new JLabel("Password: ");
        lbPassword.setFont(mainFont);

        tPassword = new JTextField();
        tPassword.setFont(mainFont);

        /* Welcome Label  */
        lbWelcome = new JLabel("");
        lbWelcome.setFont(mainFont);

        JLabel lbRole = new JLabel("Role: ");
        lbRole.setFont(mainFont);

        String[] roles = {"Customer", "Manager", "Counter Staff", "Technician"};
        dropRole = new JComboBox<>(roles);
        dropRole.setFont(mainFont);

        /*BTN */
        JButton btnSubmit = new JButton("Login");
        btnSubmit.setFont(mainFont);
        btnSubmit.addActionListener(e -> {
            String firstName = tFirstName.getText();
            String tpNum = tTpNum.getText();
            String password = tPassword.getText();
            String role = (String) dropRole.getSelectedItem();

            if (validateLogin(firstName, tpNum, password, role)) {
                lbWelcome.setText("Welcome, " + firstName + "!");
                dispose();
                User loggedInUser = UserFileHandler.findUser(tpNum, password, role);

                if (role.equals("Manager")) {
                    ManagerPage managerPage = new ManagerPage();
                    managerPage.showPage();
                } else if (role.equals("Counter Staff")) {
                    CounterStaffPage counterStaffPage = new CounterStaffPage(loggedInUser);
                    counterStaffPage.setVisible(true);
                } else if (role.equals("Technician")) {
                    TechnicianPage techPage = new TechnicianPage(loggedInUser);
                    techPage.setVisible(true);
                } else if (role.equals("Customer")) {
                    CustomerPage customerPage = new CustomerPage(loggedInUser);
                    customerPage.setVisible(true);
                }
            } else {
                lbWelcome.setText("Invalid credentials. Please try again.");
            }
        });

        /* BTNClear  */
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(mainFont);
        btnClear.addActionListener(e -> {
            tFirstName.setText("");
            tPassword.setText("");
            lbWelcome.setText("");
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1,2,5,5));
        btnPanel.add(btnSubmit);
        btnPanel.add(btnClear);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(9,1,5,5));
        formPanel.add(welcome);
        formPanel.add(lbFirstName);
        formPanel.add(tFirstName);
        formPanel.add(lbTpNum);
        formPanel.add(tTpNum);
        formPanel.add(lbPassword);
        formPanel.add(tPassword);
        formPanel.add(lbRole);
        formPanel.add(dropRole);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(lbWelcome, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("Welcome");
        setSize(450, 490);
        setMinimumSize(new Dimension(400, 300));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    public static void main(String[] args) {
        MainFrame myFrame = new MainFrame();
        myFrame.initialize();
    }
}

