package backend.models;

import backend.models.enums.ServiceType;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServicePrice {
    private String priceId;
    private ServiceType serviceType;
    private int price;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedDateTime;

    
    public ServicePrice(String priceId, ServiceType serviceType, int price,
                        String lastUpdatedBy, LocalDateTime lastUpdatedDateTime) {
        setPriceId(priceId);
        setServiceType(serviceType);
        setPrice(price);
        setLastUpdatedBy(lastUpdatedBy);
        setLastUpdatedDateTime(lastUpdatedDateTime);
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = requireNonBlank(priceId, "priceId");
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = Objects.requireNonNull(serviceType, "serviceType cannot be null");
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        Integer validatedPrice = Objects.requireNonNull(price, "price cannot be null");
        if (validatedPrice < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }
        this.price = validatedPrice;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = requireNonBlank(lastUpdatedBy, "lastUpdatedBy");
    }

    public LocalDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    public void setLastUpdatedDateTime(LocalDateTime lastUpdatedDateTime) {
        this.lastUpdatedDateTime = Objects.requireNonNull(lastUpdatedDateTime, "lastUpdatedDateTime cannot be null");
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}