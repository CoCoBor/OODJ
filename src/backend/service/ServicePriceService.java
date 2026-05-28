package backend.service;

import backend.models.ServicePrice;
import backend.models.enums.ServiceType;
import backend.repository.ServicePriceRepository;
import backend.util.IdGenerator;
import backend.util.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicePriceService {
    private final SessionManager sessionManager;
    private final ServicePriceRepository servicePriceRepository;

    public ServicePriceService(ServicePriceRepository servicePriceRepository, SessionManager sessionManager) {
        this.servicePriceRepository = servicePriceRepository;
        this.sessionManager = sessionManager;
    }

    public ServicePrice updatePrice(ServiceType serviceType, int newPrice) {
        String actorId = sessionManager.getCurrentUser().getUserId();
        String actorName = sessionManager.getCurrentUser().getUsername();
        String actor = actorName + " (" + actorId + ")";

        ServiceType normalizedServiceType = Objects.requireNonNull(serviceType, "Service type cannot be null");
        int normalizedNewPrice = validatePrice(newPrice, "New price cannot be null");
        
        List<ServicePrice> existingPrices = servicePriceRepository.findAll();
        String newPriceId = IdGenerator.nextPriceId(extractPriceIds(existingPrices));

        ServicePrice newServicePrice = new ServicePrice(newPriceId, normalizedServiceType, normalizedNewPrice, actor, java.time.LocalDateTime.now());
        
        servicePriceRepository.save(newServicePrice);
        return newServicePrice;
    }

    public int getPriceByServiceType(ServiceType serviceType) {
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

    public List<ServicePrice> getAllPrices() {
        return servicePriceRepository.findAll();
    }

    private int validatePrice(Integer price, String errorMessage) {
        Integer validatedPrice = Objects.requireNonNull(price, errorMessage);
        if (validatedPrice < 0) {
            throw new ServiceException("Price cannot be negative");
        }
        
        return validatedPrice;
    }

        private List<String> extractPriceIds(List<ServicePrice> prices) {
        List<String> ids = new ArrayList<>();
        for (ServicePrice price : prices) {
            ids.add(price.getPriceId());
        }
        return ids;
    }
}
