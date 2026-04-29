package dev.vaulton.vaultonapi.domain.repository;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
  Optional<RefreshToken> findByTokenHash(SecureBuffer tokenHash);

  List<RefreshToken> findAllByUserId(UUID userId);

  RefreshToken save(RefreshToken refreshToken);

  boolean revokeAllByUserId(UUID userId, Instant revokedAt, RevocationReason reason);
}
