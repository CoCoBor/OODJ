import java.awt.*;
import javax.swing.*;

public class CustomerAddUser extends JFrame {

    private JTextField tName, tTp, tPhone, tcarPlate, tcarType, trole;
    private JPasswordField tPassword;

    public CustomerAddUser() {

        setTitle("Add User");
        setSize(400, 400);
        setLayout(new GridLayout(10, 1));

        // Fields
        tName = new JTextField();
        tTp = new JTextField();
        tPhone = new JTextField();
        tcarPlate = new JTextField();
        tcarType = new JTextField();
        tPassword = new JPasswordField();
        trole = new JTextField("Customer");
        trole.setEditable(false);

        // UI Labels + Inputs
        add(new JLabel("Name"));
        add(tName);

        add(new JLabel("TP Number"));
        add(tTp);

        add(new JLabel("Car Plate"));
        add(tcarPlate);

        add(new JLabel("Car Type"));
        add(tcarType);

        add(new JLabel("Phone"));
        add(tPhone);

        add(new JLabel("Password"));
        add(tPassword);

        add(new JLabel("Role"));
        add(trole);



        // Buttons
        JButton btnAdd = new JButton("Add User");
        JButton btnBack = new JButton("Back");

        add(btnAdd);
        add(btnBack);

        btnAdd.addActionListener(e -> {

            String name = tName.getText();
            String tp = tTp.getText();
            String phone = tPhone.getText();
            String carPlate = tcarPlate.getText();
            String carType = tcarType.getText();
            String password = new String(tPassword.getPassword());
            String role = trole.getText();

            // MUST check if TP already exists before adding
            // MUST check if Phone already exists before adding & IT MUST be 10 digits long before adding
            try {
                UserFileHandler.checkSameTp(tp);
                UserFileHandler.checkSamePhone(phone);
                UserFileHandler.checkPhoneNumber(phone);
                UserFileHandler.addUser(name, tp, phone, password, role);
                CounterHandler.addVehicle(tp, carPlate, carType);
                JOptionPane.showMessageDialog(this, "User added successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // BACK BUTTON
        btnBack.addActionListener(e -> {
            dispose();
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}

