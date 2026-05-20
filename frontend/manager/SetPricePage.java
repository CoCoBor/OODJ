package frontend.manager;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.ServicePrice;
import models.enums.ServiceType;
import service.ServiceException;
import service.ServicePriceService;

public class SetPricePage extends JPanel {

    private final ServicePriceService priceService;
    private JComboBox<ServiceType> serviceTypeCombo;
    private JTextField priceField;
    private JButton updateBtn;
    private JTable priceTable;
    private DefaultTableModel tableModel;

    public SetPricePage(ServicePriceService priceService) {
        this.priceService = priceService;
        setLayout(new BorderLayout(8,8));
        initControls();
        initTable();
        refreshTable();
    }

    private void initControls() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        serviceTypeCombo = new JComboBox<>(ServiceType.values());
        priceField = new JTextField(8);
        updateBtn = new JButton("Update Price");
        top.add(new JLabel("Service Type:"));
        top.add(serviceTypeCombo);
        top.add(new JLabel("New Price:"));
        top.add(priceField);
        top.add(updateBtn);
        add(top, BorderLayout.NORTH);

        updateBtn.addActionListener(e -> onUpdate());
    }

    private void initTable() {
        String[] cols = {"Price ID", "Service Type", "Price", "Last Updated By", "Last Updated"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        priceTable = new JTable(tableModel);
        add(new JScrollPane(priceTable), BorderLayout.CENTER);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<ServicePrice> prices = priceService.getAllPrices();
            for (ServicePrice p : prices) {
                tableModel.addRow(new Object[]{p.getPriceId(), p.getServiceType(), p.getPrice(), p.getLastUpdatedBy(), p.getLastUpdatedDateTime()});
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        ServiceType type = (ServiceType) serviceTypeCombo.getSelectedItem();
        String priceText = priceField.getText();
        try {
            int newPrice = Integer.parseInt(priceText.trim());
            priceService.updatePrice(type, newPrice);
            JOptionPane.showMessageDialog(this, "Price updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            priceField.setText("");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
