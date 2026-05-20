package backend;

import java.io.*;
import java.util.*;

public class UserFileHandler {

    private static final String filePath =
        "text/users_id.txt";

    public static List<User> getAllUsersObject() {
    List<User> users = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 5) {
                User user = new User(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4]
                );
                users.add(user);
            }
        }

    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    return users;
}
    public static List<User> getStaffOnly() {
        List<User> allUsers = getAllUsersObject();
        List<User> staff = new ArrayList<>();
        for (User u : allUsers) {
            if (!u.getRole().trim().equalsIgnoreCase("customer")) {
                staff.add(u);
            }
        }
        return staff;
    }

    public static List<User> getCustomersOnly() {
        List<User> allUsers = getAllUsersObject();
        List<User> customers = new ArrayList<>();
        for (User u : allUsers) {
            if (u.getRole().trim().equalsIgnoreCase("customer")) {
                customers.add(u);
            }
        }
        return customers;
    }

    public static void addUser(String name, String tp, String phone, String password, String role) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(name + "," + tp + "," + phone + "," + password + "," + role);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

   public static User findUser(String tp, String password, String role) {

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 5) {

                String fileName = parts[0];
                String fileTp = parts[1];
                String filePhone = parts[2];
                String filePassword = parts[3];
                String fileRole = parts[4];

                if (fileTp.equals(tp)
                        && filePassword.equals(password)
                        && fileRole.equals(role)) {

                    return new User(
                        fileName,
                        fileTp,
                        filePhone,
                        filePassword,
                        fileRole
                    );
                }
            }
        }

    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    return null; // not found
}

    public static void setPrice(String service, String price) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("text/price.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|", -1);

                if (parts.length == 2 && parts[0].equals(service)) {
                    lines.add(service + "|" + price);
                } else {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter("text/price.txt", false))) {
        for (String l : lines) {
            pw.println(l);
        }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    }

    private static void writeAll(List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("JavaAssignment/" + filePath, false))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPrice(String service) {
        try (BufferedReader br = new BufferedReader(new FileReader("text/price.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|", -1);

                if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(service.trim())) {
                    return parts[1];
                }
            }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return "";
    }

    public static Map<String, int[]> generateReport(){
        Map<String, int[]> report = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("text/appointments.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length < 11) continue;
                    String serviceType = parts[3].trim();
                    String status = parts[8].trim().toLowerCase();
                    String payment = parts[10].trim();

                    if (status.equalsIgnoreCase("Completed") && payment.equalsIgnoreCase("PAID")) {
                        int price = 0;

                        try {
                            price = Integer.parseInt(getPrice(serviceType));
                        } catch (NumberFormatException e) {
                            // If price is not a valid integer, default to 0
                        }
                    report.putIfAbsent(serviceType, new int[]{0, 0}); // {total, completed}

                    report.get(serviceType)[0]++; // total++
                    report.get(serviceType)[1] += price; // add price to completed total
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
    }
        return report;
    }

    public static void checkSameTp(String tp) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    String fileTp = parts[1].trim();

                    if (fileTp.equals(tp)) {
                        throw new Exception("TP number already exists!");
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkSamePhone(String phone) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    String filePhone = parts[2].trim();

                    if (filePhone.equals(phone)) {
                        throw new Exception("Phone number already exists!");
                    }
                }
            }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public static void checkPhoneNumber(String phone) throws Exception {
        if (!phone.matches("\\d{10}")) {
            throw new Exception("Phone number must be 10 digits!");
        }
    }

}