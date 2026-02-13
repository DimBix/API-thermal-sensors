package it.univr.track.repository;

import it.univr.track.entity.UserRegistered;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserRegistered, Long> {
    Optional<UserRegistered> findByUsername(String username);

    Optional<UserRegistered> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
