import java.io.*;
import java.util.*;

/**
 * CustomerHandler
 * Handles all file I/O operations for the Customer role:
 *   - Reading the customer's own appointments from appointments.txt
 *   - Fetching service prices from price.txt
 */
public class CustomerHandler {

    private static final String APPT_FILE  = "text/appointments.txt";
    private static final String PRICE_FILE = "text/price.txt";

    /**
     * Return all appointments belonging to the given customer TP.
     * appointments.txt format (≥8 fields):
     *   customer(0), customerTP(1), plate(2), service(3), technician(4), techTP(5), date(6), time(7), [status(8)], [comment(9)]
     */
    public static List<String[]> getAppointmentsForCustomer(String customerTP) {
        List<String[]> result = new ArrayList<>();

        File f = new File(APPT_FILE);
        if (!f.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(APPT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                // Index 1 = customerTP
                if (parts.length >= 8 && parts[1].trim().equals(customerTP)) {
                    String service = parts[3].trim();
                    String price = UserFileHandler.getPrice(service);

                    String[] newRow = Arrays.copyOf(parts, parts.length + 1);
                    newRow[parts.length] = price;
                    result.add(newRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Look up the price for a service type from price.txt.
     * price.txt format: ServiceName|Price
     * The service stored in appointments is "Normal (1 Hour)" or "Major (3 Hours)".
     * We map those to "Normal" / "Major" to match price.txt keys.
     */
    public static String getPriceForService(String service) {
        String key;
        if (service.toLowerCase().contains("normal")) {
            key = "Normal (1 Hour)";
        } else if (service.toLowerCase().contains("major")) {
            key = "Major (3 Hours)";
        } else {
            key = service;
        }

        File f = new File(PRICE_FILE);
        if (!f.exists()) return "N/A";

        try (BufferedReader br = new BufferedReader(new FileReader(PRICE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(key)) {
                    return parts[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "N/A";
    }
}
