package dev.vaulton.vaultonapi.infrastructure.persistence.repository;

import dev.vaulton.vaultonapi.infrastructure.persistence.entity.EntryEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface EntryJpaRepository extends Repository<EntryEntity, UUID> {
  Optional<EntryEntity> findByIdAndUserId(UUID entryId, UUID userId);

  List<EntryEntity> findByUserId(UUID userId);

  EntryEntity save(EntryEntity entry);

  @Transactional
  long deleteByIdAndUserId(UUID entryId, UUID userId);
}
