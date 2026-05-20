package backend.models;

import backend.models.enums.AppointmentStatus;
import java.sql.Time;
import java.util.Date;

public class Appointment {
    private String appointmentId;
    private String customerTP;
    private String plate;
    private String service;
    private String technicianTP;
    private String counterStaffTP;
    private Date date;
    private Time time;
    private AppointmentStatus status;
    private Boolean paymentStatus;


public Appointment(String appointmentId, String customerTP, String plate, String service, String technicianTP, String counterStaffTP, Date date, Time time, AppointmentStatus status, Boolean paymentStatus) {
    this.appointmentId = appointmentId;
    this.customerTP = customerTP;
    this.plate = plate;
    this.service = service;
    this.technicianTP = technicianTP;
    this.counterStaffTP = counterStaffTP;
    this.date = date;
    this.time = time;
    this.status = status;
    this.paymentStatus = paymentStatus;
}
 
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getCustomerTP() {
        return customerTP;
    }

    public String getPlate() {
        return plate;
    }

    public String getService() {
        return service;
    }

    public String getTechnicianTP() {
        return technicianTP;
    }

    public String getCounterStaffTP() {
        return counterStaffTP;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = requireNonBlank(appointmentId, "Appointment ID");
    }

    public void setCustomerTP(String customerTP) {
        this.customerTP = requireNonBlank(customerTP, "Customer TP");
    }

    public void setPlate(String plate) {
        this.plate = requireNonBlank(plate, "Plate");
    }

    public void setService(String service) {
        this.service = requireNonBlank(service, "Service");
    }

    public void setTechnicianTP(String technicianTP) {
        this.technicianTP = requireNonBlank(technicianTP, "Technician TP");
    }

    public void setCounterStaffTP(String counterStaffTP) {
        this.counterStaffTP = requireNonBlank(counterStaffTP, "Counter Staff TP");
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    protected String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
