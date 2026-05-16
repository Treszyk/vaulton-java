package dev.vaulton.vaultonapi.domain.crypto;

import dev.vaulton.vaultonapi.domain.exception.VaultonDomainException;

/** A Value Object representing an AES-GCM encrypted payload. */
public record EncryptedValue(SecureBuffer nonce, SecureBuffer cipherText, SecureBuffer tag)
    implements AutoCloseable {
  public EncryptedValue {
    if (nonce == null || nonce.length() != CryptoConstants.GCM_NONCE_LEN) {
      throw new VaultonDomainException("Invalid nonce length", "Invalid crypto blob sizes.");
    }
    if (tag == null || tag.length() != CryptoConstants.GCM_TAG_LEN) {
      throw new VaultonDomainException("Invalid tag length", "Invalid crypto blob sizes.");
    }
    if (cipherText == null || cipherText.length() > CryptoConstants.MAX_ENTRY_CIPHERTEXT_BYTES) {
      throw new VaultonDomainException("Invalid payload size", "Invalid crypto blob sizes.");
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
