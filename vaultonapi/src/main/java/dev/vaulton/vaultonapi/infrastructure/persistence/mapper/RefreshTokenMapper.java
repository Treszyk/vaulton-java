package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CryptoMapper.class)
public interface RefreshTokenMapper {
  RefreshToken toDomain(RefreshTokenEntity entity);

  RefreshTokenEntity toEntity(RefreshToken domain);
}
