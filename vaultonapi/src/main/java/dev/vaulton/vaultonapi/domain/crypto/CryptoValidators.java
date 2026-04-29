package dev.vaulton.vaultonapi.domain.crypto;

/**
 * Provides domain-level validation policies for encrypted data. These methods ensure that
 * cryptographic payloads meet context-specific requirements (e.g., exact lengths for master keys).
 */
public final class CryptoValidators {
  private CryptoValidators() {}

  public static boolean isValidMasterKey(EncryptedValue v) {
    return v != null && v.cipherText().length() == CryptoConstants.MK_LEN;
  }

  public static boolean isValidVaultEntry(EncryptedValue v) {
    if (v == null) return false;
    return v.cipherText().length() >= 1
        && v.cipherText().length() <= CryptoConstants.MAX_ENTRY_CIPHERTEXT_BYTES;
  }
}
