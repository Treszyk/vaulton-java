package dev.vaulton.vaultonapi.infrastructure.persistence.mapper;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.JpaEncryptedValue;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.UserEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("resource")
class UserMapperTest {
  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        mapper, "cryptoMapper", Mappers.getMapper(CryptoMapper.class));
  }

  @Test
  void shouldMapUserEntityToDomain() {
    UserEntity testUser =
        new UserEntity(
            UUID.randomUUID(),
            new byte[] {1, 2, 3},
            new byte[] {4, 5, 6},
            new byte[] {7, 8, 9},
            new byte[] {10, 11, 12},
            new byte[] {13, 14, 15},
            KdfMode.DEFAULT,
            new JpaEncryptedValue(new byte[12], new byte[] {16, 17}, new byte[16]),
            new JpaEncryptedValue(new byte[12], new byte[] {18, 19}, new byte[16]),
            new byte[] {20, 21},
            new byte[] {22, 23},
            1,
            Instant.now(),
            Instant.now(),
            Instant.now(),
            5,
            Instant.now(),
            Instant.now()
        );

    User mappedUser = mapper.toDomain(testUser);

    assertNotNull(mappedUser);
    assertAll(
        () -> assertEquals(testUser.getId(), mappedUser.getId()),
        () -> assertEquals(testUser.getKdfMode(), mappedUser.getKdfMode()),
        () -> assertEquals(testUser.getCryptoSchemaVer(), mappedUser.getCryptoSchemaVer()),
        () -> assertEquals(testUser.getCreatedAt(), mappedUser.getCreatedAt()),
        () -> assertEquals(testUser.getUpdatedAt(), mappedUser.getUpdatedAt()),
        () -> assertEquals(testUser.getLastLoginAt(), mappedUser.getLastLoginAt()),
        () -> assertEquals(testUser.getFailedLoginCount(), mappedUser.getFailedLoginCount()),
        () -> assertEquals(testUser.getLastFailedLoginAt(), mappedUser.getLastFailedLoginAt()),
        () -> assertEquals(testUser.getLockedUntil(), mappedUser.getLockedUntil()),

        () -> assertArrayEquals(testUser.getVerifier(), mappedUser.getVerifier().bytes()),
        () -> assertArrayEquals(testUser.getSaltVerifier(), mappedUser.getSaltVerifier().bytes()),
        () -> assertArrayEquals(testUser.getAdminVerifier(), mappedUser.getAdminVerifier().bytes()),
        () ->
            assertArrayEquals(testUser.getSaltAdminVerifier(), mappedUser.getSaltAdminVerifier().bytes()),
        () -> assertArrayEquals(testUser.getSaltPwd(), mappedUser.getSaltPwd().bytes()),
        () -> assertArrayEquals(testUser.getRkVerifier(), mappedUser.getRkVerifier().bytes()),
        () -> assertArrayEquals(testUser.getSaltRk(), mappedUser.getSaltRk().bytes()),

        () ->
            assertArrayEquals(
                testUser.getMkWrapPwd().nonce(), mappedUser.getMkWrapPwd().nonce().bytes()),
        () ->
            assertArrayEquals(
                testUser.getMkWrapPwd().cipherText(), mappedUser.getMkWrapPwd().cipherText().bytes()),
        () ->
            assertArrayEquals(testUser.getMkWrapPwd().tag(), mappedUser.getMkWrapPwd().tag().bytes()));
  }

  @Test
  void shouldMapDomainUserToEntity() {
    User testUser = new User(
        UUID.randomUUID(),
        new SecureBuffer(new byte[] {1, 2, 3}),
        new SecureBuffer(new byte[] {4, 5, 6}),
        new SecureBuffer(new byte[] {7, 8, 9}),
        new SecureBuffer(new byte[] {10, 11, 12}),
        new SecureBuffer(new byte[] {13, 14, 15}),
        KdfMode.DEFAULT,
        new EncryptedValue(new SecureBuffer(new byte[12]), new SecureBuffer(new byte[] {16, 17}), new SecureBuffer(new byte[16])),
        new EncryptedValue(new SecureBuffer(new byte[12]), new SecureBuffer(new byte[] {18, 19}), new SecureBuffer(new byte[16])),
        new SecureBuffer(new byte[] {20, 21}),
        new SecureBuffer(new byte[] {22, 23}),
        1,
        Instant.now(),
        Instant.now(),
        Instant.now(),
        5,
        Instant.now(),
        Instant.now()
    );

    UserEntity mappedUser = mapper.toEntity(testUser);
    assertNotNull(mappedUser);
    assertAll(
        () -> assertEquals(testUser.getId(), mappedUser.getId()),
        () -> assertEquals(testUser.getKdfMode(), mappedUser.getKdfMode()),
        () -> assertEquals(testUser.getCryptoSchemaVer(), mappedUser.getCryptoSchemaVer()),
        () -> assertEquals(testUser.getCreatedAt(), mappedUser.getCreatedAt()),
        () -> assertEquals(testUser.getUpdatedAt(), mappedUser.getUpdatedAt()),
        () -> assertEquals(testUser.getLastLoginAt(), mappedUser.getLastLoginAt()),
        () -> assertEquals(testUser.getFailedLoginCount(), mappedUser.getFailedLoginCount()),
        () -> assertEquals(testUser.getLastFailedLoginAt(), mappedUser.getLastFailedLoginAt()),
        () -> assertEquals(testUser.getLockedUntil(), mappedUser.getLockedUntil()),

        () -> assertArrayEquals(testUser.getVerifier().bytes(), mappedUser.getVerifier()),
        () -> assertArrayEquals(testUser.getSaltVerifier().bytes(), mappedUser.getSaltVerifier()),
        () -> assertArrayEquals(testUser.getAdminVerifier().bytes(), mappedUser.getAdminVerifier()),
        () ->
            assertArrayEquals(testUser.getSaltAdminVerifier().bytes(), mappedUser.getSaltAdminVerifier()),
        () -> assertArrayEquals(testUser.getSaltPwd().bytes(), mappedUser.getSaltPwd()),
        () -> assertArrayEquals(testUser.getRkVerifier().bytes(), mappedUser.getRkVerifier()),
        () -> assertArrayEquals(testUser.getSaltRk().bytes(), mappedUser.getSaltRk()),

        () ->
            assertArrayEquals(
                testUser.getMkWrapPwd().nonce().bytes(), mappedUser.getMkWrapPwd().nonce()),
        () ->
            assertArrayEquals(
                testUser.getMkWrapPwd().cipherText().bytes(), mappedUser.getMkWrapPwd().cipherText()),
        () ->
            assertArrayEquals(testUser.getMkWrapPwd().tag().bytes(), mappedUser.getMkWrapPwd().tag()));
  }

  @Test
  void shouldHandleNulls() {
    assertNull(mapper.toDomain(null));
    assertNull(mapper.toEntity(null));
  }
}