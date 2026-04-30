package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.RefreshTokenEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("resource")
class RefreshTokenMapperTest {
  private final RefreshTokenMapper mapper = Mappers.getMapper(RefreshTokenMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        mapper, "cryptoMapper", Mappers.getMapper(CryptoMapper.class));
  }

  @Test
  void shouldMapRefreshTokenEntityToDomain() {
    RefreshTokenEntity testEntity = new RefreshTokenEntity(
        UUID.randomUUID(),
        UUID.randomUUID(),
        new byte[] {1, 2, 3},
        new byte[] {4, 5, 6},
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Instant.now(),
        RevocationReason.REGULAR
    );

    RefreshToken mappedToken = mapper.toDomain(testEntity);

    assertNotNull(mappedToken);
    assertAll(
        () -> assertEquals(testEntity.getId(), mappedToken.getId()),
        () -> assertEquals(testEntity.getUserId(), mappedToken.getUserId()),
        () -> assertEquals(testEntity.getCreatedAt(), mappedToken.getCreatedAt()),
        () -> assertEquals(testEntity.getExpiresAt(), mappedToken.getExpiresAt()),
        () -> assertEquals(testEntity.getRevokedAt(), mappedToken.getRevokedAt()),
        () -> assertEquals(testEntity.getRevocationReason(), mappedToken.getRevocationReason()),
        () -> assertArrayEquals(testEntity.getTokenHash(), mappedToken.getTokenHash().bytes()),
        () -> assertArrayEquals(testEntity.getAccessTokenJtiHash(), mappedToken.getAccessTokenJtiHash().bytes())
    );
  }

  @Test
  void shouldMapDomainRefreshTokenToEntity() {
    try (RefreshToken testToken = new RefreshToken(
        UUID.randomUUID(),
        UUID.randomUUID(),
        new SecureBuffer(new byte[] {7, 8, 9}),
        Instant.now(),
        Instant.now().plusSeconds(3600),
        null,
        null,
        new SecureBuffer(new byte[] {10, 11, 12})
    )) {
      RefreshTokenEntity mappedEntity = mapper.toEntity(testToken);

      assertNotNull(mappedEntity);
      assertAll(
          () -> assertEquals(testToken.getId(), mappedEntity.getId()),
          () -> assertEquals(testToken.getUserId(), mappedEntity.getUserId()),
          () -> assertEquals(testToken.getCreatedAt(), mappedEntity.getCreatedAt()),
          () -> assertEquals(testToken.getExpiresAt(), mappedEntity.getExpiresAt()),
          () -> assertNull(mappedEntity.getRevokedAt()),
          () -> assertNull(mappedEntity.getRevocationReason()),
          () -> assertArrayEquals(testToken.getTokenHash().bytes(), mappedEntity.getTokenHash()),
          () -> assertArrayEquals(testToken.getAccessTokenJtiHash().bytes(), mappedEntity.getAccessTokenJtiHash())
      );
    }
  }
}
