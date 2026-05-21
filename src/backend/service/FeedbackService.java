package backend.service;

import backend.models.Appointment;
import backend.models.CustomerFeedback;
import backend.repository.AppointmentRepository;
import backend.repository.FeedbackRepository;
import backend.repository.UserRepository;
import backend.util.IdGenerator;
import backend.util.SessionManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public FeedbackService(FeedbackRepository feedbackRepository, AppointmentRepository appointmentRepository, UserRepository userRepository, SessionManager sessionManager) {
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    public CustomerFeedback customerFeedback(String appointmentId, String customerId, int rating, String comments) {
        String normalizedAppointmentId = requireNonBlank(appointmentId, "Appointment ID");
        String normalizedCustomerId = requireNonBlank(customerId, "Customer ID");

        Appointment appointment = findAppointmentById(normalizedAppointmentId);
        String feedbackId = IdGenerator.nextFeedbackId(extractFeedbackIds(feedbackRepository.findAll()));

        CustomerFeedback feedback = new CustomerFeedback(
                feedbackId,
                normalizedAppointmentId,
                normalizedCustomerId,
                appointment.getTechnicianId(),
                appointment.getCounterStaffId(),
                rating,
                comments == null ? "" : comments.trim(),
                LocalDateTime.now()
        );

        feedbackRepository.save(feedback);
        return feedback;

    }

    public List<CustomerFeedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public List<CustomerFeedback> getCustomerFeedback(String customerId) {
        String normalizedCustomerId = requireNonBlank(customerId, "Customer ID");
        List<CustomerFeedback> matches = new ArrayList<>();
        List<CustomerFeedback> feedbacks = feedbackRepository.findAll();
        for (CustomerFeedback feedback : feedbacks) {
            if (feedback.getCustomerId().equals(normalizedCustomerId)) {
                matches.add(feedback);
            }
        }
        return matches;
    }

    public List<CustomerFeedback> getTechnicianFeedback(String technicianId) {
        String normalizedTechnicianId = requireNonBlank(technicianId, "Technician ID");
        List<CustomerFeedback> matches = new ArrayList<>();
        List<CustomerFeedback> feedbacks = feedbackRepository.findAll();
        for (CustomerFeedback feedback : feedbacks) {
            if (feedback.getTechnicianId().equals(normalizedTechnicianId)) {
                matches.add(feedback);
            }
        }
        return matches;
    }

    private List<String> extractFeedbackIds(List<CustomerFeedback> feedbacks) {
        List<String> ids = new ArrayList<>();
        for (CustomerFeedback feedback : feedbacks) {
            ids.add(feedback.getFeedbackId());
        }
        return ids;
    }

        private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ServiceException("Appointment not found: " + appointmentId));
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ServiceException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

}
