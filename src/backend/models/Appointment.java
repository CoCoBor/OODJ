package backend.models;

import backend.models.enums.AppointmentStatus;
import backend.models.enums.ServiceType;
import java.time.LocalDateTime;
import java.util.Objects;

public class Appointment {

    private String appointmentId;
    private String customerId;
    private String technicianId;
    private String counterStaffId;
    private ServiceType serviceType;
    private AppointmentStatus status;
    private LocalDateTime scheduledStartDateTime;
    private LocalDateTime expectedEndDateTime;
    private LocalDateTime appointmentCreated;
    private String notes;

    public Appointment(String appointmentId, String customerId, String technicianId, String counterStaffId, ServiceType serviceType, AppointmentStatus status, LocalDateTime scheduledStartDateTime, LocalDateTime expectedEndDateTime, LocalDateTime appointmentCreated, String notes) {
        setAppointmentId(appointmentId);
        setCustomerId(customerId);
        setTechnicianId(technicianId);
        setCounterStaffId(counterStaffId);
        setServiceType(serviceType);
        setStatus(status);
        setScheduledStartDateTime(scheduledStartDateTime);
        setExpectedEndDateTime(expectedEndDateTime);
        setAppointmentCreatedTime(appointmentCreated);
        setNotes(notes);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = requireNonBlank(appointmentId, "Appointment ID");
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = requireNonBlank(customerId, "Customer ID");
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        if (technicianId == null) {
            this.technicianId = "";
            return;
        }
        this.technicianId = technicianId.trim();
    }

    public String getCounterStaffId() {
        return counterStaffId;
    }

    public void setCounterStaffId(String counterStaffId) {
        this.counterStaffId = requireNonBlank(counterStaffId, "Counter Staff ID");
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = Objects.requireNonNull(serviceType, "Service Type cannot be null");
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public LocalDateTime getScheduledStartDateTime() {
        return scheduledStartDateTime;
    }

    public void setScheduledStartDateTime(LocalDateTime scheduledStartDateTime) {
        this.scheduledStartDateTime = Objects.requireNonNull(scheduledStartDateTime, "Scheduled Start Date Time cannot be null");
    }

    public LocalDateTime getExpectedEndDateTime() {
        return expectedEndDateTime;
    }

    public void setExpectedEndDateTime(LocalDateTime expectedEndDateTime) {
        this.expectedEndDateTime = Objects.requireNonNull(expectedEndDateTime, "Expected End Date Time cannot be null");
        if (this.scheduledStartDateTime != null && expectedEndDateTime.isBefore(this.scheduledStartDateTime)) {
            throw new IllegalArgumentException("Expected End Date Time cannot be before Scheduled Start Date Time");
        }
    }

    public LocalDateTime getAppointmentCreatedTime() {
        return appointmentCreated;
    }

    public void setAppointmentCreatedTime(LocalDateTime appointmentCreatedTime) {
        this.appointmentCreated = Objects.requireNonNull(appointmentCreatedTime, "Appointment Created cannot be null");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
