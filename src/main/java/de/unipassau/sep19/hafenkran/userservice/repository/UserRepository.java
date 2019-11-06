package de.unipassau.sep19.hafenkran.userservice.repository;

import de.unipassau.sep19.hafenkran.userservice.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    @Override
    Optional<User> findById(UUID uuid);

    Optional<User> findByUsername(String username);
}
