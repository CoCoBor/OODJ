import java.io.*;
import java.util.*;

/**
 * TechnicianHandler
 * Handles all file I/O operations for the Technician role:
 *   - Reading their assigned appointments from appointments.txt
 *   - Updating appointment status
 *   - Saving / retrieving job feedbacks from feedbacks.txt
 *
 * appointments.txt format (9-10 fields):
 *   customer, customerTP, plate, service, technician, techTP, date, time, status[, customerComment]
 *
 * feedbacks.txt format (6 fields):
 *   customerTP, date, time, techTP, techName, feedbackText
 */
public class TechnicianHandler {

    private static final String APPT_FILE     = "text/appointments.txt";
    private static final String FEEDBACK_FILE = "text/feedbacks.txt";

    /** Return all appointments assigned to the given technician TP. */
    public static List<String[]> getAppointmentsForTechnician(String techTP) {
        List<String[]> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(APPT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                // Index 5 = techTP
                if (parts.length >= 8 && parts[5].trim().equals(techTP)) {
                    result.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Mark a specific appointment as "Completed".
     * Appointment is identified by customerTP + date + time (unique enough).
     */
    public static void updateAppointmentStatus(String customerTP, String date, String time, String newStatus) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(APPT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { lines.add(line); continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 11
                        && parts[1].trim().equals(customerTP)
                        && parts[6].trim().equals(date)
                        && parts[7].trim().equals(time)) {

                    // Rebuild with status field (index 8)
                    parts[8] = newStatus; // ensure to only update status
                    lines.add(String.join(",", parts)); //Nnow the role can be rebuild SAFELYY
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeAllAppointments(lines);
    }

    /**
     * Save technician feedback for a job they have done.
     * feedbacks.txt: customerTP,date,time,techTP,techName,feedbackText
     */
    public static void saveFeedback(String customerTP, String date, String time,
                                     String techTP, String techName, String feedbackText) {
        // Replace existing if any
        List<String> lines = new ArrayList<>();
        boolean found = false;

        File f = new File(FEEDBACK_FILE);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) { lines.add(line); continue; }
                    String[] parts = line.split(",", 6);
                    if (parts.length >= 3
                            && parts[0].trim().equals(customerTP)
                            && parts[1].trim().equals(date)
                            && parts[2].trim().equals(time)) {
                        lines.add(customerTP + "," + date + "," + time + ","
                                + techTP + "," + techName + "," + feedbackText);
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!found) {
            lines.add(customerTP + "," + date + "," + time + ","
                    + techTP + "," + techName + "," + feedbackText);
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FEEDBACK_FILE, false))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get technician's feedback for a specific appointment (for pre-populating the text area).
     * Returns empty string if none found.
     */
    public static String getFeedbackForAppointment(String customerTP, String date, String time) {
        File f = new File(FEEDBACK_FILE);
        if (!f.exists()) return "";

        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length >= 6
                        && parts[0].trim().equals(customerTP)
                        && parts[1].trim().equals(date)
                        && parts[2].trim().equals(time)) {
                    return parts[5].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** Get all feedbacks submitted by a given technician. */
    public static List<String[]> getFeedbacksByTechnician(String techTP) {
        List<String[]> result = new ArrayList<>();
        File f = new File(FEEDBACK_FILE);
        if (!f.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length >= 6 && parts[3].trim().equals(techTP)) {
                    result.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Get all feedbacks (for Manager view). */
    public static List<String[]> getAllFeedbacks() {
        List<String[]> result = new ArrayList<>();
        File f = new File(FEEDBACK_FILE);
        if (!f.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length == 6) result.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ----------------------------------------------------------------
    //  CUSTOMER COMMENTS  (stored in appointments.txt index 9)
    // ----------------------------------------------------------------

    /**
     * Save a customer comment into appointments.txt (index 9).
     * Called from CustomerPage.
     */
    public static void saveCustomerComment(String customerTP, String date, String time, String comment) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(APPT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { lines.add(line); continue; }
                String[] parts = line.split(",", -1);
                if (parts.length < 11) { 
                    parts = Arrays.copyOf(parts, 11); // ensure we have enough fields to avoid index issues
                }

                if (parts[1].trim().equals(customerTP)
                        && parts[6].trim().equals(date)
                        && parts[7].trim().equals(time)) {

                            comment = comment.replace(",", ";"); // sanitize commas in comment to avoid CSV issues
                            comment = comment.replace("\n", " ");
                            comment = comment.replace("\r", " "); // remove newlines
                            parts[9] = comment; // update comment field 

                            lines.add(String.join(",", parts)); // rebuild line with updated comment
                
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeAllAppointments(lines);
    }

    // ----------------------------------------------------------------
    //  HELPER
    // ----------------------------------------------------------------
    private static void writeAllAppointments(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPT_FILE, false))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
