package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import dev.vaulton.vaultonapi.domain.model.Entry;
import dev.vaulton.vaultonapi.domain.repository.EntryRepository;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.EntryMapper;
import dev.vaulton.vaultonapi.infrastructure.persistence.repository.EntryJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EntryRepositoryAdapter implements EntryRepository {
  private final EntryJpaRepository jpaRepository;
  private final EntryMapper mapper;

  @Override
  public Optional<Entry> findByIdAndUserId(UUID entryId, UUID userId) {
    return jpaRepository.findByIdAndUserId(entryId, userId).map(mapper::toDomain);
  }

  @Override
  public List<Entry> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public Entry save(Entry entry) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(entry)));
  }

  @Override
  public boolean deleteByIdAndUserId(UUID entryId, UUID userId) {
    return jpaRepository.deleteByIdAndUserId(entryId, userId);
  }
}
