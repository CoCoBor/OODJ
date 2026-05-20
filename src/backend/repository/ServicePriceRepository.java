package src.backend.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import src.backend.models.ServicePrice;
import src.backend.models.enums.ServiceType;

public class ServicePriceRepository implements InterfaceRepo<ServicePrice> {
    private static final String FILE_PATH = "src/data/service_prices.txt";

    @Override
    public void save(ServicePrice servicePrice) {
        try {
            List<String> lines = readAllLines();
            String servicePriceLine = entityToString(servicePrice);
            lines.add(servicePriceLine);
            writeAllLines(lines);
        } catch (IOException e) {
            System.err.println("Error saving service price: " + e.getMessage());
        }
    }

    @Override
    public Optional<ServicePrice> findById(String id) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    ServicePrice servicePrice = stringToEntity(line);
                    if (servicePrice != null && servicePrice.getPriceId().equals(id)) {
                        return Optional.of(servicePrice);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding service price with id " + id + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<ServicePrice> findAll() {
        List<ServicePrice> servicePrices = new ArrayList<>();
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    ServicePrice servicePrice = stringToEntity(line);
                    if (servicePrice != null) {
                        servicePrices.add(servicePrice);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding all service prices: " + e.getMessage());
        }
        return servicePrices;
    }

    @Override
    public void update(ServicePrice servicePrice) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    ServicePrice existingServicePrice = stringToEntity(line);
                    if (existingServicePrice != null && existingServicePrice.getPriceId().equals(servicePrice.getPriceId())) {
                        updatedLines.add(entityToString(servicePrice));
                    } else {
                        updatedLines.add(line);
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error updating service price: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    ServicePrice servicePrice = stringToEntity(line);
                    if (servicePrice != null && !servicePrice.getPriceId().equals(id)) {
                        updatedLines.add(line);
                    }
                }
            }
            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error deleting service price: " + e.getMessage());
        }
    }

    private List<String> readAllLines() throws IOException {
        Path path = Paths.get(FILE_PATH);

        return Files.readAllLines(path);
    }

    private void writeAllLines(List<String> lines) throws IOException {
        Path path = Paths.get(FILE_PATH);
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String entityToString(ServicePrice servicePrice) {
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append(servicePrice.getPriceId()).append("|")
                .append(servicePrice.getServiceType().toString()).append("|")
                .append(servicePrice.getPrice()).append("|")
                .append(servicePrice.getLastUpdatedBy()).append("|")
                .append(formatDateTime(servicePrice.getLastUpdatedDateTime()));
        return baseInfo.toString();
    }

    private ServicePrice stringToEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] p = line.split("\\|");

        if (p.length != 5) {
            System.err.println("Invalid service price data format: " + line);
            return null;
        }

        try {
            String priceId = p[0];
            ServiceType serviceType = ServiceType.valueOf(p[1]);
            int price = Integer.parseInt(p[2]);
            String lastUpdatedBy = p[3];
            LocalDateTime lastUpdatedDateTime = parseDateTime(p[4]);

            return new ServicePrice(priceId, serviceType, price, lastUpdatedBy, lastUpdatedDateTime);
        } catch (Exception e) {
            System.err.println("Error parsing service price data: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, dateFormat);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTime == null ? "" : dateTime.format(dateFormat);
	}
    
}

