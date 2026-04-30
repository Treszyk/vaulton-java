package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CryptoMapper.class)
public interface UserMapper {
  User toDomain(UserEntity entity);

  UserEntity toEntity(User domain);
}
