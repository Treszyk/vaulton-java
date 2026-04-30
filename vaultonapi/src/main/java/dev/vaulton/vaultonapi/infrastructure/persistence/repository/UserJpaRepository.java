package dev.vaulton.vaultonapi.infrastructure.persistence.repository;

import dev.vaulton.vaultonapi.infrastructure.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  @Transactional
  long removeById(UUID id);
}
