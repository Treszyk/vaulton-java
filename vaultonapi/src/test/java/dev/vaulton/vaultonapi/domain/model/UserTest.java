package dev.vaulton.vaultonapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({"resource", "DataFlowIssue"})
class UserTest {

  private SecureBuffer mockBuffer() {
    return new SecureBuffer(new byte[] {1, 2, 3});
  }

  @Test
  void constructorShouldThrowWhenCriticalFieldsAreNull() {
    UUID id = UUID.randomUUID();
    try (SecureBuffer buf = mockBuffer()) {
      KdfMode mode = KdfMode.DEFAULT;
      EncryptedValue mockWrap = mock(EncryptedValue.class);

      // Missing Verifier
      assertThrows(
          NullPointerException.class,
          () ->
              new User(
                  id, null, buf, buf, buf, buf, mode, mockWrap, mockWrap, buf, buf, 1, null, null,
                  null, 0, null, null));

      // Missing mkWrapPwd
      assertThrows(
          NullPointerException.class,
          () ->
              new User(
                  id, buf, buf, buf, buf, buf, mode, null, mockWrap, buf, buf, 1, null, null, null,
                  0, null, null));

      // Missing rkVerifier
      assertThrows(
          NullPointerException.class,
          () ->
              new User(
                  id, buf, buf, buf, buf, buf, mode, mockWrap, mockWrap, null, buf, 1, null, null,
                  null, 0, null, null));
    }
  }

  @Test
  void constructorShouldCreateUserWhenCriticalFieldsArePresent() {
    try (SecureBuffer buf = mockBuffer()) {
      EncryptedValue mockWrap = mock(EncryptedValue.class);
      assertDoesNotThrow(
          () ->
              new User(
                  UUID.randomUUID(),
                  buf,
                  buf,
                  buf,
                  buf,
                  buf,
                  KdfMode.DEFAULT,
                  mockWrap,
                  mockWrap,
                  buf,
                  buf,
                  1,
                  null,
                  null,
                  null,
                  0,
                  null,
                  null));
    }
  }

  @Test
  void shouldWipeInternalBuffersOnWipe() {
    SecureBuffer buf = mockBuffer();
    EncryptedValue mockWrap = mock(EncryptedValue.class);

    User user =
        new User(
            UUID.randomUUID(),
            buf,
            buf,
            buf,
            buf,
            buf,
            KdfMode.DEFAULT,
            mockWrap,
            mockWrap,
            buf,
            buf,
            1,
            null,
            null,
            null,
            0,
            null,
            null);
    user.wipe();

    // Verify cascading wipe to SecureBuffers
    assertThrows(IllegalStateException.class, buf::bytes);

    // Verify cascading wipe to EncryptedValues
    verify(mockWrap, Mockito.atLeastOnce()).wipe();
  }

  @Test
  void shouldWipeInternalBuffersOnClose() {
    SecureBuffer buf = mockBuffer();
    EncryptedValue mockWrap = mock(EncryptedValue.class);

    User user =
        new User(
            UUID.randomUUID(),
            buf,
            buf,
            buf,
            buf,
            buf,
            KdfMode.DEFAULT,
            mockWrap,
            mockWrap,
            buf,
            buf,
            1,
            null,
            null,
            null,
            0,
            null,
            null);

    //noinspection EmptyTryBlock
    try (user) {
      // Simulated use as a resource
    }

    assertThrows(IllegalStateException.class, buf::bytes);
    verify(mockWrap, Mockito.atLeastOnce()).wipe();
  }
}
