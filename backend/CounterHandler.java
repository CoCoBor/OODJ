package backend;

import java.io.*;
import java.util.*;

public class CounterHandler {

    private static final String filePath =
        "text/users_id.txt";

    // Update personal info
    public static void updateUser(String tp, String name, String phone, String password, String role) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 5 && parts[1].trim().equals(tp)) {
                    lines.add(name + "," + tp + "," + phone + "," + password + "," + role);
                } else {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeAll(lines);
    }

    // UPDATE CUSTOMER ONLY 
    public static void updateCustomer(String tp, String name, String phone) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 5 && parts[1].trim().equals(tp)) {

                    String password = parts[3];
                    String role = parts[4];

                    lines.add(name + "," + tp + "," + phone + "," + password + "," + role);
                } else {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeAll(lines);
    }

    private static void writeAll(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addVehicle(String tp, String plate, String type) {
    String localFilePath =
        "text/customer_vehicle.txt";

    try (PrintWriter pw = new PrintWriter(new FileWriter(localFilePath, true))) {

        pw.println(tp + "," + plate + "," + type);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    }

    public static String[] getCustomersOnly() {
        Set<String> customers = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5 && parts[4].trim().equalsIgnoreCase("customer")) {
                    customers.add(parts[0].trim() + " (" + parts[1].trim() + ")"); // Name (TP)
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return customers.toArray(new String[0]);
    }

    public static String[] getTechnicians() {
        Set<String> technicians = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5 && parts[4].trim().equalsIgnoreCase("technician")) {
                    technicians.add(parts[0].trim() + " (" + parts[1].trim() + ")"); // Name (TP)
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return technicians.toArray(new String[0]);
    }

    public static void addAppointment(String customer, String tp, String plate, String service, String technician, String techTP, String date, String time) {
        String localFilePath =
            "text/appointments.txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(localFilePath, true))) {
            pw.println(customer + "," + tp + "," + plate + "," + service + "," + technician + "," + techTP + "," + date + "," + time + ",Pending," + "" + "," + "UNPAID"); 
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCarPlateByTP(String tp) {
        String localFilePath =
            "text/customer_vehicle.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 3 && parts[0].trim().equals(tp)) {
                    return parts[1].trim(); // Return plate
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "No Car Plate Found"; // Not found
    }

    public static int getDuration(String service) {
        switch (service) {
            case "Normal (1 Hour)":
                return 1;
            case "Major (3 Hours)":
                return 3;
            default:
                return 0; // Unknown service
        }
    }

    public static int getHour(String time){
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    // Check technician availability for a given date and time slot
    //SO technician won't be overbooked for multiple appointments at the same time
    public static boolean technicianAvailability(String techTp, String date, String time, String duration) {
        String localFilePath =
            "text/appointments.txt";
        int newStart = getHour(time);
        int newEnd = newStart + getDuration(duration);
        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 8) continue; // SKIP broken lines
                String bookedTechTp = parts[5].trim();
                String bookedDate = parts[6].trim();
                String bookedTime = parts[7].trim();
                String bookedService = parts[3].trim();

                if (bookedTechTp.equals(techTp) && bookedDate.equals(date)) {
                    int bookedStart = getHour(bookedTime);
                    int bookedEnd = bookedStart + getDuration(bookedService);

                    // Check for time overlap
                    if (newStart < bookedEnd && newEnd > bookedStart) {
                        return false; // Not available
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
    }
    return true; // Available
   }

   // For counter staff to view all appointments in a table
   public static List<Appointment> getAllAppointmentsForTable() {

    String localFilePath =
        "text/appointments.txt";

    java.util.List<Appointment> appointments =
            new java.util.ArrayList<>();

        try (BufferedReader br =
            new BufferedReader(new FileReader(localFilePath))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",");

            if (parts.length >= 11) {

                Appointment appt = new Appointment(
                        parts[0].trim(), // customer
                        parts[1].trim(), // TP number
                        parts[2].trim(), // plate
                        parts[3].trim(), // service
                        parts[4].trim(), // technician
                        parts[5].trim(), // tech TP
                        parts[6].trim(), // date
                        parts[7].trim(),  // time
                        parts[8].trim(),   // status
                        parts[10].trim() // payment status
                );
                appointments.add(appt);
            }
        }

    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    return appointments;
}
    // For counter staff to update payment status to UNPAID when appointment is marked as completed by technician
    public static void updatePaymentStatus(String customerTP, String date, String time, String newStatus) {
        String localFilePath =
            "text/appointments.txt";

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 11) {
                    parts = Arrays.copyOf(parts, 11); // ensure we have enough fields to avoid index issues 
                }
                    if (parts[1].trim().equals(customerTP)
                            && parts[6].trim().equals(date)
                            && parts[7].trim().equals(time)) {
                        parts[10] = newStatus; // Update payment status (index 10)
                         lines.add(String.join(",", parts));
                    } else {
                        lines.add(line);
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Write the updated lines back to the file
        try (PrintWriter pw = new PrintWriter(new FileWriter(localFilePath, false))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // For counter staff to view pending payments (completed but UNPAID)
    public static List<String[]> getPendingPayments() {
        List<String[]> pending = new ArrayList<>();

        String localFilePath =
            "text/appointments.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 11) continue; // SKIP broken lines
                String status = parts[8].trim();
                String paymentStatus = parts[10].trim();

                if (status.equalsIgnoreCase("Completed") && paymentStatus.equalsIgnoreCase("UNPAID")) {
                    String price = UserFileHandler.getPrice(parts[3].trim());
                    pending.add(new String[]{
                            parts[0].trim(), // customer
                            parts[2].trim(), // plate
                            parts[3].trim(), // service
                            parts[1].trim(), // TP number
                            parts[6].trim(), // date
                            parts[7].trim(),  // time
                            price,
                            paymentStatus
                    });
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pending;
    }

    // Mark payment as paid in appointments.txt when counter staff confirms payment
    public static void markPaymentAsPaid(String customerTP, String date, String time) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("text/appointments.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 11) {
                    lines.add(line); // Keep broken lines as-is
                    continue;
                }

                    String tp = parts[1] == null ? "" : parts[1].trim();
                    String apptDate  = parts[6] == null ? "" : parts[6].trim();
                    String apptTime  = parts[7] == null ? "" : parts[7].trim();
                if (tp.equals(customerTP) && apptDate.equals(date) && apptTime.equals(time)) {
                    parts[10] = "PAID"; // Update payment status to PAID
                    lines.add(String.join(",", parts));
                } else {
                    lines.add(line);
                    
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Write the updated lines back to the file
        try (PrintWriter pw = new PrintWriter(new FileWriter("text/appointments.txt", false))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
 }
    // for those who are completed but HAVE NOT PAID YET
    public static List<Appointment>getCompletedUnpaidAppointments() {
        String localFilePath =
            "text/appointments.txt";
        List<Appointment> completeUnpaid = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 11) continue; // SKIP broken lines
                String status = parts[8].trim();
                String paymentStatus = parts[10].trim();

                if (status.equalsIgnoreCase("Completed") && paymentStatus.equalsIgnoreCase("UNPAID")) {
                    Appointment appt = new Appointment(
                            parts[0].trim(), // customer
                            parts[1].trim(), // TP number
                            parts[2].trim(), // plate
                            parts[3].trim(), // service
                            parts[4].trim(), // technician
                            parts[5].trim(), // technician TP
                            parts[6].trim(), // date
                            parts[7].trim(),  // time
                            parts[8].trim(),   // status
                            parts[10].trim() // payment status
                    );
                    completeUnpaid.add(appt);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return completeUnpaid;
    }

    // For generating receipt for ONLY THOSE WHO ARE COMPLETED AND PAID
    public static List<Appointment> getCompletedPaidAppointments(){
        String localFilePath =
                "text/appointments.txt";
            List<Appointment> completePaid = new ArrayList<>();
    
            try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
                String line;
    
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 11) continue; // SKIP broken lines
                    String status = parts[8].trim();
                    String paymentStatus = parts[10].trim();
    
                    if (status.equalsIgnoreCase("Completed") && paymentStatus.equalsIgnoreCase("PAID")) {
                        Appointment appt = new Appointment(
                                parts[0].trim(), // customer
                                parts[1].trim(), // TP number
                                parts[2].trim(), // plate
                                parts[3].trim(), // service
                                parts[4].trim(), // technician
                                parts[5].trim(), // technician TP
                                parts[6].trim(), // date
                                parts[7].trim(),  // time
                                parts[8].trim(),   // status
                                parts[10].trim() // payment status
                        );
                        completePaid.add(appt);
                    }
                }
            } catch (IOException e) {
                    throw new RuntimeException(e);
                }
          return completePaid;
    }

    public static void generateReceipt(Appointment appt) {
         String receiptFolder = "receipts";
         
         // Create receipts folder if it doesn't exist
         //Receipts should be stored in a folder for better organization
         File folder = new File(receiptFolder);
         
         if (!folder.exists()) {
            folder.mkdir();
        }
        String receiptFile = receiptFolder + "/receipt_" + appt.getCustomerTP() + "_" + appt.getDate() + "_" + appt.getTime().replace(":", "-") + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(receiptFile))) {
            pw.println("----- APU Automotive Center Receipt -----");
            pw.println("Customer Name: " + appt.getCustomerName());
            pw.println("TP Number: " + appt.getCustomerTP());
            pw.println("Car Plate: " + appt.getPlate());
            pw.println("Service: " + appt.getService());
            pw.println("Technician: " + appt.getTechnician());
            pw.println("Date: " + appt.getDate());
            pw.println("Time: " + appt.getTime());
            pw.println("Status: " + appt.getStatus());
            pw.println("Payment Status: " + appt.getPaymentStatus());
            String price = UserFileHandler.getPrice(appt.getService());
            pw.println("Price: RM " + price);
            pw.println("-----------------THANK YOU---------------------");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    //To show receipt content after generating for confirmation
    public static String readReceipt(Appointment appt) {
        String receiptFolder = "receipts";
        String receiptFile = receiptFolder + "/receipt_" + appt.getCustomerTP() + "_" + appt.getDate() + "_" + appt.getTime().replace(":", "-") + ".txt";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(receiptFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static List<String[]> getCustomerFeedback() {
        String localFilePath =
            "text/appointments.txt";
        List<String[]> feedbackList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(localFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 10) {
                    String feedback = parts[9].trim();

                    //Only show appointment with feedback 
                    if(!feedback.isEmpty()) {
                        String[] feedbackEntry = {
                                parts[1].trim(), // TP number
                                parts[3].trim(), // service
                                parts[4].trim(), // technician
                                parts[6].trim(), // date
                                parts[7].trim(),  // time
                                feedback // customer feedback (index 9)
                        };
                        feedbackList.add(feedbackEntry);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return feedbackList;
    }
    
}