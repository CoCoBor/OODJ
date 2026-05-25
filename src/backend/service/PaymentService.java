package backend.service;

import backend.models.Appointment;
import backend.models.Payment;
import backend.models.ServicePrice;
import backend.models.enums.AppointmentStatus;
import backend.models.enums.PaymentMethod;
import backend.models.enums.ServiceType;
import backend.repository.AppointmentRepository;
import backend.repository.PaymentRepository;
import backend.repository.ServicePriceRepository;
import backend.util.IdGenerator;
import backend.util.SessionManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentService {

    private final SessionManager sessionManager;
    private final ServicePriceRepository servicePriceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(SessionManager sessionManager, ServicePriceRepository servicePriceRepository,
                          AppointmentRepository appointmentRepository, PaymentRepository paymentRepository) {
        this.sessionManager = sessionManager;
        this.servicePriceRepository = servicePriceRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
    }

    public Payment CreatePayment(String appointmentId, PaymentMethod paymentMethod) {
        String normalizedAppointmentId = requireNonBlank(appointmentId, "Appointment ID");
        PaymentMethod normalizedPaymentMethod = Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        Appointment appointment = findAppointmentById(normalizedAppointmentId);
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ServiceException("Payment is only allowed for COMPLETED appointments");
        }
         List<Payment> existingPayments = paymentRepository.findAll();
        for (Payment existingPayment : existingPayments) {
            if (normalizedAppointmentId.equals(existingPayment.getAppointmentId())) {
                throw new ServiceException("Payment already exists for appointment: " + normalizedAppointmentId);
            }
        }
        
        String paymentId = IdGenerator.nextPaymentId(extractPaymentIds(existingPayments));
        int amount = getServicePrice(appointment.getServiceType());
        LocalDateTime paidDateTime = LocalDateTime.now().withSecond(0).withNano(0);
        String receiptNumber = generateReceiptNumber(existingPayments, paidDateTime);

        Payment payment = new Payment(
                paymentId,
                normalizedAppointmentId,
                amount,
                normalizedPaymentMethod,
                paidDateTime,
                receiptNumber
        );

        paymentRepository.save(payment);

        return payment;
    }

    public String generateReceipt(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        Appointment appointment = findAppointmentById(payment.getAppointmentId());

        StringBuilder builder = new StringBuilder();
        builder.append("===== PAYMENT RECEIPT =====\n");
        builder.append("Receipt Number: ").append(payment.getReceiptNumber()).append("\n");
        builder.append("Payment ID: ").append(payment.getPaymentId()).append("\n");
        builder.append("Appointment ID: ").append(payment.getAppointmentId()).append("\n");
        builder.append("Customer ID: ").append(appointment.getCustomerId()).append("\n");
        builder.append("Service Type: ").append(appointment.getServiceType()).append("\n");
        builder.append("Amount: ").append(payment.getAmount()).append("\n");
        builder.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n");
        builder.append("Paid At: ").append(payment.getPaidDateTime()).append("\n");
        builder.append("===========================\n");
        return builder.toString();
    }

    public List<Appointment> getCompletedButUnpaidAppointment(){
        List<Appointment> matches = new ArrayList<>();
        List<Appointment> allApp = appointmentRepository.findAll();
        for(Appointment app : allApp){
            if(app.getStatus() == AppointmentStatus.COMPLETED){
                Payment payment = findPaymentByAppointmentId(app.getAppointmentId());
                if(payment == null){
                    matches.add(app);
                }
            }
        }

        return matches;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getAllPaymentsForCustomer(String customerId) {
        List<Payment> matches = new ArrayList<>();
        List<Payment> payments = paymentRepository.findAll();
        for (Payment payment : payments) {
            Appointment appointment = findAppointmentById(payment.getAppointmentId());
            if (appointment.getCustomerId().equals(customerId)) {
                matches.add(payment);
            }
        }
        return matches;
    }

    private List<String> extractPaymentIds(List<Payment> payments) {
        List<String> ids = new ArrayList<>();
        for (Payment payment : payments) {
            ids.add(payment.getPaymentId());
        }
        return ids;
    }

    private int getServicePrice(ServiceType serviceType) {
        ServiceType normalizedServiceType = Objects.requireNonNull(serviceType, "Service type cannot be null");
        ServicePrice latestPrice = null;
        List<ServicePrice> prices = servicePriceRepository.findAll();
        for (ServicePrice price : prices) {
            if (price.getServiceType() == normalizedServiceType) {
                latestPrice = price;
            }
        }
        if (latestPrice == null) {
            throw new ServiceException("Price not found for service type: " + normalizedServiceType);
        }
        return latestPrice.getPrice();
    }

    private String generateReceiptNumber(List<Payment> existingPayments, LocalDateTime paymentDateTime) {
        DateTimeFormatter RECEIPT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateToken = paymentDateTime.format(RECEIPT_DATE_FORMAT);
        String prefix = "REC-" + dateToken + "-";
        int maxSequence = 0;

        for (Payment payment : existingPayments) {
            String receiptNumber = payment.getReceiptNumber();
            if (receiptNumber == null || !receiptNumber.startsWith(prefix)) {
                continue;
            }

            String suffix = receiptNumber.substring(prefix.length());
            try {
                int sequence = Integer.parseInt(suffix);
                if (sequence > maxSequence) {
                    maxSequence = sequence;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return prefix + String.format("%03d", maxSequence + 1);
    }
    

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ServiceException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ServiceException("Appointment not found: " + appointmentId));
    }

    private Payment findPaymentByAppointmentId(String appointmentId) {
        List<Payment> payments = paymentRepository.findAll();
        for (Payment p : payments) {
            if (p.getAppointmentId().equals(appointmentId)) {
                return p;
            }
        }
        return null;
    }

}
