package src.backend.models;


import java.time.LocalDateTime;
import java.util.Objects;

public class Feedback {
    private String feedbackId;
    private String appointmentId;
    private String customerId;
    private String technicianId;
    private String counterStaffId;
    private int rating;
    private String comment;
    private LocalDateTime commentDateTime;

    public Feedback(String feedbackId, String appointmentId, String customerId, String technicianId, String counterStaffId, int rating, String comment, LocalDateTime commentDateTime) {
        setFeedbackId(feedbackId);
        setAppointmentId(appointmentId);
        setCustomerId(customerId);
        setTechnicianId(technicianId);
        setCounterStaffId(counterStaffId);
        setRating(rating);
        setComment(comment);
        setcommentDateTime(commentDateTime);
    }

    
    public String getFeedbackId() {
        return feedbackId;
    }
    
    public void setFeedbackId(String feedbackId) {
        this.feedbackId = requireNonBlank(feedbackId, "feedbackId");
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = requireNonBlank(appointmentId, "appointmentId");
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = requireNonBlank(customerId, "customerId");
    }
    
    public String getTechnicianId() {
        return technicianId;
    }
    
    public void setTechnicianId(String technicianId) {
        this.technicianId = requireNonBlank(technicianId, "technicianId");
    }
    
    public String getCounterStaffId() {
        return counterStaffId;
    }
    
    public void setCounterStaffId(String counterStaffId) {
        this.counterStaffId = requireNonBlank(counterStaffId, "counterStaffId");
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment == null ? "" : comment.trim();
    }
    
    public LocalDateTime getcommentDateTime() {
        return commentDateTime;
    }

    public void setcommentDateTime(LocalDateTime commentDateTime) {
        this.commentDateTime = Objects.requireNonNull(commentDateTime, "commentDateTime");
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}

