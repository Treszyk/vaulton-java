package dev.vaulton.vaultonapi.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource")
class EntryTest {

  private SecureBuffer mockBuffer(int size) {
    return new SecureBuffer(new byte[size]);
  }

  private EncryptedValue mockPayload() {
    // Nonce 12 bytes, CipherText 16 bytes, Tag 16 bytes
    return new EncryptedValue(mockBuffer(12), mockBuffer(16), mockBuffer(16));
  }

  @Test
  void constructorShouldThrowWhenCriticalFieldsAreNull() {
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    EncryptedValue payload = mockPayload();

    // Missing UserID
    assertThrows(NullPointerException.class, () -> new Entry(id, null, payload, null, null));

    // Missing Payload
    assertThrows(NullPointerException.class, () -> new Entry(id, userId, null, null, null));
  }

  @Test
  void constructorShouldCreateEntryWhenCriticalFieldsArePresent() {
    assertDoesNotThrow(
        () -> new Entry(UUID.randomUUID(), UUID.randomUUID(), mockPayload(), null, null));
  }

  @Test
  void shouldWipeInternalBuffersOnWipe() {
    EncryptedValue payload = mockPayload();

    Entry entry = new Entry(UUID.randomUUID(), UUID.randomUUID(), payload, null, null);
    entry.wipe();

    assertThrows(IllegalStateException.class, () -> entry.getPayload().cipherText().bytes());
    assertThrows(IllegalStateException.class, () -> payload.cipherText().bytes());
  }

  @Test
  void shouldWipeInternalBuffersOnClose() {
    EncryptedValue payload = mockPayload();

    Entry entry = new Entry(UUID.randomUUID(), UUID.randomUUID(), payload, null, null);

    //noinspection EmptyTryBlock
    try (entry) {
      // Simulated use as a resource
    }

    assertThrows(IllegalStateException.class, () -> entry.getPayload().cipherText().bytes());
    assertThrows(IllegalStateException.class, () -> payload.cipherText().bytes());
  }
}
