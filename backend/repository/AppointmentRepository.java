package backend.repository;


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
import backend.models.Appointment;
import backend.models.enums.AppointmentStatus;
import backend.models.enums.ServiceType;

public class AppointmentRepository implements InterfaceRepo<Appointment> {

    private static final String FILE_PATH = "src/data/appointments.txt";

    @Override
    public void save(Appointment appointment) {
        try {
            List<String> lines = readAllLines();
            String userLine = entityToString(appointment);
            lines.add(userLine);
            writeAllLines(lines);
        } catch (IOException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
        }
    }

    @Override
    public Optional<Appointment> findById(String id) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if(!line.trim().isEmpty()){
                    Appointment appointment = stringToEntity(line);
                    if (appointment != null && appointment.getAppointmentId().equals(id)) {
                        return Optional.of(appointment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding appointment by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if(!line.trim().isEmpty()){
                    Appointment appointment = stringToEntity(line);
                    if (appointment != null) {
                        appointments.add(appointment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding all appointments: " + e.getMessage());
        }
        return appointments;
    }

    @Override
    public void update(Appointment appointment) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if(!line.trim().isEmpty()){
                    Appointment existingAppointment = stringToEntity(line);
                    if (existingAppointment != null && existingAppointment.getAppointmentId().equals(appointment.getAppointmentId())) {
                        updatedLines.add(entityToString(appointment)); // Update the line with new appointment data
                    } else {
                        updatedLines.add(line); // Keep the existing line
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
        } 
    }

    @Override
    public void delete(String id) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if(!line.trim().isEmpty()){
                    Appointment appointment = stringToEntity(line);
                    if (appointment != null && !appointment.getAppointmentId().equals(id)) {
                        updatedLines.add(line); // Keep the line if it doesn't match the ID to delete
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
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

    private String entityToString(Appointment appointment) {
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append(appointment.getAppointmentId()).append("|")
                .append(appointment.getCustomerId()).append("|")
                .append(appointment.getTechnicianId()).append("|")
                .append(appointment.getCounterStaffId()).append("|")
                .append(appointment.getServiceType()).append("|")
                .append(appointment.getStatus().toString()).append("|")
                .append(formatDateTime(appointment.getScheduledStartDateTime())).append("|")
                .append(formatDateTime(appointment.getExpectedEndDateTime())).append("|")
                .append(formatDateTime(appointment.getAppointmentCreatedTime())).append("|")
                .append(appointment.getNotes());

        return baseInfo.toString();
    }

    private Appointment stringToEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null; // Skip empty lines
        }

        String[] p = line.split("\\|", -1);

        if (p.length < 10) {
            System.err.println("Invalid appointment data format: " + line);
            return null;
        }

        try {
            String appointmentId = p[0];
            String customerId = p[1];
            String technicianId = p[2];
            String counterStaffId = p[3];
            ServiceType serviceType = ServiceType.valueOf(p[4]);
            AppointmentStatus status = AppointmentStatus.valueOf(p[5]);
            LocalDateTime scheduledStartDateTime = parseDateTime(p[6]);
            LocalDateTime expectedEndDateTime = parseDateTime(p[7]);
            LocalDateTime appointmentCreatedTime = parseDateTime(p[8]);
            String notes = p[9];

            return new Appointment(appointmentId, customerId, technicianId, counterStaffId, serviceType, status, scheduledStartDateTime, expectedEndDateTime, appointmentCreatedTime, notes);
        } catch (Exception e) {
            System.err.println("Error parsing appointment data: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTimeStr, dateFormat);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return dateTime == null ? "" : dateTime.format(dateFormat);
	}
}
