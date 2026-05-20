package backend.models;

import java.sql.Time;
import java.util.Date;

public class Feedback {
    private String feedbackId;
    private String appointmentId;
    private String customerTP;
    private Date date;
    private Time time;
    private String technicianTP;
    private String counterStaffTP;
    private String feedbackText;

    public Feedback(String feedbackId, String appointmentId, String customerTP, Date date, Time time, String technicianTP, String counterStaffTP, String feedbackText) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.customerTP = customerTP;
        this.date = date;
        this.time = time;
        this.technicianTP = technicianTP;
        this.counterStaffTP = counterStaffTP;
        this.feedbackText = feedbackText;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getCustomerTP() { return customerTP; }
    public void setCustomerTP(String customerTP) { this.customerTP = customerTP; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }

    public String getTechnicianTP() { return technicianTP; }
    public void setTechnicianTP(String technicianTP) { this.technicianTP = technicianTP; }

    public String getCounterStaffTP() { return counterStaffTP; }
    public void setCounterStaffTP(String counterStaffTP) { this.counterStaffTP = counterStaffTP; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
}
