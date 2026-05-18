import java.awt.*;
import java.util.Map;
import javax.swing.*;

//This to view how many completed appointments and revenue made
public class ViewReport extends JFrame {

    JTable table;

    public ViewReport() {

        setTitle("View Reports");
        setSize(700, 400);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Service Reports", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        
        String[] columnNames = {
                "Service Type",
                "Total Customers",
                "Total Revenue"
        };

        
        Map<String, int[]> report = UserFileHandler.generateReport();

        Object[][] data = new Object[report.size()][3];

        int i = 0;
        for (String service : report.keySet()) {

            int[] values = report.get(service);

            data[i][0] = service;
            data[i][1] = values[0];              // total customers
            data[i][2] = "RM " + values[1];     // total revenue

            i++;
        }

        //Report Table
        table = new JTable(data, columnNames);
        table.setEnabled(false); // prevent editing

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}