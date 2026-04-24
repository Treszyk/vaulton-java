package dev.vaulton.vaultonapi.domain.crypto;

/**
 * A Value Object representing an AES-GCM encrypted payload.
 */
public record EncryptedValue(
        byte[] nonce,
        byte[] cipherText,
        byte[] tag
) {}
