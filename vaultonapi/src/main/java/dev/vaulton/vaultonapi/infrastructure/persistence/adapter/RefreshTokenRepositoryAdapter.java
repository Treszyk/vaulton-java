package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import dev.vaulton.vaultonapi.domain.repository.RefreshTokenRepository;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.RefreshTokenMapper;
import dev.vaulton.vaultonapi.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
  private final RefreshTokenJpaRepository jpaRepository;
  private final RefreshTokenMapper mapper;

  @Override
  public Optional<RefreshToken> findByTokenHash(SecureBuffer tokenHash) {
    return jpaRepository.findByTokenHash(tokenHash.bytes()).map(mapper::toDomain);
  }

  @Override
  public List<RefreshToken> findAllByUserId(UUID userId) {
    return jpaRepository.findAllByUserId(userId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(refreshToken)));
  }

  @Override
  public boolean revokeAllByUserId(UUID userId, Instant revokedAt, RevocationReason reason) {
    return jpaRepository.revokeAllByUserId(userId, revokedAt, reason) > 0;
  }
}
