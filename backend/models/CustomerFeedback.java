package backend.models;

import java.time.LocalDateTime;

public class CustomerFeedback extends Feedback {
    public CustomerFeedback(String feedbackId, String appointmentId, String customerId, 
                            String technicianId, String counterStaffId, int rating, String comment, LocalDateTime commentDateTime) {
        super(feedbackId, appointmentId, customerId, technicianId, counterStaffId, rating, comment, commentDateTime);
    }
}
