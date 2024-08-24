package repository.repositoryInterface;

import java.util.ArrayList;
import java.util.UUID;

public interface ICRUDRepository<T> {

    void create(T t);

    ArrayList<T> getAll();

    T getById(UUID id);

    void updateName(UUID id, String name);

    void delete(UUID id);
}
