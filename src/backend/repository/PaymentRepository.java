package src.backend.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import src.backend.models.Payment;
import src.backend.models.enums.PaymentMethod;

public class PaymentRepository implements InterfaceRepo<Payment> {

    private static final String FILE_PATH = "src/data/payments.txt";

    @Override
    public void save(Payment payment) {
        try {
            List<String> lines = readAllLines();
            String paymentLine = entityToString(payment);
            lines.add(paymentLine);
            writeAllLines(lines);
        } catch (IOException e) {
            System.err.println("Error saving payment: " + e.getMessage());
        }
    }

    @Override
    public Optional<Payment> findById(String id) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if(!line.trim().isEmpty()){
                    Payment payment = stringToEntity(line);
                    if (payment != null && payment.getPaymentId().equals(id)) {
                        return Optional.of(payment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding payment by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Payment payment = stringToEntity(line);
                    if (payment != null) {
                        payments.add(payment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding all payments: " + e.getMessage());
        }
        return payments;
    }

    @Override
    public void update(Payment payment) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Payment existingPayment = stringToEntity(line);
                    if (existingPayment != null && existingPayment.getPaymentId().equals(payment.getPaymentId())) {
                        updatedLines.add(entityToString(payment));
                    } else {
                        updatedLines.add(line);
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (Exception e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }

    }

    @Override
    public void delete(String id) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Payment payment = stringToEntity(line);
                    if (payment != null && !payment.getPaymentId().equals(id)) {
                        updatedLines.add(line);
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
        }
    }

    private List<String> readAllLines() throws IOException {
        Path path = Paths.get(FILE_PATH);

        return Files.readAllLines(path);
    }

    private void writeAllLines(List<String> lines) throws IOException {
        Path path = Paths.get(FILE_PATH);
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String entityToString(Payment payment) {
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append(payment.getPaymentId()).append("|")
                .append(payment.getAppointmentId()).append("|")
                .append(payment.getAmount()).append("|")
                .append(payment.getPaymentMethod()).append("|")
                .append(formatDateTime(payment.getPaidDateTime())).append("|")
                .append(payment.getReceiptNumber());

        return baseInfo.toString();
    }

    private Payment stringToEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] p = line.split("\\|");

        if (p.length != 6) {
            System.err.println("Invalid payment data format: " + line);
            return null;
        }

        try {
            String paymentId = p[0];
            String appointmentId = p[1];
            int amount = Integer.parseInt(p[2]);
            PaymentMethod paymentMethod = PaymentMethod.valueOf(p[3]);
            LocalDateTime paidDateTime = parseDateTime(p[4]);
            String receiptNumber = p[5];

            return new Payment(paymentId, appointmentId, amount, paymentMethod, paidDateTime, receiptNumber);
        } catch (Exception e) {
            System.err.println("Error parsing payment data: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, dateFormat);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime == null ? "" : dateTime.format(dateFormat);
    }
}

