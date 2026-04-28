package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntryTest {

    private SecureBuffer mockBuffer(int size) {
        return new SecureBuffer(new byte[size]);
    }

    private EncryptedValue mockPayload() {
        // Nonce 12 bytes, CipherText 16 bytes, Tag 16 bytes
        return new EncryptedValue(mockBuffer(12), mockBuffer(16), mockBuffer(16));
    }

    @Test
    void constructorShouldThrowWhenCriticalFieldsAreNull() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        EncryptedValue payload = mockPayload();

        // Missing UserID
        assertThrows(NullPointerException.class, () ->
                new Entry(id, null, payload, null, null)
        );

        // Missing Payload
        assertThrows(NullPointerException.class, () ->
                new Entry(id, userId, null, null, null)
        );
    }

    @Test
    void constructorShouldCreateEntryWhenCriticalFieldsArePresent() {
        assertDoesNotThrow(() ->
                new Entry(UUID.randomUUID(), UUID.randomUUID(), mockPayload(), null, null)
        );
    }
}
