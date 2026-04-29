package dev.vaulton.vaultonapi.domain.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class EncryptedValueTest {
  private SecureBuffer getMockSecureBuffer(int length) {
    return new SecureBuffer(
        new String(new char[length]).replace('\0', 'a').getBytes(StandardCharsets.UTF_8)); // 4
  }

  private SecureBuffer mockNonce(boolean valid) {
    return getMockSecureBuffer(CryptoConstants.GCM_NONCE_LEN + (valid ? 0 : 1));
  }

  private SecureBuffer mockCipherText(boolean valid) {
    return getMockSecureBuffer(CryptoConstants.MAX_ENTRY_CIPHERTEXT_BYTES + (valid ? 0 : 1));
  }

  private SecureBuffer mockTag(boolean valid) {
    return getMockSecureBuffer(CryptoConstants.GCM_TAG_LEN + (valid ? 0 : 1));
  }

  @Test
  void shouldThrowWhenInvalidCryptoSize() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // 13 bytes Nonce instead of 12
          new EncryptedValue(mockNonce(false), mockCipherText(true), mockTag(true));
        });
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // 4097 bytes CipherText instead of 4096
          new EncryptedValue(mockNonce(true), mockCipherText(false), mockTag(true));
        });
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // 17 bytes Tag instead of 16
          new EncryptedValue(mockNonce(true), mockCipherText(true), mockTag(false));
        });
  }

  @Test
  void shouldCreateCorrectlyWhenValidCryptoSize() {
    assertDoesNotThrow(
        () -> {
          new EncryptedValue(mockNonce(true), mockCipherText(true), mockTag(true));
        });
  }

  @Test
  void shouldWipeInternalBuffersOnWipe() {
    SecureBuffer nonce = mockNonce(true);
    SecureBuffer ciphertext = mockCipherText(true);
    SecureBuffer tag = mockTag(true);

    EncryptedValue encryptedValue = new EncryptedValue(nonce, ciphertext, tag);
    encryptedValue.wipe();

    assertThrows(IllegalStateException.class, () -> encryptedValue.nonce().bytes());
    assertThrows(IllegalStateException.class, () -> encryptedValue.cipherText().bytes());
    assertThrows(IllegalStateException.class, () -> encryptedValue.tag().bytes());
    assertThrows(IllegalStateException.class, nonce::bytes);
    assertThrows(IllegalStateException.class, ciphertext::bytes);
    assertThrows(IllegalStateException.class, tag::bytes);
  }

  @Test
  void shouldWipeInternalBuffersOnClose() {
    SecureBuffer nonce = mockNonce(true);
    SecureBuffer ciphertext = mockCipherText(true);
    SecureBuffer tag = mockTag(true);

    EncryptedValue encryptedValue = new EncryptedValue(nonce, ciphertext, tag);

    //noinspection EmptyTryBlock
    try (encryptedValue) {
      // Simulated use as a resource
    }

    assertThrows(IllegalStateException.class, () -> encryptedValue.nonce().bytes());
    assertThrows(IllegalStateException.class, () -> encryptedValue.cipherText().bytes());
    assertThrows(IllegalStateException.class, () -> encryptedValue.tag().bytes());
    assertThrows(IllegalStateException.class, nonce::bytes);
    assertThrows(IllegalStateException.class, ciphertext::bytes);
    assertThrows(IllegalStateException.class, tag::bytes);
  }
}
