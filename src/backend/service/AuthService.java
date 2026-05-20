package src.backend.service;

import src.backend.models.User;
import src.backend.repository.UserRepository;
import src.backend.util.SessionManager;

public class AuthService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public AuthService(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    public User login(String email, String password) {
        // Prefer repository lookup to avoid loading all users
        User matched = userRepository.findOne(u -> u.getEmail().equalsIgnoreCase(email)).orElse(null);

        if (matched == null) {
            throw new ServiceException("Invalid email");
        }

        if (verifyPassword(password, matched.getPassword())) {
            sessionManager.setCurrentUser(matched);
            return matched;
        }

        throw new ServiceException("Invalid password");
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean verifyPassword(String inputPlainPassword, String storedHashPassword) {
        try {
            String inputHash = User.hashPassword(inputPlainPassword);
            return inputHash.equals(storedHashPassword);
        } catch (Exception e) {
            return false;
        }
    }

}

