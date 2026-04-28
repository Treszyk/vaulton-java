package dev.vaulton.vaultonapi.domain.crypto;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedValueTest {
    private SecureBuffer getMockSecureBuffer(int length) {
        return new SecureBuffer(new String(new char[length]).replace('\0', 'a').getBytes(StandardCharsets.UTF_8)); // 4
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
        assertThrows(IllegalArgumentException.class, () -> {
            // 13 bytes Nonce instead of 12
            new EncryptedValue(mockNonce(false), mockCipherText(true), mockTag(true));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            // 4097 bytes CipherText instead of 4096
            new EncryptedValue(mockNonce(true), mockCipherText(false), mockTag(true));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            // 17 bytes Tag instead of 16
            new EncryptedValue(mockNonce(true), mockCipherText(true), mockTag(false));
        });
    }

    @Test
    void shouldCreateCorrectlyWhenValidCryptoSize() {
        assertDoesNotThrow(() -> {
            new EncryptedValue(mockNonce(true), mockCipherText(true), mockTag(true));
        });
    }
}