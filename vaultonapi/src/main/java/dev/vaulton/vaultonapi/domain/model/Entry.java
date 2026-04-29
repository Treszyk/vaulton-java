package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Represents a single encrypted item in the user's vault. */
@AllArgsConstructor
@Getter
@Setter
public class Entry implements AutoCloseable {
  private UUID id;
  @NonNull private UUID userId;

  // Encrypted payload AES-GCM
  @NonNull private EncryptedValue payload;

  // metadata
  private Instant createdAt;
  private Instant updatedAt;

  public void wipe() {
    payload.wipe();
  }

  @Override
  public void close() {
    wipe();
  }
}
