package dev.vaulton.vaultonapi.domain.service.usercreation;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserCreationInputTest {

  @Test
  void shouldReturnTrueWhenAllFieldsArePresent() {
    UserCreationInput input = createValidInput();
    assertTrue(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenAccountIdIsNull() {
    UserCreationInput input = createInputWithNullField("accountId");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenVerifierIsNull() {
    UserCreationInput input = createInputWithNullField("verifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenAdminVerifierIsNull() {
    UserCreationInput input = createInputWithNullField("adminVerifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenRkVerifierIsNull() {
    UserCreationInput input = createInputWithNullField("rkVerifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenSPwdIsNull() {
    UserCreationInput input = createInputWithNullField("sPwd");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenKdfModeIsNull() {
    UserCreationInput input = createInputWithNullField("kdfMode");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenMkWrapPwdIsNull() {
    UserCreationInput input = createInputWithNullField("mkWrapPwd");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenMkWrapRkIsNull() {
    UserCreationInput input = createInputWithNullField("mkWrapRk");
    assertFalse(input.isValid());
  }

  private UserCreationInput createValidInput() {
    return new UserCreationInput(
        UUID.randomUUID(),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.SALT_LEN]),
        KdfMode.DEFAULT,
        createRealWrap(),
        createRealWrap(),
        1);
  }

  private UserCreationInput createInputWithNullField(String fieldName) {
    return new UserCreationInput(
        fieldName.equals("accountId") ? null : UUID.randomUUID(),
        fieldName.equals("verifier")
            ? null
            : new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        fieldName.equals("adminVerifier")
            ? null
            : new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        fieldName.equals("rkVerifier")
            ? null
            : new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        fieldName.equals("sPwd") ? null : new SecureBuffer(new byte[CryptoConstants.SALT_LEN]),
        fieldName.equals("kdfMode") ? null : KdfMode.DEFAULT,
        fieldName.equals("mkWrapPwd") ? null : createRealWrap(),
        fieldName.equals("mkWrapRk") ? null : createRealWrap(),
        1);
  }

  private EncryptedValue createRealWrap() {
    return new EncryptedValue(
        new SecureBuffer(new byte[CryptoConstants.GCM_NONCE_LEN]),
        new SecureBuffer(new byte[CryptoConstants.MK_LEN]),
        new SecureBuffer(new byte[CryptoConstants.GCM_TAG_LEN]));
  }
}
