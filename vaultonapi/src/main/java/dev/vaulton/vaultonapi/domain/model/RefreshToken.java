package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a persistent session token used to issue new access tokens.
 * Supports revocation and rotation tracking for enhanced security.
 */
@AllArgsConstructor
@Getter @Setter
public class RefreshToken {
    private UUID id;
    private UUID userId;

    // Hash of the opaque refresh token
    private SecureBuffer tokenHash;

    private Instant createdAt;
    private Instant expiresAt;
    private Instant revokedAt;
    private RevocationReason revocationReason;

    // Hash of the active access token's Jti
    private SecureBuffer accessTokenJtiHash;

    private User user;

    public boolean isActive() {
        if (expiresAt == null) return false;
        return revokedAt == null && expiresAt.isAfter(Instant.now());
    }
}
