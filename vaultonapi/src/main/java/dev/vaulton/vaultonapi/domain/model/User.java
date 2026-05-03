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
public class User implements AutoCloseable {
  private final UUID id;

  // Auth
  @Setter @NonNull private SecureBuffer verifier;
  @Setter @NonNull private SecureBuffer saltVerifier;

  // Admin (Elevated actions)
  @Setter @NonNull private SecureBuffer adminVerifier;
  @Setter @NonNull private SecureBuffer saltAdminVerifier;

  // Initial KDF salt
  @Setter @NonNull private SecureBuffer saltPwd;

  @Setter @NonNull private KdfMode kdfMode;

  // Master key wraps
  @Setter @NonNull private EncryptedValue mkWrapPwd;
  @Setter @NonNull private EncryptedValue mkWrapRk;

  // Recovery
  @Setter @NonNull private SecureBuffer rkVerifier;
  @Setter @NonNull private SecureBuffer saltRk;

  // Crypto schema version (always 1 in this prototype)
  @NonNull private Integer cryptoSchemaVer;

  // metadata
  private final Instant createdAt;
  @Setter private Instant updatedAt;
  @Setter private Instant lastLoginAt;
  @Setter private Integer failedLoginCount;
  @Setter private Instant lastFailedLoginAt;
  @Setter private Instant lockedUntil;

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
