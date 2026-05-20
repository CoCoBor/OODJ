package backend.models;

public class Appointment {
    private final String customerName;
    private final String customerTP;
    private final String plate;
    private final String service;
    private final String technician;
    private final String techTP;
    private final String date;
    private final String time;
    private final String status;
    private final String paymentStatus;


public Appointment(String customerName, String customerTP, String plate, String service, String technician, String techTP, String date, String time, String status, String paymentStatus) {
    this.customerName = customerName;
    this.customerTP = customerTP;
    this.plate = plate;
    this.service = service;
    this.technician = technician;
    this.techTP = techTP;
    this.date = date;
    this.time = time;
    this.status = status;
    this.paymentStatus = paymentStatus;
}
 
    public String getCustomerName() {
        return customerName;
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

    public String getTechnician() {
        return technician;
    }

    public String getTechTP() {
        return techTP;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
