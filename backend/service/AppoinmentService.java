package backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import backend.models.Appointment;
import backend.models.Technician;
import backend.models.User;
import backend.models.enums.AppointmentStatus;
import backend.models.enums.Role;
import backend.models.enums.ServiceType;
import backend.repository.AppointmentRepository;
import backend.repository.UserRepository;
import backend.util.IdGenerator;
import backend.util.SessionManager;

public class AppoinmentService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final AppointmentRepository appointmentRepository;

    public AppoinmentService(AppointmentRepository appointmentRepository, UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment createAppointment(String customerId, ServiceType serviceType, LocalDateTime scheduledStartDateTime, LocalDateTime expectedEndDateTime, String notes) {
        User actor = sessionManager.getCurrentUser();
        String actorUserId = actor.getUserId();

        String normalizedCustomerId = requireNonBlank(customerId, "customerId");
        ServiceType normalizedServiceType = Objects.requireNonNull(serviceType, "serviceType");
        String appointmentId = IdGenerator.nextAppointmentId(extractAppointmentIds(appointmentRepository.findAll()));
        LocalDateTime appointmentCreated = LocalDateTime.now();

        LocalDateTime normalizedScheduledStartDateTime = Objects.requireNonNull(scheduledStartDateTime, "scheduledStartDateTime");
        LocalDateTime normalizedExpectedEndDateTime = Objects.requireNonNull(expectedEndDateTime, "expectedEndDateTime");
        if (normalizedServiceType == ServiceType.NORMAL_SERVICE) {
            normalizedExpectedEndDateTime = normalizedScheduledStartDateTime.plusHours(1);
        } else if (normalizedServiceType == ServiceType.MAJOR_SERVICE) {
            normalizedExpectedEndDateTime = normalizedScheduledStartDateTime.plusHours(3);
        } else if (normalizedServiceType == ServiceType.OTHER) {
            normalizedExpectedEndDateTime = expectedEndDateTime;
        } else {
            throw new ServiceException("Unsupported service type: " + normalizedServiceType);
        }

        User customer = findUserById(normalizedCustomerId);
        if (customer.getRole() != Role.CUSTOMER) {
            throw new ServiceException("User is not a customer: " + normalizedCustomerId);
        }

        String normalizedNotes = notes == null ? "" : notes.trim();

        Appointment appointment = new Appointment(appointmentId, normalizedCustomerId, null, actorUserId, normalizedServiceType, AppointmentStatus.PENDING, normalizedScheduledStartDateTime, normalizedExpectedEndDateTime, appointmentCreated, normalizedNotes);
        appointmentRepository.save(appointment);
        return appointment;
    }

    public Appointment assignTechnician(String appointmentId, String technicianId) {
        String normalizedAppointmentId = requireNonBlank(appointmentId, "appointmentId");
        String normalizedTechnicianId = requireNonBlank(technicianId, "technicianId");
        Appointment appointment = findAppointmentById(normalizedAppointmentId);
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new ServiceException("Only pending appointments can be assigned a technician");
        }
        User tech = findUserById(normalizedTechnicianId);
        if (tech.getRole() != Role.TECHNICIAN) {
            throw new ServiceException("User is not a technician: " + normalizedTechnicianId);
        }

        Technician technician = (Technician) tech;
        if (!technician.getIsAvailable()) {
            throw new ServiceException("Technician " + technician.getUsername() + " is currently busy or unavailable!");
        }

        appointment.setTechnicianId(normalizedTechnicianId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        technician.setIsAvailable(false);
        appointmentRepository.update(appointment);
        userRepository.update(technician);
        return appointment;

    }

    public Appointment updateStatus(String appointmentId, AppointmentStatus newStatus, String completionNote) {
        String normalizedAppointmentId = requireNonBlank(appointmentId, "appointmentId");
        AppointmentStatus normalizedNewStatus = Objects.requireNonNull(newStatus, "newStatus");
        Appointment appointment = findAppointmentById(normalizedAppointmentId);
        appointment.setStatus(normalizedNewStatus);

        String technicianId = appointment.getTechnicianId();
        if (normalizedNewStatus == AppointmentStatus.CANCELLED) {
            if (technicianId != null) {
                User tech = findUserById(technicianId);
                if (tech.getRole() == Role.TECHNICIAN) {
                    Technician technician = (Technician) tech;
                    technician.setIsAvailable(true);
                    userRepository.update(technician);
                }
            }
        } else if (normalizedNewStatus == AppointmentStatus.COMPLETED) {
            String normalizedNote = requireNonBlank(completionNote, "completionNote");
            appointment.setNotes(normalizedNote);
            if (technicianId != null) {
                User tech = findUserById(technicianId);
                if (tech.getRole() == Role.TECHNICIAN) {
                    Technician technician = (Technician) tech;
                    technician.setIsAvailable(true);
                    userRepository.update(technician);
                }
            }

        }
        appointmentRepository.update(appointment);
        return appointment;
    }

    public List<Appointment> getAppointmentsListForCustomer(String customerId, AppointmentStatus statusFilter) {
        String normalizedCustomerId = requireNonBlank(customerId, "customerId");
        List<Appointment> matches = new ArrayList<>();
        List<Appointment> allAppointments = appointmentRepository.findAll();
        User customer = findUserById(normalizedCustomerId);
        if (customer.getRole() != Role.CUSTOMER) {
            throw new ServiceException("User is not a customer: " + normalizedCustomerId);
        }

        for (Appointment app : allAppointments) {
            if (app.getCustomerId().equals(normalizedCustomerId)) {
                if (statusFilter == null || app.getStatus() == statusFilter) {
                    matches.add(app);
                }
            }
        }
        return matches;
    }

    public List<Appointment> getAppointmentsListForTechnician(String technicianId, AppointmentStatus statusFilter) {
        String normalizedTechnicianId = requireNonBlank(technicianId, "technicianId");
        List<Appointment> matches = new ArrayList<>();
        List<Appointment> allAppointments = appointmentRepository.findAll();
        User tech = findUserById(normalizedTechnicianId);
        if (tech.getRole() != Role.TECHNICIAN) {
            throw new ServiceException("User is not a technician: " + normalizedTechnicianId);
        }

        for (Appointment app : allAppointments) {
            if (normalizedTechnicianId.equals(app.getTechnicianId())) {
                if (statusFilter == null || app.getStatus() == statusFilter) {
                    matches.add(app);
                }
            }
        }
        return matches;
    }

    public List<Appointment> getAllAppointments(AppointmentStatus statusFilter) {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        List<Appointment> matches = new ArrayList<>();
        for (Appointment app : allAppointments) {
            if (statusFilter == null || app.getStatus() == statusFilter) {
                matches.add(app);
            }
        }
        return matches;
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found: " + userId));
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

    private List<String> extractAppointmentIds(List<Appointment> appointments) {
        List<String> ids = new ArrayList<>();
        for (Appointment appointment : appointments) {
            ids.add(appointment.getAppointmentId());
        }
        return ids;
    }
}
