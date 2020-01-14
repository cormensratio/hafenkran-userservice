package de.unipassau.sep19.hafenkran.userservice.repository;

import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    @Override
    Optional<User> findById(@NonNull UUID uuid);

    Optional<User> findByName(String name);

    List<User> findByIdIn(List<UUID> ids);

    void deleteUserById(UUID id);

}
