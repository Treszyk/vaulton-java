package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.model.Entry;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.EntryEntity;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.JpaEncryptedValue;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("resource")
class EntryMapperTest {
  private final EntryMapper mapper = Mappers.getMapper(EntryMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mapper, "cryptoMapper", Mappers.getMapper(CryptoMapper.class));
  }

  @Test
  void shouldMapEntryEntityToDomain() {
    EntryEntity testEntity =
        new EntryEntity(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new JpaEncryptedValue(new byte[12], new byte[] {1, 2, 3}, new byte[16]),
            Instant.now(),
            Instant.now());

    Entry mappedEntry = mapper.toDomain(testEntity);

    assertNotNull(mappedEntry);
    assertAll(
        () -> assertEquals(testEntity.getId(), mappedEntry.getId()),
        () -> assertEquals(testEntity.getUserId(), mappedEntry.getUserId()),
        () -> assertEquals(testEntity.getCreatedAt(), mappedEntry.getCreatedAt()),
        () -> assertEquals(testEntity.getUpdatedAt(), mappedEntry.getUpdatedAt()),
        () ->
            assertArrayEquals(
                testEntity.getPayload().nonce(), mappedEntry.getPayload().nonce().bytes()),
        () ->
            assertArrayEquals(
                testEntity.getPayload().cipherText(),
                mappedEntry.getPayload().cipherText().bytes()),
        () ->
            assertArrayEquals(
                testEntity.getPayload().tag(), mappedEntry.getPayload().tag().bytes()));
  }

  @Test
  void shouldMapDomainEntryToEntity() {
    try (Entry testEntry =
        new Entry(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new EncryptedValue(
                new SecureBuffer(new byte[12]),
                new SecureBuffer(new byte[] {4, 5, 6}),
                new SecureBuffer(new byte[16])),
            Instant.now(),
            Instant.now())) {
      EntryEntity mappedEntity = mapper.toEntity(testEntry);

      assertNotNull(mappedEntity);
      assertAll(
          () -> assertEquals(testEntry.getId(), mappedEntity.getId()),
          () -> assertEquals(testEntry.getUserId(), mappedEntity.getUserId()),
          () -> assertEquals(testEntry.getCreatedAt(), mappedEntity.getCreatedAt()),
          () -> assertEquals(testEntry.getUpdatedAt(), mappedEntity.getUpdatedAt()),
          () ->
              assertArrayEquals(
                  testEntry.getPayload().nonce().bytes(), mappedEntity.getPayload().nonce()),
          () ->
              assertArrayEquals(
                  testEntry.getPayload().cipherText().bytes(),
                  mappedEntity.getPayload().cipherText()),
          () ->
              assertArrayEquals(
                  testEntry.getPayload().tag().bytes(), mappedEntity.getPayload().tag()));
    }
  }
}
