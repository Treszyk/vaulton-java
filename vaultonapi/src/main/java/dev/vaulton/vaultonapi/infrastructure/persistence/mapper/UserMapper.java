package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = CryptoMapper.class,
    imports = {dev.vaulton.vaultonapi.domain.enums.KdfMode.class})
public interface UserMapper {
  @Mapping(target = "kdfMode", expression = "java(KdfMode.fromValue(entity.getKdfMode()))")
  User toDomain(UserEntity entity);

  @Mapping(target = "kdfMode", expression = "java(domain.getKdfMode().getValue())")
  UserEntity toEntity(User domain);
}
