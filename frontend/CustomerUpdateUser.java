package frontend;

import backend.*;
import java.awt.*;
import javax.swing.*;

//This is to update customer details 
public class CustomerUpdateUser extends JFrame {

    JTextField tName, tTpNum, tPhone;

    public CustomerUpdateUser(String name, String tp, String phone) {

        tName = new JTextField(name);
        tTpNum = new JTextField(tp);
        tTpNum.setEditable(false);
        tPhone = new JTextField(phone);

        JButton btnUpdate = new JButton("Update");

        btnUpdate.addActionListener(e -> {

            String newName = tName.getText();
            String newPhone = tPhone.getText();

            try {
                UserFileHandler.checkPhoneNumber(newPhone);
                CounterHandler.updateCustomer(tp, newName, newPhone);
                JOptionPane.showMessageDialog(this, "Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
        });

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name"));
        panel.add(tName);
        panel.add(new JLabel("TP"));
        panel.add(tTpNum);
        panel.add(new JLabel("Phone"));
        panel.add(tPhone);
        panel.add(btnUpdate);

        add(panel);

        setTitle("Update Customer");
        setSize(400, 300);
        setVisible(true);
    }
    
}
