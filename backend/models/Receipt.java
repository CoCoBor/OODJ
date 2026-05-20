package backend.models;

public class Receipt {
    private String receiptId;
    private String customerName;
    private String customerTP;
    private String plate;
    private String service;
    private int price;
    private String date;
    private String time;
    private String technician;

    public Receipt() {}

    public Receipt(String receiptId, String customerName, String customerTP, String plate, String service, int price, String date, String time, String technician) {
        this.receiptId = receiptId;
        this.customerName = customerName;
        this.customerTP = customerTP;
        this.plate = plate;
        this.service = service;
        this.price = price;
        this.date = date;
        this.time = time;
        this.technician = technician;
    }

    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerTP() { return customerTP; }
    public void setCustomerTP(String customerTP) { this.customerTP = customerTP; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }
}
