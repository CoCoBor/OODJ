public class Appointment {
    private String customerName;
    private String customerTP;
    private String plate;
    private String service;
    private String technician;
    private String techTP;
    private String date;
    private String time;
    private String status;
    private String paymentStatus;


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
