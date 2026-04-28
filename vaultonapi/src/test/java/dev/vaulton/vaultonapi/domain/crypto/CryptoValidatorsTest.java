package dev.vaulton.vaultonapi.domain.crypto;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CryptoValidatorsTest {
    @Test
    void isValidMasterKeyReturnsTrueWhenValidMK() {
        assertTrue(() -> CryptoValidators.isValidMasterKey(new EncryptedValue(
                new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                new SecureBuffer(new String(new char[CryptoConstants.MK_LEN]).replace('\0', 'a').getBytes(StandardCharsets.UTF_8)), // 32 bytes
                new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
        )));
    }

    @Test
    void isValidMasterKeyReturnsFalseWhenInvalidMK() {
        assertFalse(() -> CryptoValidators.isValidMasterKey(new EncryptedValue(
                new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                new SecureBuffer(new String(new char[CryptoConstants.MK_LEN + 1]).replace('\0', 'a').getBytes(StandardCharsets.UTF_8)), // 33 bytes
                new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
        )));
    }

    @Test
    void isValidVaultEntryReturnsTrueWhenValidEntry() {
        assertTrue(() -> CryptoValidators.isValidVaultEntry(new EncryptedValue(
                new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                new SecureBuffer("validCiphertext".getBytes(StandardCharsets.UTF_8)),
                new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
        )));
    }

    @Test
    void isValidVaultEntryReturnsFalseWhenInvalidEntry() {
        assertFalse(() -> CryptoValidators.isValidVaultEntry(new EncryptedValue(
                new SecureBuffer("ąąąąąą".getBytes(StandardCharsets.UTF_8)),
                new SecureBuffer(new byte[0]),
                new SecureBuffer("ąąąąąąąą".getBytes(StandardCharsets.UTF_8))
        )));
    }
}