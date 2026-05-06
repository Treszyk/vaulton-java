package dev.vaulton.vaultonapi.infrastructure.crypto;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import java.security.SecureRandom;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource")
class JceCryptoAdapterTest {

  private JceCryptoAdapter adapter;
  private final byte[] fakeSaltSecret = new byte[32];

  @BeforeEach
  void setUp() {
    adapter = new JceCryptoAdapter(new SecureRandom(), fakeSaltSecret);
  }

  @Test
  void shouldGenerateRandomBytesOfRequestedLength() {

    SecureBuffer testBuffer = adapter.generateRandomBytes(32);
    SecureBuffer secondTestBuffer = adapter.generateRandomBytes(32);

    assertEquals(32, testBuffer.length());
    assertEquals(32, secondTestBuffer.length());

    assertNotEquals(testBuffer, secondTestBuffer);
  }

  @Test
  void shouldComputeConsistentVerifier() {
    SecureBuffer verifierRaw = adapter.generateRandomBytes(CryptoConstants.VERIFIER_LEN);
    SecureBuffer salt = adapter.generateRandomBytes(CryptoConstants.SALT_LEN);
    int iterations = 600_000;
    byte[] pepper = adapter.generateRandomBytes(CryptoConstants.PEPPER_LEN).bytes();

    SecureBuffer firstCompute =
        adapter.computeStoredVerifier(verifierRaw, salt, iterations, pepper);
    SecureBuffer secondCompute =
        adapter.computeStoredVerifier(verifierRaw, salt, iterations, pepper);
    SecureBuffer diffPepperCompute =
        adapter.computeStoredVerifier(
            verifierRaw,
            salt,
            iterations,
            adapter.generateRandomBytes(CryptoConstants.PEPPER_LEN).bytes());

    assertEquals(CryptoConstants.VERIFIER_LEN, firstCompute.length());
    assertEquals(CryptoConstants.VERIFIER_LEN, secondCompute.length());
    assertEquals(CryptoConstants.VERIFIER_LEN, diffPepperCompute.length());
    assertEquals(firstCompute, secondCompute);

    assertNotEquals(firstCompute, diffPepperCompute);
  }

  @Test
  void shouldComputeDeterministicFakeSalt() {
    UUID firstUUID = UUID.randomUUID();
    UUID secondUUID = UUID.randomUUID();

    SecureBuffer firstSalt = adapter.computeFakeSalt(firstUUID);
    SecureBuffer secondSalt = adapter.computeFakeSalt(firstUUID);
    SecureBuffer diffIdSalt = adapter.computeFakeSalt(secondUUID);

    assertEquals(CryptoConstants.SALT_LEN, firstSalt.length());
    assertEquals(CryptoConstants.SALT_LEN, secondSalt.length());
    assertEquals(CryptoConstants.SALT_LEN, diffIdSalt.length());

    assertEquals(firstSalt, secondSalt);
    assertNotEquals(firstSalt, diffIdSalt);
  }
}
