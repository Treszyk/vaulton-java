package dev.vaulton.vaultonapi.domain.crypto;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedValueTest {
    @Test
    void shouldThrowWhenInvalidCryptoSize() {
        assertThrows(IllegalArgumentException.class, () -> {
                new EncryptedValue(
                        new SecureBuffer("ąąąąąąą".getBytes(StandardCharsets.UTF_8)), // 14 bytes instead of 12
                        new SecureBuffer("cipherText".getBytes(StandardCharsets.UTF_8)),
                        new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
                );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new EncryptedValue(
                    new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                    new SecureBuffer("cipherText".getBytes(StandardCharsets.UTF_8)),
                    new SecureBuffer("ąąąąąąąąą".getBytes(StandardCharsets.UTF_8)) // 18 bytes instead of 16
            );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new EncryptedValue(
                    new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                    new SecureBuffer(new String(new char[2049]).replace('\0', 'ą').getBytes(StandardCharsets.UTF_8)), // 4098 bytes instead of 4096
                    new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
            );
        });
    }

    @Test
    void shouldCreateCorrectlyWhenValidCryptoSize() {
        assertDoesNotThrow(() -> {
            new EncryptedValue(
                    new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)), // 12 bytes
                    new SecureBuffer(new String(new char[2048]).replace('\0', 'ą').getBytes(StandardCharsets.UTF_8)), // 4096 byes
                    new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8)) // 16 bytes
            );
        });
    }
}