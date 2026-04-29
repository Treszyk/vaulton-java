package dev.vaulton.vaultonapi.domain.repository;

import dev.vaulton.vaultonapi.domain.model.Entry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryRepository {
  Optional<Entry> findByIdAndUserId(UUID entryId, UUID userId);

  List<Entry> findByUserId(UUID userId);

  Entry save(Entry entry);

  boolean deleteByIdAndUserId(UUID entryId, UUID userId);
}
