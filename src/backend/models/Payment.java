package backend.models;

import backend.models.enums.PaymentMethod;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {

    private String paymentId;
    private String appointmentId;
    private int amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidDateTime;
    private String receiptNumber;

    public Payment(String paymentId, String appointmentId, int amount, PaymentMethod paymentMethod, LocalDateTime paidDateTime, String receiptNumber) {
        setPaymentId(paymentId);
        setAppointmentId(appointmentId);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setPaidDateTime(paidDateTime);
        setReceiptNumber(receiptNumber);
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = requireNonBlank(paymentId, "Payment ID");
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = requireNonBlank(appointmentId, "Appointment ID");
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Payment Method cannot be null");
    }

    public LocalDateTime getPaidDateTime() {
        return paidDateTime;
    }

    public void setPaidDateTime(LocalDateTime paidDateTime) {
        this.paidDateTime = Objects.requireNonNull(paidDateTime, "Paid Date Time cannot be null");
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = requireNonBlank(receiptNumber, "Receipt Number");
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

}
