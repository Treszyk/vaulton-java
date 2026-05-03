package dev.vaulton.vaultonapi.domain.service.registration;

import static org.junit.jupiter.api.Assertions.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationInput;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegistrationInputTest {

  @Test
  void shouldReturnTrueWhenAllFieldsArePresent() {
    RegistrationInput input = createValidInput();
    assertTrue(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenAccountIdIsNull() {
    RegistrationInput input = createInputWithNullField("accountId");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenVerifierIsNull() {
    RegistrationInput input = createInputWithNullField("verifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenAdminVerifierIsNull() {
    RegistrationInput input = createInputWithNullField("adminVerifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenRkVerifierIsNull() {
    RegistrationInput input = createInputWithNullField("rkVerifier");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenSPwdIsNull() {
    RegistrationInput input = createInputWithNullField("sPwd");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenKdfModeIsNull() {
    RegistrationInput input = createInputWithNullField("kdfMode");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenMkWrapPwdIsNull() {
    RegistrationInput input = createInputWithNullField("mkWrapPwd");
    assertFalse(input.isValid());
  }

  @Test
  void shouldReturnFalseWhenMkWrapRkIsNull() {
    RegistrationInput input = createInputWithNullField("mkWrapRk");
    assertFalse(input.isValid());
  }

  private RegistrationInput createValidInput() {
    return new RegistrationInput(
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

  private RegistrationInput createInputWithNullField(String fieldName) {
    return new RegistrationInput(
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
        fieldName.equals("sPwd") ? null : new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
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
