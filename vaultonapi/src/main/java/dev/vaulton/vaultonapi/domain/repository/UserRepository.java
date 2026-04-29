package dev.vaulton.vaultonapi.domain.repository;

import dev.vaulton.vaultonapi.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findById(UUID id);

  boolean existsById(UUID id);

  User save(User user);

  boolean deleteById(UUID id);
}
