package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.JpaEncryptedValue;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("resource")
class CryptoMapperTest {
  private final CryptoMapper mapper = Mappers.getMapper(CryptoMapper.class);

  @Test
  void shouldMapByteArrayToSecureBuffer() {
    byte[] testBuffer = new byte[] {1, 2, 3};
    SecureBuffer mappedSecureBuffer = mapper.byteArrayToSecureBuffer(testBuffer);

    assertNotNull(mappedSecureBuffer);
    assertArrayEquals(mappedSecureBuffer.bytes(), testBuffer);

    testBuffer[0] = 99;

    assertNotEquals(mappedSecureBuffer.bytes()[0], testBuffer[0]);
  }

  @Test
  void shouldMapSecureBufferToByteArray() {
    SecureBuffer testBuffer = new SecureBuffer(new byte[] {1, 2, 3});
    byte[] mappedByteArray = mapper.secureBufferToByteArray(testBuffer);

    assertNotNull(mappedByteArray);
    assertArrayEquals(mappedByteArray, testBuffer.bytes());

    mappedByteArray[0] = 99;
    assertNotEquals(mappedByteArray[0], testBuffer.bytes()[0]);
  }

  @Test
  void shouldMapJpaEncryptedValueToDomain() {
    JpaEncryptedValue testJpaEncVal =
        new JpaEncryptedValue(new byte[12], new byte[] {1, 2, 3}, new byte[16]);

    EncryptedValue mappedEncVal = mapper.jpaEncryptedValueToDomain(testJpaEncVal);

    assertArrayEquals(testJpaEncVal.nonce(), mappedEncVal.nonce().bytes());
    assertArrayEquals(testJpaEncVal.tag(), mappedEncVal.tag().bytes());
    assertArrayEquals(testJpaEncVal.cipherText(), mappedEncVal.cipherText().bytes());

    testJpaEncVal.cipherText()[0] = 99;
    assertNotEquals(testJpaEncVal.cipherText()[0], mappedEncVal.cipherText().bytes()[0]);
  }

  @Test
  void shouldMapDomainEncryptedValueToJpa() {
    EncryptedValue testEncVal =
        new EncryptedValue(
            new SecureBuffer(new byte[12]),
            new SecureBuffer(new byte[] {1, 2, 3}),
            new SecureBuffer(new byte[16]));

    JpaEncryptedValue mappedJpaEncVal = mapper.domainEncryptedValueToJpa(testEncVal);

    assertArrayEquals(testEncVal.nonce().bytes(), mappedJpaEncVal.nonce());
    assertArrayEquals(testEncVal.tag().bytes(), mappedJpaEncVal.tag());
    assertArrayEquals(testEncVal.cipherText().bytes(), mappedJpaEncVal.cipherText());

    mappedJpaEncVal.cipherText()[0] = 99;
    assertNotEquals(mappedJpaEncVal.cipherText()[0], testEncVal.cipherText().bytes()[0]);
  }

  @Test
  void shouldHandleNulls() {
    assertNull(mapper.byteArrayToSecureBuffer(null));
    assertNull(mapper.secureBufferToByteArray(null));
    assertNull(mapper.jpaEncryptedValueToDomain(null));
    assertNull(mapper.domainEncryptedValueToJpa(null));
  }
}
