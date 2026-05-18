import java.awt.*;
import java.io.*;
import javax.swing.*;

public class Feedback extends JFrame {

    JTable table;

    public Feedback() {

        setTitle("View Feedback");
        setSize(750, 450);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Customer Feedback & Technician Remarks", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        add(title, BorderLayout.NORTH);

        // Load feedbacks.txt
        String[] columnNames = {"Customer TP", "Date", "Time", "Technician", "Tech Feedback", "Customer Comment"};
        java.util.List<String[]> feedbacks = TechnicianHandler.getAllFeedbacks();

        // Load appointments for customer comments
        java.util.List<String[]> allAppts = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("text/appointments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) allAppts.add(parts);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (String[] fb : feedbacks) {
            String custComment = "";
            for (String[] appt : allAppts) {
                if (appt[1].trim().equals(fb[0].trim())
                        && appt[6].trim().equals(fb[1].trim())
                        && appt[7].trim().equals(fb[2].trim())
                        && appt.length > 9) {
                    custComment = appt[9].trim();
                    break;
                }
            }
            rows.add(new Object[]{ fb[0], fb[1], fb[2], fb[4], fb[5], custComment });
        }

        Object[][] data = rows.toArray(new Object[0][]);
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            dispose();
            new ManagerPage().setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
