package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * The core domain entity representing a Vaulton user, their authentication verifiers, and master
 * key wraps.
 */
@AllArgsConstructor
@Getter
@Setter
public class User implements AutoCloseable {
  private UUID id;

  // Auth
  @NonNull private SecureBuffer verifier;
  @NonNull private SecureBuffer saltVerifier;

  // Admin (Elevated actions)
  @NonNull private SecureBuffer adminVerifier;
  @NonNull private SecureBuffer saltAdminVerifier;

  // Initial KDF salt
  @NonNull private SecureBuffer saltPwd;

  @NonNull private KdfMode kdfMode;

  // Master key wraps
  @NonNull private EncryptedValue mkWrapPwd;
  @NonNull private EncryptedValue mkWrapRk;

  // Recovery
  @NonNull private SecureBuffer rkVerifier;
  @NonNull private SecureBuffer saltRk;

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
    verifier.wipe();
    saltVerifier.wipe();
    adminVerifier.wipe();
    saltAdminVerifier.wipe();
    saltPwd.wipe();
    mkWrapPwd.wipe();
    mkWrapRk.wipe();
    rkVerifier.wipe();
    saltRk.wipe();
  }

  @Override
  public void close() {
    wipe();
  }
}
