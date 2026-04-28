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
public class User implements AutoCloseable {
    private UUID id;

    // Auth
    @NonNull private SecureBuffer verifier;
    @NonNull private SecureBuffer S_verifier; // I really want to keep this crypto salt naming...

    // Admin (Elevated actions)
    @NonNull private SecureBuffer adminVerifier;
    @NonNull private SecureBuffer S_adminVerifier;

    // Initial KDF salt
    @NonNull private SecureBuffer S_pwd;

    @NonNull private KdfMode kdfMode;

    // Master key wraps
    @NonNull private EncryptedValue mkWrapPwd;
    @NonNull private EncryptedValue mkWrapRk;

    // Recovery
    @NonNull private SecureBuffer rkVerifier;
    @NonNull private SecureBuffer S_rk;

    // Crypto schema version (always 1 in this prototype)
    @NonNull private Integer cryptoSchemaVer;

    // metadata
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    private Integer failedLoginCount;
    private Instant lastFailedLoginAt;
    private Instant lockedUntil;

    public void wipe() {
        verifier.wipe(); S_verifier.wipe();
        adminVerifier.wipe(); S_adminVerifier.wipe();
        S_pwd.wipe();
        mkWrapPwd.wipe(); mkWrapRk.wipe();
        rkVerifier.wipe(); S_rk.wipe();
    }

    @Override
    public void close() { wipe(); }
}
