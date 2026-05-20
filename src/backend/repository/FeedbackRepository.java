package backend.repository;


import backend.models.CustomerFeedback;
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

public class FeedbackRepository implements InterfaceRepo<CustomerFeedback> {
    private static final String FILE_PATH = "src/text/feedback.txt";

    @Override
    public void save(CustomerFeedback entity) {
        try {
            List<String> lines = readAllLines();
            String feedbackLine = entityToString(entity);
            lines.add(feedbackLine);
            writeAllLines(lines);
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
        }
    }

    @Override
    public Optional<CustomerFeedback> findById(String id) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    CustomerFeedback feedback = stringToEntity(line);
                    if (feedback != null && feedback.getFeedbackId().equals(id)) {
                        return Optional.of(feedback);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding feedback with id " + id + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<CustomerFeedback> findAll() {
        List<CustomerFeedback> feedbackList = new ArrayList<>();
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    CustomerFeedback feedback = stringToEntity(line);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
            }
            return feedbackList;
        } catch (IOException e) {
            System.err.println("Error finding all feedback: " + e.getMessage());
        }
        return feedbackList;
    }

    @Override
    public void update(CustomerFeedback entity) {
        try {
            List<String> lines = readAllLines();
            String updatedFeedbackLine = entityToString(entity);
            boolean found = false;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.trim().isEmpty()) {
                    CustomerFeedback feedback = stringToEntity(line);
                    if (feedback != null && feedback.getFeedbackId().equals(entity.getFeedbackId())) {
                        lines.set(i, updatedFeedbackLine);
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                writeAllLines(lines);
            } else {
                System.err.println("Feedback with id " + entity.getFeedbackId() + " not found for update.");
            }
        } catch (IOException e) {
            System.err.println("Error updating feedback: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    CustomerFeedback feedback = stringToEntity(line);
                    if (feedback != null && !feedback.getFeedbackId().equals(id)) {
                        updatedLines.add(line);
                    }
                }
            }

            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error deleting feedback with id " + id + ": " + e.getMessage());
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

    private String entityToString(CustomerFeedback feedback) {
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append(feedback.getFeedbackId()).append("|")
                .append(feedback.getAppointmentId()).append("|")
                .append(feedback.getCustomerId()).append("|")
                .append(feedback.getTechnicianId()).append("|")
                .append(feedback.getCounterStaffId()).append("|")
                .append(feedback.getRating()).append("|")
                .append(feedback.getComment()).append("|")
                .append(formatDateTime(feedback.getcommentDateTime()));
        return baseInfo.toString();
    }

    private CustomerFeedback stringToEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] p = line.split("\\|");

        if (p.length != 8) {
            System.err.println("Invalid feedback data format: " + line);
            return null;
        }

        try {
            String feedbackId = p[0];
            String appointmentId = p[1];
            String customerId = p[2];
            String technicianId = p[3];
            String counterStaffId = p[4];
            int rating = Integer.parseInt(p[5]);
            String comment = p[6];
            LocalDateTime commentDateTime = parseDateTime(p[7]);

            return new CustomerFeedback(feedbackId, appointmentId, customerId, technicianId, counterStaffId, rating, comment, commentDateTime);
        } catch (Exception e) {
            System.err.println("Error parsing feedback data: " + line + " - " + e.getMessage());
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

