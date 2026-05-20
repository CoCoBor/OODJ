package backend.models;

public class ServicePrice {
    private String serviceName;
    private int price;
    private String lastModifiedBy;
    private String lastModifiedDateTime;

    public ServicePrice(String serviceName, int price, String lastModifiedBy, String lastModifiedDateTime) {
        this.serviceName = serviceName;
        this.price = price;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getLastModifiedDateTime() { return lastModifiedDateTime; }
    public void setLastModifiedDateTime(String lastModifiedDateTime) { this.lastModifiedDateTime = lastModifiedDateTime; }
}
