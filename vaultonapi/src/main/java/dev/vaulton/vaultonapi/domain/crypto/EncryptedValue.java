package dev.vaulton.vaultonapi.domain.crypto;

/** A Value Object representing an AES-GCM encrypted payload. */
public record EncryptedValue(SecureBuffer nonce, SecureBuffer cipherText, SecureBuffer tag)
    implements AutoCloseable {
  public EncryptedValue {
    if (nonce == null || nonce.length() != CryptoConstants.GCM_NONCE_LEN) {
      throw new IllegalArgumentException("Invalid nonce length");
    }
    if (tag == null || tag.length() != CryptoConstants.GCM_TAG_LEN) {
      throw new IllegalArgumentException("Invalid tag length");
    }
    if (cipherText == null || cipherText.length() > CryptoConstants.MAX_ENTRY_CIPHERTEXT_BYTES) {
      throw new IllegalArgumentException("Invalid payload size");
    }
  }

  public void wipe() {
    nonce.wipe();
    cipherText.wipe();
    tag.wipe();
  }

  @Override
  public void close() {
    wipe();
  }
}
