package src.backend.repository;

import java.util.List;
import java.util.Optional;

public interface InterfaceRepo<T> {
    void save(T entity);
    Optional<T> findById(String id);
    List<T> findAll();
    void update(T entity);
    void delete(String id);

}
