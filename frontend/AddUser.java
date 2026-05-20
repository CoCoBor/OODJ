package frontend;

import backend.*;
import java.awt.*;
import javax.swing.*;

public class AddUser extends JFrame {

    private JTextField tName, tTp, tPhone;
    private JPasswordField tPassword;
    private JComboBox<String> roleBox;

    public AddUser() {

        setTitle("Add User");
        setSize(400, 400);
        setLayout(new GridLayout(10, 1));

        // Fields
        tName = new JTextField();
        tTp = new JTextField();
        tPhone = new JTextField();
        tPassword = new JPasswordField();

        String[] roles = {"Manager", "Counter Staff", "Technician"};
        roleBox = new JComboBox<>(roles);

        // UI Labels + Inputs
        add(new JLabel("Name"));
        add(tName);

        add(new JLabel("TP Number"));
        add(tTp);

        add(new JLabel("Phone"));
        add(tPhone);

        add(new JLabel("Password"));
        add(tPassword);

        add(new JLabel("Role"));
        add(roleBox);

        // Buttons
        JButton btnAdd = new JButton("Add User");
        JButton btnBack = new JButton("Back");

        add(btnAdd);
        add(btnBack);

        btnAdd.addActionListener(e -> {

            String name = tName.getText();
            String tp = tTp.getText();
            String phone = tPhone.getText();
            String password = new String(tPassword.getPassword());
            String role = (String) roleBox.getSelectedItem();

            // MUST check if TP already exists before adding
            // MUST check if Phone already exists before adding & IT MUST be 10 digits long before adding
            try {
                UserFileHandler.checkSameTp(tp);
                UserFileHandler.checkSamePhone(phone);
                UserFileHandler.checkPhoneNumber(phone);
                UserFileHandler.addUser(name, tp, phone, password, role);
                JOptionPane.showMessageDialog(this, "User added successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //BACK BUTTON
        btnBack.addActionListener(e -> {
            dispose();
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
