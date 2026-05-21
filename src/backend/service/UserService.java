package backend.service;

import backend.models.Appointment;
import backend.models.CounterStaff;
import backend.models.Customer;
import backend.models.Manager;
import backend.models.Technician;
import backend.models.User;
import backend.models.enums.AppointmentStatus;
import backend.models.enums.Role;
import backend.repository.AppointmentRepository;
import backend.repository.UserRepository;
import backend.util.IdGenerator;
import backend.util.SessionManager;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final AppointmentRepository appointmentRepository;

    public UserService(UserRepository userRepository, SessionManager sessionManager, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
        this.appointmentRepository = appointmentRepository;
    }

    public User createStaff(Role role,
            String username,
            String password,
            String phone,
            String email,
            String specialisation,
            boolean isAvailable) {
        User currentUser = sessionManager.getCurrentUser();
        String normalizedUsername = requireNonBlank(username, "username");
        String normalizedPassword = validatePassword(requireNonBlank(password, "password"));
        String normalizedEmail = validateEmail(requireNonBlank(email, "email"));
        String normalizedPhone = validatePhone(requireNonBlank(phone, "phone"));

        ensureUsernameNEmailIsUnique(normalizedUsername, normalizedEmail,normalizedPhone, null);

        String userId = IdGenerator.nextUserId(extractUserIds(userRepository.findAll()));

        String hashedPassword;
        try {
            hashedPassword = User.hashPassword(normalizedPassword); // 仅在注册时哈希一次
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
        User newUser;

        if (role == Role.TECHNICIAN) {
            newUser = new Technician(userId, normalizedUsername, hashedPassword,
                    normalizedEmail, normalizedPhone, specialisation, isAvailable);
        } else if (role == Role.MANAGER) {
            newUser = new Manager(userId, normalizedUsername, hashedPassword,
                    normalizedEmail, normalizedPhone);
        } else if (role == Role.COUNTER_STAFF) {
            newUser = new CounterStaff(userId, normalizedUsername, hashedPassword,
                    normalizedEmail, normalizedPhone);
        } else {
            throw new ServiceException("Invalid role for staff creation: " + role);
        }

        userRepository.save(newUser);
        return newUser;
    }

    public Customer createCustomer(String username,
            String password,
            String phone,
            String email,
            String vehicleModel,
            String vehiclePlate) {

        String normalizedUsername = requireNonBlank(username, "username");
        String normalizedPassword = validatePassword(requireNonBlank(password, "password"));
        String normalizedPhone = validatePhone(requireNonBlank(phone, "phone"));
        String normalizedEmail = validateEmail(requireNonBlank(email, "email"));
        String normalizedVehicleModel = requireNonBlank(vehicleModel, "vehicleModel");
        String normalizedVehiclePlate = requireNonBlank(vehiclePlate, "vehiclePlate");

        ensureUsernameNEmailIsUnique(normalizedUsername, normalizedEmail, normalizedPhone, null);

        String userId = IdGenerator.nextUserId(extractUserIds(userRepository.findAll()));
        String hashedPassword;

        try {
            hashedPassword = User.hashPassword(normalizedPassword);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
        Customer newCustomer = new Customer(
                userId,
                normalizedUsername,
                hashedPassword,
                normalizedEmail,
                normalizedPhone,
                normalizedVehicleModel,
                normalizedVehiclePlate
        );

        userRepository.save(newCustomer);
        return newCustomer;
    }

    public Customer updateCustomer(String userId,
            String username,
            String phone,
            String email,
            String vehicleModel,
            String vehiclePlate) {

        String normalizedUserId = requireNonBlank(userId, "userId");
        String normalizedUsername = requireNonBlank(username, "username");
        String normalizedPhone = validatePhone(requireNonBlank(phone, "phone"));
        String normalizedEmail = validateEmail(requireNonBlank(email, "email"));
        String normalizedVehicleModel = requireNonBlank(vehicleModel, "vehicleModel");
        String normalizedVehiclePlate = requireNonBlank(vehiclePlate, "vehiclePlate");

        Customer target = (Customer) findUserById(normalizedUserId);
        if (target.getRole() != Role.CUSTOMER) {
            throw new ServiceException("User is not a customer: " + normalizedUserId);
        }

        ensureUsernameNEmailIsUnique(normalizedUsername, normalizedEmail, normalizedPhone, normalizedUserId);

        target.setUsername(normalizedUsername);
        target.setPhone(normalizedPhone);
        target.setEmail(normalizedEmail);
        target.setVehicleModel(normalizedVehicleModel);
        target.setVehiclePlate(normalizedVehiclePlate);

        userRepository.update(target);
        return target;
    }

    public void deleteCustomer(String userId) {

        String normalizedUserId = requireNonBlank(userId, "userId");

        Customer target = (Customer) findUserById(normalizedUserId);
        if (target.getRole() != Role.CUSTOMER) {
            throw new ServiceException("User is not a customer: " + normalizedUserId);
        }

        if (hasActiveAppointments(normalizedUserId)) {
            throw new ServiceException("Cannot delete customer with active appointments");
        }

        userRepository.delete(normalizedUserId);
    }

    public User updateProfile(String userId, String phone, String email, String newPassword) {
        String normalizedUserId = requireNonBlank(userId, "userId");
        String normalizedPhone = validatePhone(requireNonBlank(phone, "phone"));
        String normalizedEmail = validateEmail(requireNonBlank(email, "email"));
        String normalizedNewPassword = newPassword == null ? "" : newPassword.trim();

        User target = findUserById(normalizedUserId);
        ensureUsernameNEmailIsUnique(target.getUsername(), normalizedEmail, normalizedPhone, normalizedUserId);

        target.setPhone(normalizedPhone);
        target.setEmail(normalizedEmail);

        if (!normalizedNewPassword.isEmpty()) {
            validatePassword(normalizedNewPassword);
            try {
                target.setPassword(User.hashPassword(normalizedNewPassword));
            } catch (Exception e) {
                throw new ServiceException("Error hashing password: " + e.getMessage());
            }
        }

        userRepository.update(target);

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getUserId().equals(target.getUserId())) {
            currentUser.setPhone(normalizedPhone);
            currentUser.setEmail(normalizedEmail);
            if (!normalizedNewPassword.isEmpty()) {
                currentUser.setPassword(target.getPassword());
            }
        }

        return target;
    }

    public User updateStaff(String userId,
            String username,
            String phone,
            String email,
            String specialisation,
            boolean isAvailable) {

        String normalizedUserId = requireNonBlank(userId, "userId");
        String normalizedUsername = requireNonBlank(username, "username");
        String normalizedPhone = validatePhone(requireNonBlank(phone, "phone"));
        String normalizedEmail = validateEmail(requireNonBlank(email, "email"));

        User target = findUserById(normalizedUserId);
        if (target.getRole() == Role.CUSTOMER) {
            throw new ServiceException("updateStaff only supports staff roles");
        }

        ensureUsernameNEmailIsUnique(normalizedUsername, normalizedEmail, normalizedPhone, normalizedUserId);

        target.setUsername(normalizedUsername);
        target.setPhone(normalizedPhone);
        target.setEmail(normalizedEmail);

        if (target instanceof Technician technician) {
            technician.setSpecialization(requireNonBlank(specialisation, "specialisation"));
            technician.setIsAvailable(isAvailable);
        }

        userRepository.update(target);

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getUserId().equals(target.getUserId())) {
            currentUser.setUsername(normalizedUsername);
            currentUser.setPhone(normalizedPhone);
            currentUser.setEmail(normalizedEmail);
            if (currentUser instanceof Technician currentTechnician && target instanceof Technician updatedTechnician) {
                currentTechnician.setSpecialization(updatedTechnician.getSpecialization());
                currentTechnician.setIsAvailable(updatedTechnician.getIsAvailable());
            }
        }

        return target;
    }

    public void deleteUser(String userId) {
        String normalizedUserId = requireNonBlank(userId, "userId");

        User currentUser = sessionManager.getCurrentUser();
        if (currentUser.getUserId().equals(normalizedUserId)) {
            throw new ServiceException("You cannot delete your own account");
        }

        findUserById(normalizedUserId);

        if (hasActiveAppointments(normalizedUserId)) {
            throw new ServiceException("Cannot delete user with active appointments");
        }

        userRepository.delete(normalizedUserId);
    }

    private boolean hasActiveAppointments(String userId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        for (Appointment appointment : appointments) {
            if (!isLinkedToUser(appointment, userId)) {
                continue;
            }

            AppointmentStatus status = appointment.getStatus();
            if (status == AppointmentStatus.PENDING
                    || status == AppointmentStatus.CONFIRMED
                    || status == AppointmentStatus.IN_PROGRESS) {
                return true;
            }
        }
        return false;
    }

    public User resetSelectedUserPassword(String userId) {
        String normalizedUserId = requireNonBlank(userId, "userId");
        User target = findUserById(normalizedUserId);

        String defaultPassword = target.getUserId() + target.getRole();
        String hashedPassword;
        try {
            hashedPassword = User.hashPassword(defaultPassword);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
        target.setPassword(hashedPassword);
        userRepository.update(target);
        return target;
    }

    public List<User> getAllUsers() {
    return userRepository.findAll();
}

    private boolean isLinkedToUser(Appointment appointment, String userId) {
        return userId.equals(appointment.getCustomerId())
                || userId.equals(appointment.getCounterStaffId())
                || userId.equals(appointment.getTechnicianId());
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found: " + userId));
    }

    private void ensureUsernameNEmailIsUnique(String username, String email, String phone, String userIdToExclude) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            boolean sameUser = userIdToExclude != null && userIdToExclude.equals(user.getUserId());
            if (!sameUser && user.getUsername().equalsIgnoreCase(username)) {
                throw new ServiceException("Username already exists: " + username);
            }
            if (!sameUser && user.getEmail().equalsIgnoreCase(email)) {
                throw new ServiceException("Email already exists: " + email);
            }
            if (!sameUser && user.getPhone().equalsIgnoreCase(phone)) {
                throw new ServiceException("Phone number already exists: " + phone);
            }
        }
    }

    private String validateEmail(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[cC][oO][mM]$");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ServiceException("Invalid email format");
        }
        return email;
    }

    private String validatePhone(String phone) {
        Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ServiceException("Invalid phone format. Must be 10-11 digits.");
        }
        return phone;
    }

    private String validatePassword(String password) {
        if (password.length() < 6 || password.length() > 26) {
            throw new ServiceException("Password must be between 6 and 26 characters long");
        }
        return password;
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ServiceException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private List<String> extractUserIds(List<User> users) {
        List<String> ids = new ArrayList<>();
        for (User user : users) {
            ids.add(user.getUserId());
        }
        return ids;
    }

    public static String hashPassword(String password) throws Exception {
        try {
            // 1. Get an instance of the SHA-256 algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
        }
        return null;
    }

}

