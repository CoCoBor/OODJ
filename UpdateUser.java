import java.awt.*;
import java.io.*;
import javax.swing.*;

public class UpdateUser extends JFrame {

    JTextField tName, tTpNum, tPhone;
    JPasswordField tPassword;
    JComboBox<String> dropRole;

    public UpdateUser(String name, String tp, String phone, String password, String role) {

        tName = new JTextField(name);
        tTpNum = new JTextField(tp);
        tTpNum.setEditable(false);
        tPhone = new JTextField(phone);
        tPassword = new JPasswordField(password);

        String[] roles = {"Manager", "Counter Staff", "Technician"};
        dropRole = new JComboBox<>(roles);
        dropRole.setSelectedItem(role);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> {

            String newName = tName.getText();
            String newPhone = tPhone.getText();
            String newPassword = new String(tPassword.getPassword());
            String newRole = (String) dropRole.getSelectedItem();

            try {
                UserFileHandler.checkPhoneNumber(newPhone);
                updateUserInFile(tp, newName, newPhone, newPassword, newRole);
                JOptionPane.showMessageDialog(this, "Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            new ManagerPage().setVisible(true);
        });

        JPanel panel = new JPanel(new GridLayout(10,1));
        panel.add(new JLabel("Name")); panel.add(tName);
        panel.add(new JLabel("TP")); panel.add(tTpNum);
        panel.add(new JLabel("Phone")); panel.add(tPhone);
        panel.add(new JLabel("Password")); panel.add(tPassword);
        panel.add(new JLabel("Role")); panel.add(dropRole);
        panel.add(btnUpdate);

        add(panel);

        setTitle("Update User");
        setSize(400, 400);
        setVisible(true);
    }

    private void updateUserInFile(String tp, String name, String phone, String password, String role) {

    java.util.List<String> lines = new java.util.ArrayList<>();
    String filePath = "text/users_id.txt";

    boolean updated = false; 

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 5) {
                String fileTp = parts[1].trim();

                if (!updated && fileTp.equals(tp)) {
                    lines.add(name + "," + tp + "," + phone + "," + password + "," + role);
                    updated = true;
                } else {
                    lines.add(line);
                }
            } else {
                lines.add(line); // safety
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    // WRITE BACK
    try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
        for (String l : lines) {
            pw.println(l);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}