package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import dev.vaulton.vaultonapi.domain.model.RefreshToken;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.CryptoMapperImpl;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.RefreshTokenMapperImpl;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

@SuppressWarnings("resource")
@DataJpaTest
@Import({RefreshTokenRepositoryAdapter.class, RefreshTokenMapperImpl.class, CryptoMapperImpl.class})
public class RefreshTokenRepositoryAdapterIntegrationTest {

  @Autowired private RefreshTokenRepositoryAdapter adapter;

  @Autowired private TestEntityManager entityManager;

  private RefreshToken createTestToken(UUID id, UUID userId, byte[] hash) {
    return new RefreshToken(
        id,
        userId,
        new SecureBuffer(hash),
        Instant.now(),
        Instant.now().plus(7, ChronoUnit.DAYS),
        null,
        null,
        new SecureBuffer(new byte[32]));
  }

  @Test
  void shouldSaveAndRetrieveTokenByHash() {
    UUID testTokenId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();

    RefreshToken testRefreshToken =
        createTestToken(testTokenId, testUserId, new byte[] {1, 3, 3, 7});
    adapter.save(testRefreshToken);
    entityManager.flush();
    entityManager.clear();

    RefreshToken foundRefreshToken =
        adapter.findByTokenHash(new SecureBuffer(new byte[] {1, 3, 3, 7})).orElse(null);
    assertNotNull(foundRefreshToken);
    assertEquals(testTokenId, foundRefreshToken.getId());
    assertEquals(testUserId, foundRefreshToken.getUserId());
    assertArrayEquals(new byte[] {1, 3, 3, 7}, foundRefreshToken.getTokenHash().bytes());
  }

  @Test
  void shouldFindAllTokensForUser() {
    UUID testUserIdA = UUID.randomUUID();
    UUID testUserIdB = UUID.randomUUID();

    for (int i = 0; i < 10; i++)
      adapter.save(createTestToken(UUID.randomUUID(), testUserIdA, new byte[] {1, 3, 3, 7}));
    adapter.save(createTestToken(UUID.randomUUID(), testUserIdB, new byte[] {7, 3, 3, 1}));
    entityManager.flush();
    entityManager.clear();

    List<RefreshToken> foundTokens = adapter.findAllByUserId(testUserIdA);

    assertEquals(10, foundTokens.size());
    assertThat(foundTokens)
        .allSatisfy(
            token -> {
              assertEquals(testUserIdA, token.getUserId());
            });
  }

  @Test
  void shouldRevokeAllActiveTokensForUser() {
    UUID testUserIdA = UUID.randomUUID();

    boolean earlyRevokeResult =
        adapter.revokeAllByUserId(testUserIdA, Instant.now(), RevocationReason.SECURITY);

    for (int i = 0; i < 10; i++)
      adapter.save(createTestToken(UUID.randomUUID(), testUserIdA, new byte[] {1, 3, 3, 7}));
    entityManager.flush();
    entityManager.clear();

    boolean revokeResult =
        adapter.revokeAllByUserId(testUserIdA, Instant.now(), RevocationReason.SECURITY);
    List<RefreshToken> foundTokens = adapter.findAllByUserId(testUserIdA);

    assertFalse(earlyRevokeResult);
    assertTrue(revokeResult);
    assertEquals(10, foundTokens.size());
    assertThat(foundTokens)
        .allSatisfy(
            token -> {
              assertEquals(testUserIdA, token.getUserId());
              assertNotNull(token.getRevokedAt());
              assertEquals(RevocationReason.SECURITY, token.getRevocationReason());
            });
  }

  @Test
  void shouldHandleTokenUpdatesAndRevocation() {
    UUID testTokenId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    byte[] tokenHash = new byte[] {1, 3, 3, 7};

    RefreshToken testRefreshToken = createTestToken(testTokenId, testUserId, tokenHash);
    adapter.save(testRefreshToken);
    entityManager.flush();
    entityManager.clear();

    RefreshToken foundRefreshToken =
        adapter.findByTokenHash(new SecureBuffer(tokenHash)).orElse(null);
    assertNotNull(foundRefreshToken);

    Instant revocationTime = Instant.now();
    foundRefreshToken.setRevokedAt(revocationTime);
    foundRefreshToken.setRevocationReason(RevocationReason.REGULAR);

    adapter.save(foundRefreshToken);

    RefreshToken foundUpdatedRefreshToken =
        adapter.findByTokenHash(new SecureBuffer(tokenHash)).orElse(null);
    assertNotNull(foundUpdatedRefreshToken);
    assertEquals(testTokenId, foundUpdatedRefreshToken.getId());
    assertEquals(testUserId, foundUpdatedRefreshToken.getUserId());
    assertArrayEquals(tokenHash, foundUpdatedRefreshToken.getTokenHash().bytes());
    assertEquals(revocationTime, foundUpdatedRefreshToken.getRevokedAt());
    assertEquals(RevocationReason.REGULAR, foundUpdatedRefreshToken.getRevocationReason());
  }

  @Test
  void shouldPersistRevocationReasonCorrectly() {
    UUID testTokenId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();

    RefreshToken token = createTestToken(testTokenId, testUserId, new byte[] {1, 1, 1});
    token.setRevokedAt(Instant.now());
    token.setRevocationReason(RevocationReason.SECURITY);

    adapter.save(token);
    entityManager.flush();
    entityManager.clear();

    RefreshToken found =
        adapter.findByTokenHash(new SecureBuffer(new byte[] {1, 1, 1})).orElseThrow();

    assertEquals(RevocationReason.SECURITY, found.getRevocationReason());
    assertNotNull(found.getRevokedAt());
  }
}
