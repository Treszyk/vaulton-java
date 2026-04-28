package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a persistent session token used to issue new access tokens.
 * Supports revocation and rotation tracking for enhanced security.
 */
@AllArgsConstructor
@Getter @Setter
public class RefreshToken implements AutoCloseable {
    private UUID id;
    @NonNull private UUID userId;

    // Hash of the opaque refresh token
    @NonNull private SecureBuffer tokenHash;

    @NonNull private Instant createdAt;
    @NonNull private Instant expiresAt;
    private Instant revokedAt;
    private RevocationReason revocationReason;

    // Hash of the active access token's Jti
    @NonNull private SecureBuffer accessTokenJtiHash;

    @NonNull private User user;

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
    public void close() { wipe(); }
}
