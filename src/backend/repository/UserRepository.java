package backend.repository;

import backend.models.CounterStaff;
import backend.models.Customer;
import backend.models.Manager;
import backend.models.Technician;
import backend.models.User;
import backend.models.enums.Role;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class UserRepository implements InterfaceRepo<User> {

    private static final String FILE_PATH = "src/text/users.txt";

    @Override
    public void save(User user) {
        try {
            List<String> lines = readAllLines();
            String userLine = entityToString(user);
            lines.add(userLine);
            writeAllLines(lines);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    User user = stringToEntity(line);
                    if (user != null && user.getUserId().equals(id)) {
                        return Optional.of(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding user with id " + id + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {

                    User user = stringToEntity(line);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void update(User user) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    User existingUser = stringToEntity(line);
                    if (existingUser != null && existingUser.getUserId().equals(user.getUserId())) {
                        updatedLines.add(entityToString(user));
                    } else {
                        updatedLines.add(line);
                    }
                }
            }

            writeAllLines(updatedLines);

        } catch (IOException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    User user = stringToEntity(line);
                    if (user != null && !user.getUserId().equals(id)) {
                        updatedLines.add(line);
                    }
                }
            }

            writeAllLines(updatedLines);
        } catch (IOException e) {
            System.err.println("Error deleting user with id " + id + ": " + e.getMessage());
        }
    }

    public Optional<User> findOne(Predicate<User> filter) {
        try {
            List<String> lines = readAllLines();
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                User entity = stringToEntity(line);
                if (entity != null && filter.test(entity)) {
                    return Optional.of(entity);
                }
            }
        } catch (IOException e) {
            System.err.println("Error during findOne: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        return findOne(u -> u.getEmail().equals(email)).isPresent();
    }

    private List<String> readAllLines() throws IOException {
        Path path = Paths.get(FILE_PATH);

        return Files.readAllLines(path);
    }

    private void writeAllLines(List<String> lines) throws IOException {
        Path path = Paths.get(FILE_PATH);
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String entityToString(User user) {
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append(user.getUserId()).append("|")
                .append(user.getUsername()).append("|")
                .append(user.getPassword()).append("|")
                .append(user.getRole().toString()).append("|")
                .append(user.getEmail()).append("|")
                .append(user.getPhone().toString());

        if (user instanceof Technician) {
            Technician t = (Technician) user;
            return baseInfo.toString() + "|" + t.getSpecialization() + "|" + t.getIsAvailable();
        } else if (user instanceof Customer) {
            Customer c = (Customer) user;
            return baseInfo.toString() + "|" + c.getVehicleModel() + "|" + c.getVehiclePlate();
        }

        return baseInfo.toString(); // For Manager or CounterStaff
    }

    private User stringToEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null; // Skip empty lines
        }

        String[] p = line.split("\\|");

        String userId = p[0].trim();
        String username = p[1].trim();
        String password = p[2].trim();
        Role role = Role.valueOf(p[3].trim());
        String email = p[4].trim();
        String phone = p[5].trim();

        switch (role) {
            case MANAGER:
                return new Manager(userId, username, password, email, phone);
            case COUNTER_STAFF:
                return new CounterStaff(userId, username, password, email, phone);
            case TECHNICIAN:
                String specialization = p[6].trim();
                Boolean isAvailable = Boolean.parseBoolean(p[7].trim());
                return new Technician(userId, username, password, email, phone, specialization, isAvailable);
            case CUSTOMER:
                String vehicleModel = p[6].trim();
                String vehiclePlate = p[7].trim();
                return new Customer(userId, username, password, email, phone, vehicleModel, vehiclePlate);
            default:
                break;
        }
        return null;
    }

}

