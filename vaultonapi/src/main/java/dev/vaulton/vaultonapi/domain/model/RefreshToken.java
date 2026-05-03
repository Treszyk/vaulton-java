package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents a persistent session token used to issue new access tokens. Supports revocation and
 * rotation tracking for enhanced security.
 */
@AllArgsConstructor
@Getter
public class RefreshToken implements AutoCloseable {
  private final UUID id;
  @NonNull private final UUID userId;

  // Hash of the opaque refresh token
  @NonNull private final SecureBuffer tokenHash;

  @NonNull private final Instant createdAt;
  @NonNull private final Instant expiresAt;
  @Setter private Instant revokedAt;
  @Setter private RevocationReason revocationReason;

  // Hash of the active access token's Jti
  @NonNull private final SecureBuffer accessTokenJtiHash;

  public boolean isActive() {
    return revokedAt == null && expiresAt.isAfter(Instant.now()) && revocationReason == null;
  }

  public void revoke(RevocationReason reason) {
    if (revokedAt != null) throw new IllegalStateException("This token was already revoked");
    revocationReason = reason;
    revokedAt = Instant.now();
  }

  public void wipe() {
    tokenHash.wipe();
    accessTokenJtiHash.wipe();
  }

  @Override
  public void close() {
    wipe();
  }
}
