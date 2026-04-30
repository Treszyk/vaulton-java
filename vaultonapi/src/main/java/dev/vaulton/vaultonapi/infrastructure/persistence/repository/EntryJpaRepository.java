package dev.vaulton.vaultonapi.infrastructure.persistence.repository;

import dev.vaulton.vaultonapi.infrastructure.persistence.entity.EntryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryJpaRepository extends JpaRepository<EntryEntity, UUID> {
  Optional<EntryEntity> findByIdAndUserId(UUID entryId, UUID userId);

  List<EntryEntity> findByUserId(UUID userId);

  boolean deleteByIdAndUserId(UUID entryId, UUID userId);
}
