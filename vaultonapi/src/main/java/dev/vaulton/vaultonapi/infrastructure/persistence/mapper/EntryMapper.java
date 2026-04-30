package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.Entry;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.EntryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CryptoMapper.class)
public interface EntryMapper {
  Entry toDomain(EntryEntity entity);

  EntryEntity toEntity(Entry domain);
}
