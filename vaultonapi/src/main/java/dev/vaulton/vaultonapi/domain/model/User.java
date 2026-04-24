package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The core domain entity representing a Vaulton user,
 * their authentication verifiers, and master key wraps.
 */
@AllArgsConstructor
@Getter @Setter
public class User {
    UUID id;

    // Auth
    private byte[] verifier;
    private byte[] S_verifier; // I realy want to keep this crypto salt naming...

    // Admin (Elevated actions)
    private byte[] adminVerifier;
    private byte[] S_adminVerifier;

    // Initial KDF salt
    private byte[] S_pwd;

    private KdfMode kdfMode;

    // Master key wraps
    private EncryptedValue mkWrapPwd;
    private EncryptedValue mkWrapRk;

    // Recovery
    private byte[] rkVerifier;
    private byte[] S_rk;

    // Crypto schema version (always 1 in this prototype)
    private Integer cryptoSchemaVer;

    // metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private Integer failedLoginCount;
    private LocalDateTime lastFailedLoginAt;
    private LocalDateTime lockedUntil;
}
