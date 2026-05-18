import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class ManagerFeedback extends JFrame {

    JTable table;

    public ManagerFeedback() {

        setTitle("View Feedback");
        setSize(750, 450);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Feedback Report & Technician Feedback", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {
                "Customer TP",
                "Date",
                "Time",
                "Technician",
                "Tech Feedback",
                "Customer Comment"
        };

        
        List<String[]> techFeedback = TechnicianHandler.getAllFeedbacks();

        List<String[]> appointments = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("text/appointments.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                appointments.add(parts);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Keep this since tech feedback and customer comments are stored separately, we need to match them based on TP
        Object[][] data = new Object[techFeedback.size()][6];

        for (int i = 0; i < techFeedback.size(); i++) {

            String[] fb = techFeedback.get(i);

            String customerTP = fb[0].replaceAll(".*\\((\\d+)\\)", "$1");
            String date = fb[1];
            String time = fb[2];

            String custComment = "";
            
            for (String[] appt : appointments) {
                if (appt.length > 9) {
                    String apptTP = appt[1].trim();
                    String apptDate = appt[6].trim();
                    String apptTime = appt[7].trim();

                    String fbTP = customerTP.trim();
                    String fbDate = date.trim();
                    String fbTime = time.trim();
                    
                    if (apptTP.equals(fbTP) && apptDate.equals(fbDate) && apptTime.equals(fbTime)) {
                        custComment = appt[9] != null ? appt[9].trim() : "";
                        break;
                    }
                }
            }

            data[i][0] = customerTP;
            data[i][1] = date;
            data[i][2] = time;
            data[i][3] = fb[4];   // technician name/id
            data[i][4] = fb[5];   // technician feedback
            data[i][5] = custComment;
        }

        table = new JTable(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make all cells non-editable
            }
        };

        table.getTableHeader().setReorderingAllowed(false); // prevent column reordering
        add(new JScrollPane(table), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}

