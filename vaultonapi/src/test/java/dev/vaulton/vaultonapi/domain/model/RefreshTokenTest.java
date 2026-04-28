package dev.vaulton.vaultonapi.domain.model;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"resource", "DataFlowIssue"})
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
                    new RefreshToken(null, null, buf, now, now, null, null, buf, mock(User.class))
            );

            // Missing TokenHash
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, null, now, now, null, null, buf, mock(User.class))
            );

            // Missing CreatedAt
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, buf, null, now, null, null, buf, mock(User.class))
            );

            // Missing ExpiresAt
            assertThrows(NullPointerException.class, () ->
                    new RefreshToken(null, userId, buf, now, null, null, null, buf, mock(User.class))
            );
        }
    }

    @Test
    void isActiveShouldReturnTrueWhenNotExpiredAndNotRevoked() {
        try (SecureBuffer buf = mockBuffer(); SecureBuffer jti = mockBuffer()) {
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), future, null, null, jti, mock(User.class)
            );

            assertTrue(token.isActive());
        }
    }

    @Test
    void isActiveShouldReturnFalseWhenExpired() {
        try (SecureBuffer buf = mockBuffer(); SecureBuffer jti = mockBuffer()) {
            Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), past, null, null, jti, mock(User.class)
            );

            assertFalse(token.isActive());
        }
    }

    @Test
    void isActiveShouldReturnFalseWhenRevoked() {
        try (SecureBuffer buf = mockBuffer(); SecureBuffer jti = mockBuffer()) {
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            RefreshToken token = new RefreshToken(
                    UUID.randomUUID(), UUID.randomUUID(), buf, Instant.now(), future, null, null, jti, mock(User.class)
            );

            token.revoke(RevocationReason.REGULAR);

            assertFalse(token.isActive());
            assertEquals(RevocationReason.REGULAR, token.getRevocationReason());
        }
    }

    @Test
    void shouldWipeInternalBuffersOnWipe() {
        SecureBuffer tokenHash = new SecureBuffer(new byte[] {1, 2, 3});
        SecureBuffer jtiHash = new SecureBuffer(new byte[] {3, 2, 1});

        RefreshToken refreshToken = new RefreshToken(
                null,
                UUID.randomUUID(),
                tokenHash,
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.MINUTES),
                null,
                null,
                jtiHash,
                mock(User.class)
        );

        refreshToken.wipe();

        assertThrows(IllegalStateException.class, () -> refreshToken.getTokenHash().bytes());
        assertThrows(IllegalStateException.class, () -> refreshToken.getAccessTokenJtiHash().bytes());
        assertThrows(IllegalStateException.class, tokenHash::bytes);
        assertThrows(IllegalStateException.class, jtiHash::bytes);
    }

    @Test
    void shouldWipeInternalBuffersOnClose() {
        SecureBuffer tokenHash = new SecureBuffer(new byte[] {1, 2, 3});
        SecureBuffer jtiHash = new SecureBuffer(new byte[] {3, 2, 1});

        RefreshToken refreshToken = new RefreshToken(
                null,
                UUID.randomUUID(),
                tokenHash,
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.MINUTES),
                null,
                null,
                jtiHash,
                mock(User.class)
        );

        //noinspection EmptyTryBlock
        try (refreshToken) {
            // Simulated use as a resource
        }

        assertThrows(IllegalStateException.class, () -> refreshToken.getTokenHash().bytes());
        assertThrows(IllegalStateException.class, () -> refreshToken.getAccessTokenJtiHash().bytes());
        assertThrows(IllegalStateException.class, tokenHash::bytes);
        assertThrows(IllegalStateException.class, jtiHash::bytes);
    }
}
