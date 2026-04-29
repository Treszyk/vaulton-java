package dev.vaulton.vaultonapi.domain.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CryptoValidatorsTest {
  private SecureBuffer getMockSecureBuffer(int length) {
    return new SecureBuffer(
        new String(new char[length]).replace('\0', 'a').getBytes(StandardCharsets.UTF_8)); // 4
  }

  private SecureBuffer mockNonce() {
    return getMockSecureBuffer(CryptoConstants.GCM_NONCE_LEN);
  }

  private SecureBuffer mockEntryCiphertext(boolean valid) {
    return getMockSecureBuffer(valid ? CryptoConstants.MAX_ENTRY_CIPHERTEXT_BYTES : 0);
  }

  private SecureBuffer mockMasterKey(boolean valid) {
    return getMockSecureBuffer(CryptoConstants.MK_LEN + (valid ? 0 : 1));
  }

  private SecureBuffer mockTag() {
    return getMockSecureBuffer(CryptoConstants.GCM_TAG_LEN);
  }

  @Test
  void isValidMasterKeyReturnsTrueWhenValidMK() {
    assertTrue(
        () ->
            CryptoValidators.isValidMasterKey(
                new EncryptedValue(mockNonce(), mockMasterKey(true), mockTag())));
  }

  @Test
  void isValidMasterKeyReturnsFalseWhenInvalidMK() {
    assertFalse(
        () ->
            CryptoValidators.isValidMasterKey(
                // 33 byte Master Key instead of 32
                new EncryptedValue(mockNonce(), mockMasterKey(false), mockTag())));
  }

  @Test
  void isValidVaultEntryReturnsTrueWhenValidEntry() {
    assertTrue(
        () ->
            CryptoValidators.isValidVaultEntry(
                new EncryptedValue(mockNonce(), mockEntryCiphertext(true), mockTag())));
  }

  @Test
  void isValidVaultEntryReturnsFalseWhenInvalidEntry() {
    assertFalse(
        () ->
            CryptoValidators.isValidVaultEntry(
                // 0 length ciphertext
                new EncryptedValue(mockNonce(), mockEntryCiphertext(false), mockTag())));
  }
}
