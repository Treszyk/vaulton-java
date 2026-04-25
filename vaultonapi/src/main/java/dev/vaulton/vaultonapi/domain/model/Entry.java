package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single encrypted item in the user's vault.
 */
@AllArgsConstructor
@Getter @Setter
public class Entry {
    private UUID id;
    private UUID userId;

    // Encrypted payload AES-GCM
    private EncryptedValue payload;

    // metadata
    private Instant createdAt;
    private Instant updatedAt;
}
