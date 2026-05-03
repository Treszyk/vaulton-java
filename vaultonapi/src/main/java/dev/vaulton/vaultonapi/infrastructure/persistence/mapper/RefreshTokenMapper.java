package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CryptoMapper.class)
public interface RefreshTokenMapper {
  @org.mapstruct.Mapping(
      target = "revocationReason",
      expression =
          "java(entity.getRevocationReason() != null ? dev.vaulton.vaultonapi.domain.enums.RevocationReason.values()[entity.getRevocationReason()] : null)")
  RefreshToken toDomain(RefreshTokenEntity entity);

  @org.mapstruct.Mapping(
      target = "revocationReason",
      expression =
          "java(domain.getRevocationReason() != null ? domain.getRevocationReason().ordinal() : null)")
  RefreshTokenEntity toEntity(RefreshToken domain);
}
