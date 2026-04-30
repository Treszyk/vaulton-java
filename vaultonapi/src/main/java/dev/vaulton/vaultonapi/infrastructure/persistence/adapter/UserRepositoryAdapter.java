package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.UserMapper;
import dev.vaulton.vaultonapi.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
  private final UserJpaRepository jpaRepository;
  private final UserMapper mapper;

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public User save(User user) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(user)));
  }

  @Override
  public boolean deleteById(UUID id) {
    return jpaRepository.removeById(id) > 0;
  }
}
