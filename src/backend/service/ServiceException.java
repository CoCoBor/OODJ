package src.backend.service;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
