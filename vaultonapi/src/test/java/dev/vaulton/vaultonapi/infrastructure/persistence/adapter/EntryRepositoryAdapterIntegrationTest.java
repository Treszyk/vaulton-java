package dev.vaulton.vaultonapi.infrastructure.persistence.adapter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.model.Entry;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.CryptoMapperImpl;
import dev.vaulton.vaultonapi.infrastructure.persistence.mapper.EntryMapperImpl;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

@SuppressWarnings("resource")
@DataJpaTest
@Import({EntryRepositoryAdapter.class, EntryMapperImpl.class, CryptoMapperImpl.class})
public class EntryRepositoryAdapterIntegrationTest {
  @Autowired private EntryRepositoryAdapter adapter;
  @Autowired private TestEntityManager entityManager;

  private Entry createTestEntry(UUID id, UUID userId) {
    return new Entry(
        id,
        userId,
        new EncryptedValue(
            new SecureBuffer(new byte[12]),
            new SecureBuffer(new byte[] {1, 2, 3}),
            new SecureBuffer(new byte[16])),
        Instant.now(),
        Instant.now());
  }

  @Test
  void shouldSaveAndRetrieveEntryByIdAndUserId() {
    UUID testUserId = UUID.randomUUID();
    UUID testEntryId = UUID.randomUUID();

    Entry testEntry = createTestEntry(testEntryId, testUserId);
    adapter.save(testEntry);
    entityManager.flush();
    entityManager.clear();

    Entry foundEntry = adapter.findByIdAndUserId(testEntryId, testUserId).orElse(null);

    assertNotNull(foundEntry);
    assertEquals(testEntryId, foundEntry.getId());
    assertArrayEquals(
        testEntry.getPayload().cipherText().bytes(), foundEntry.getPayload().cipherText().bytes());
  }

  @Test
  void shouldNotRetrieveEntryWhenUserIdMismatch() {
    UUID testEntryId = UUID.randomUUID();

    Entry testEntry = createTestEntry(testEntryId, UUID.randomUUID());
    adapter.save(testEntry);
    entityManager.flush();
    entityManager.clear();

    Entry foundEntry = adapter.findByIdAndUserId(testEntryId, UUID.randomUUID()).orElse(null);

    assertNull(foundEntry);
  }

  @Test
  void shouldFindAllEntriesByUserId() {
    UUID testUserIdA = UUID.randomUUID();
    UUID testUserIdB = UUID.randomUUID();

    for (int i = 0; i < 10; i++) adapter.save(createTestEntry(UUID.randomUUID(), testUserIdA));
    adapter.save(createTestEntry(UUID.randomUUID(), testUserIdB));
    entityManager.flush();
    entityManager.clear();

    List<Entry> foundEntries = adapter.findByUserId(testUserIdA);

    assertEquals(10, foundEntries.size());
    assertThat(foundEntries)
        .allSatisfy(
            entry -> {
              assertEquals(testUserIdA, entry.getUserId());
            });
  }

  @Test
  void shouldDeleteEntryOnlyWhenUserIdMatches() {
    UUID testEntryId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();

    Entry testEntry = createTestEntry(testEntryId, testUserId);
    adapter.save(testEntry);
    entityManager.flush();
    entityManager.clear();

    boolean foundEntryWrongId = adapter.deleteByIdAndUserId(testEntryId, UUID.randomUUID());
    boolean foundEntryCorrectId = adapter.deleteByIdAndUserId(testEntryId, testUserId);

    assertFalse(foundEntryWrongId);
    assertTrue(foundEntryCorrectId);
    assertTrue(adapter.findByIdAndUserId(testEntryId, testUserId).isEmpty());
  }
}
