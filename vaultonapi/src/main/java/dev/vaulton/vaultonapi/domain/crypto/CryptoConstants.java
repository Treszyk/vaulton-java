package dev.vaulton.vaultonapi.domain.crypto;

/**
 * Global constants defining the cryptographic specifications for the system.
 * These values ensure consistency between the Java backend and the Angular client,
 * specifically regarding key lengths, salts, and AES-GCM parameters.
 */
public final class CryptoConstants {
    private CryptoConstants() {}
    public static final int VERIFIER_LEN = 32;
    public static final int MK_LEN = 32;
    public static final int SALT_LEN = 16;
    public static final int GCM_NONCE_LEN = 12;
    public static final int GCM_TAG_LEN = 16;
    public static final int PEPPER_LEN = 32;
    public static final int MAX_ENTRY_CIPHERTEXT_BYTES = 4096;
}
