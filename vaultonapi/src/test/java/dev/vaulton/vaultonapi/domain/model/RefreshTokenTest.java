package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private SecureBuffer mockBuffer() {
        return new SecureBuffer(new byte[]{1, 2, 3});
    }

    @Test
    void constructorShouldThrowWhenCriticalFieldsAreNull() {
        UUID userId = UUID.randomUUID();
        try (SecureBuffer buf = mockBuffer()) {
            Instant now = Instant.now();

            // Missing UserID
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, null, buf, now, now, null, null, null, null)
            );

            // Missing TokenHash
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, null, now, now, null, null, null, null)
            );

            // Missing CreatedAt
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, buf, null, now, null, null, null, null)
            );

            // Missing ExpiresAt
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, buf, now, null, null, null, null, null)
            );
        }
    }

    @Test
    void isActiveShouldReturnTrueWhenNotExpiredAndNotRevoked() {
        try (SecureBuffer buf = mockBuffer()) {
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), future, null, null, null, null
            );

            assertTrue(token.isActive());
        }
    }

    @Test
    void isActiveShouldReturnFalseWhenExpired() {
        try (SecureBuffer buf = mockBuffer()) {
            Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), past, null, null, null, null
            );

            assertFalse(token.isActive());
        }
    }

    @Test
    void isActiveShouldReturnFalseWhenRevoked() {
        try (SecureBuffer buf = mockBuffer()) {
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), future, Instant.now(), RevocationReason.REGULAR, null, null
            );

            assertFalse(token.isActive());
        }
    }
}
