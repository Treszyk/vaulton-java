package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * The core domain entity representing a Vaulton user,
 * their authentication verifiers, and master key wraps.
 */
@AllArgsConstructor
@Getter @Setter
public class User {
    private UUID id;

    // Auth
    @NonNull private SecureBuffer verifier;
    @NonNull private SecureBuffer S_verifier; // I really want to keep this crypto salt naming...

    // Admin (Elevated actions)
    private SecureBuffer adminVerifier;
    private SecureBuffer S_adminVerifier;

    // Initial KDF salt
    @NonNull private SecureBuffer S_pwd;

    @NonNull private KdfMode kdfMode;

    // Master key wraps
    private EncryptedValue mkWrapPwd;
    private EncryptedValue mkWrapRk;

    // Recovery
    private SecureBuffer rkVerifier;
    private SecureBuffer S_rk;

    // Crypto schema version (always 1 in this prototype)
    @NonNull private Integer cryptoSchemaVer;

    // metadata
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    private Integer failedLoginCount;
    private Instant lastFailedLoginAt;
    private Instant lockedUntil;
}
