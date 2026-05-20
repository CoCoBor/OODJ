package backend.models;

public class ServicePrice {
    private String serviceName; // e.g., "Normal", "Major"
    private int price; // price in whole currency units (RM)

    public ServicePrice() {}

    public ServicePrice(String serviceName, int price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
