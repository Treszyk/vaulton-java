package dev.vaulton.vaultonapi.infrastructure.crypto;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

@RequiredArgsConstructor
public class BouncyCryptoAdapter implements CryptoService {
  private final SecureRandom randGen;
  private final byte[] fakeSaltSecret;

  void zeroizeBuffer(byte[] buffer) {
    if (buffer != null) java.util.Arrays.fill(buffer, (byte) 0x00);
  }

  @Override
  public SecureBuffer computeStoredVerifier(
      SecureBuffer verifierRaw, SecureBuffer salt, int iterations, byte[] pepper) {
    PKCS5S2ParametersGenerator gen = null;
    byte[] verifierBytes = null;
    byte[] saltBytes = null;
    byte[] pepperedVerifier = null;
    byte[] hash = null;

    try {
      gen = new PKCS5S2ParametersGenerator(new SHA256Digest());

      verifierBytes = verifierRaw.bytes();
      saltBytes = salt.bytes();
      pepperedVerifier = new byte[verifierBytes.length + pepper.length];
      System.arraycopy(verifierBytes, 0, pepperedVerifier, 0, verifierBytes.length);
      System.arraycopy(pepper, 0, pepperedVerifier, verifierBytes.length, pepper.length);

      gen.init(pepperedVerifier, saltBytes, iterations);
      KeyParameter keyParameter = (KeyParameter) gen.generateDerivedParameters(256);
      hash = keyParameter.getKey();

      return new SecureBuffer(hash);
    } finally {
      zeroizeBuffer(pepperedVerifier);
      zeroizeBuffer(verifierBytes);
      zeroizeBuffer(saltBytes);
      zeroizeBuffer(hash);
    }
  }

  @Override
  public SecureBuffer generateRandomBytes(int length) {
    byte[] randBytes = null;
    try {
      randBytes = new byte[length];
      randGen.nextBytes(randBytes);

      return new SecureBuffer(randBytes);
    } finally {
      zeroizeBuffer(randBytes);
    }
  }

  @Override
  public SecureBuffer computeFakeSalt(UUID accountId) {
    byte[] idBytes = null;
    byte[] contextBytes = null;
    byte[] combined = null;
    byte[] hmacResult = null;
    byte[] truncatedResult = null;

    ByteBuffer idBuffer = null;
    Mac mac = null;
    SecretKeySpec spec = new SecretKeySpec(fakeSaltSecret, "HmacSHA256");

    try {
      contextBytes = "Vaulton.FakeSalt.v1".getBytes(StandardCharsets.UTF_8);

      idBuffer = ByteBuffer.allocate(CryptoConstants.SALT_LEN);
      idBuffer.putLong(accountId.getMostSignificantBits());
      idBuffer.putLong(accountId.getLeastSignificantBits());
      idBytes = idBuffer.array();

      combined = new byte[idBytes.length + contextBytes.length];
      System.arraycopy(contextBytes, 0, combined, 0, contextBytes.length);
      System.arraycopy(idBytes, 0, combined, contextBytes.length, idBytes.length);

      mac = Mac.getInstance("HmacSHA256");
      mac.init(spec);
      hmacResult = mac.doFinal(combined);

      truncatedResult = new byte[CryptoConstants.SALT_LEN];
      System.arraycopy(hmacResult, 0, truncatedResult, 0, truncatedResult.length);

      return new SecureBuffer(truncatedResult);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } finally {
      zeroizeBuffer(idBytes);
      if (idBuffer != null) idBuffer.clear();
      if (mac != null) mac.reset();
      zeroizeBuffer(contextBytes);
      zeroizeBuffer(combined);
      zeroizeBuffer(hmacResult);
      zeroizeBuffer(truncatedResult);
    }
  }
}
