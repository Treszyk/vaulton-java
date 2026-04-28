package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private SecureBuffer mockBuffer() {
        return new SecureBuffer(new byte[]{1, 2, 3});
    }

    @Test
    void constructorShouldThrowWhenCriticalFieldsAreNull() {
        UUID id = UUID.randomUUID();
        try (SecureBuffer buf = mockBuffer()) {
            KdfMode mode = KdfMode.DEFAULT;

            // Missing Verifier
            assertThrows(NullPointerException.class, () ->
                    new User(id, null, buf, null, null, buf, mode, null, null, null, null, 1, null, null, null, 0, null, null)
            );

            // Missing S_verifier
            assertThrows(NullPointerException.class, () ->
                    new User(id, buf, null, null, null, buf, mode, null, null, null, null, 1, null, null, null, 0, null, null)
            );

            // Missing S_pwd
            assertThrows(NullPointerException.class, () ->
                    new User(id, buf, buf, null, null, null, mode, null, null, null, null, 1, null, null, null, 0, null, null)
            );

            // Missing KdfMode
            assertThrows(NullPointerException.class, () ->
                    new User(id, buf, buf, null, null, buf, null, null, null, null, null, 1, null, null, null, 0, null, null)
            );

            // Missing CryptoSchemaVer
            assertThrows(NullPointerException.class, () ->
                    new User(id, buf, buf, null, null, buf, mode, null, null, null, null, null, null, null, null, 0, null, null)
            );
        }
    }

    @Test
    void constructorShouldCreateUserWhenCriticalFieldsArePresent() {
        assertDoesNotThrow(() -> 
            new User(UUID.randomUUID(), mockBuffer(), mockBuffer(), null, null, mockBuffer(), KdfMode.DEFAULT, null, null, null, null, 1, null, null, null, 0, null, null)
        );
    }
}