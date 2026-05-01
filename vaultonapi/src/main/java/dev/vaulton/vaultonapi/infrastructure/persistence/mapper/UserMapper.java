package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CryptoMapper.class)
public interface UserMapper {
  @Mapping(
      target = "kdfMode",
      expression = "java(entity.getKdfMode() == 1 ? KdfMode.DEFAULT : KdfMode.STRONG)")
  User toDomain(UserEntity entity);

  @Mapping(target = "kdfMode", expression = "java(domain.getKdfMode().getValue())")
  UserEntity toEntity(User domain);
}
