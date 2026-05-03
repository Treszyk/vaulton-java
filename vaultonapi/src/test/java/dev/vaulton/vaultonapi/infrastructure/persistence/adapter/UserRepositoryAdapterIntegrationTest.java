package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.CryptoMapperImpl;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.UserMapperImpl;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

@SuppressWarnings("resource")
@DataJpaTest()
@Import({
  UserRepositoryAdapter.class,
  UserMapperImpl.class,
  CryptoMapperImpl.class,
})
public class UserRepositoryAdapterIntegrationTest {
  @Autowired private UserRepositoryAdapter adapter;
  @Autowired private TestEntityManager entityManager;

  private User createTestUser(UUID id) {
    return new User(
        id,
        new SecureBuffer(new byte[] {1, 2, 3}),
        new SecureBuffer(new byte[] {4, 5, 6}),
        new SecureBuffer(new byte[] {7, 8, 9}),
        new SecureBuffer(new byte[] {10, 11, 12}),
        new SecureBuffer(new byte[] {13, 14, 15}),
        KdfMode.DEFAULT,
        new EncryptedValue(
            new SecureBuffer(new byte[12]),
            new SecureBuffer(new byte[] {16, 17}),
            new SecureBuffer(new byte[16])),
        new EncryptedValue(
            new SecureBuffer(new byte[12]),
            new SecureBuffer(new byte[] {18, 19}),
            new SecureBuffer(new byte[16])),
        new SecureBuffer(new byte[] {20, 21}),
        new SecureBuffer(new byte[] {22, 23}),
        1,
        Instant.now(),
        Instant.now(),
        Instant.now(),
        0,
        null,
        null);
  }

  @Test
  void shouldSaveAndRetrieveUser() {
    User testUser = createTestUser(UUID.randomUUID());
    adapter.save(testUser);
    entityManager.flush();
    entityManager.clear();

    User foundUser = adapter.findById(testUser.getId()).orElse(null);

    assertNotNull(foundUser);
    assertEquals(testUser.getId(), foundUser.getId());
    assertEquals(testUser.getKdfMode(), foundUser.getKdfMode());
    assertArrayEquals(testUser.getAdminVerifier().bytes(), foundUser.getAdminVerifier().bytes());
  }

  @Test
  void shouldReturnTrueWhenUserExists() {
    User testUser = createTestUser(UUID.randomUUID());
    adapter.save(testUser);
    entityManager.flush();
    entityManager.clear();

    assertTrue(adapter.existsById(testUser.getId()));
  }

  @Test
  void shouldReturnFalseWhenUserDoesNotExists() {
    assertFalse(adapter.existsById(UUID.randomUUID()));
  }

  @Test
  void shouldDeleteUserSuccessfully() {
    User testUser = createTestUser(UUID.randomUUID());
    adapter.save(testUser);
    entityManager.flush();
    entityManager.clear();

    assertTrue(adapter.existsById(testUser.getId()));

    assertTrue(adapter.deleteById(testUser.getId()));

    assertFalse(adapter.existsById(testUser.getId()));
  }

  @Test
  void shouldReturnFalseWhenDeletingNonExistentUser() {
    assertFalse(adapter.deleteById(UUID.randomUUID()));
  }

  @Test
  void shouldUpdateExistingUserFields() {
    User testUser = createTestUser(UUID.randomUUID());
    adapter.save(testUser);
    entityManager.flush();
    entityManager.clear();

    testUser.setFailedLoginCount(67);
    adapter.save(testUser);
    entityManager.flush();
    entityManager.clear();

    User foundUser = adapter.findById(testUser.getId()).orElse(null);
    assertNotNull(foundUser);
    assertEquals(67, foundUser.getFailedLoginCount());
  }
}
