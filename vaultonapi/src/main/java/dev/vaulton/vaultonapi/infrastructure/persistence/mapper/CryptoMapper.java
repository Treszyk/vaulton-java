package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.JpaEncryptedValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CryptoMapper {
  default SecureBuffer byteArrayToSecureBuffer(byte[] value) {
    return value == null ? null : new SecureBuffer(value);
  }

  default byte[] secureBufferToByteArray(SecureBuffer value) {
    return value == null ? null : value.bytes();
  }

  EncryptedValue jpaEncryptedValueToDomain(JpaEncryptedValue value);

  JpaEncryptedValue domainEncryptedValueToJpa(EncryptedValue value);
}
