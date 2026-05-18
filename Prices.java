import java.awt.*;
import javax.swing.*;

public class Prices extends JFrame {
     private JTextField price;
     private JComboBox<String> serviceBox;

     public Prices(){
        setTitle("Services");
        setSize(400, 400);
        setLayout(new GridLayout(10, 1));

        String[] services = {"Normal (1 Hour)", "Major (3 Hours)"};
        serviceBox = new JComboBox<>(services);
        serviceBox.addActionListener(e -> loadPrice());

        price = new JTextField();
        add(new JLabel("Service Type"));
        add(serviceBox);
        add(new JLabel("Price (RM): "));
        add(price);

        JButton btnSet = new JButton("Set Price");

        add(btnSet);
        loadPrice();

        btnSet.addActionListener(e -> {
            String serviceType = (String) serviceBox.getSelectedItem();
            String priceValue = price.getText();

            // Here you can add code to save the price to a file or database
            if (priceValue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a price!");
                 return;
                }
            UserFileHandler.setPrice(serviceType, priceValue);
            JOptionPane.showMessageDialog(this, "Price for " + serviceType + " set to: " + priceValue);
        });

     }
     private void loadPrice() {
        String selectedService = (String) serviceBox.getSelectedItem();
        String priceValue = UserFileHandler.getPrice(selectedService);
        price.setText(priceValue);
     }
        
}
