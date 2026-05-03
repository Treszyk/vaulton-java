package dev.vaulton.vaultonapi.domain.service.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationError;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationInput;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

  @Mock private CryptoService cryptoService;

  @Mock private UserRepository userRepository;

  private UserRegistrationService registrationService;

  @BeforeEach
  void setUp() {
    registrationService = new UserRegistrationServiceImpl(cryptoService, userRepository);
  }

  @Test
  void shouldReturnAccountExistsWhenIdIsTaken() {
    UUID existingId = UUID.randomUUID();
    when(userRepository.existsById(existingId)).thenReturn(true);
    RegistrationInput input = createValidInput(existingId);

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Failure.class, result);
    assertEquals(RegistrationError.ACCOUNT_EXISTS, ((RegistrationResult.Failure) result).error());
  }

  @Test
  void shouldReturnUnsupportedSchemaWhenVersionIsInvalid() {
    RegistrationInput input =
        new RegistrationInput(
            UUID.randomUUID(),
            mock(SecureBuffer.class),
            mock(SecureBuffer.class),
            mock(SecureBuffer.class),
            mock(SecureBuffer.class),
            KdfMode.DEFAULT,
            mock(EncryptedValue.class),
            mock(EncryptedValue.class),
            999);

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Failure.class, result);
    assertEquals(
        RegistrationError.UNSUPPORTED_CRYPTO_SCHEMA, ((RegistrationResult.Failure) result).error());
  }

  @Test
  @SuppressWarnings("resource")
  void shouldReturnSuccessWhenInputIsValid() {
    UUID accountId = UUID.randomUUID();
    RegistrationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(false);

    SecureBuffer mockSalt = new SecureBuffer(new byte[CryptoConstants.SALT_LEN]);
    SecureBuffer mockStored = new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(mockSalt);
    when(cryptoService.computeStoredVerifier(any(), any())).thenReturn(mockStored);

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Success.class, result);
    RegistrationResult.Success success = (RegistrationResult.Success) result;

    assertNotNull(success.user());
    assertEquals(accountId, success.user().getId());

    verify(cryptoService, times(3)).generateRandomBytes(anyInt());
    verify(cryptoService, times(3)).computeStoredVerifier(any(), any());
  }

  @Test
  void shouldGenerateUniqueIdWhenPreRegisterIsCalled() {
    UUID firstId = UUID.randomUUID();
    UUID secondId = UUID.randomUUID();

    when(userRepository.existsById(any())).thenReturn(false);

    UUID generatedId = registrationService.preRegister();

    assertNotNull(generatedId);
    verify(userRepository, atLeastOnce()).existsById(any());
  }

  private RegistrationInput createValidInput(UUID id) {
    return new RegistrationInput(
        id,
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        KdfMode.DEFAULT,
        new EncryptedValue(
            new SecureBuffer(new byte[CryptoConstants.GCM_NONCE_LEN]),
            new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
            new SecureBuffer(new byte[CryptoConstants.GCM_TAG_LEN])),
        new EncryptedValue(
            new SecureBuffer(new byte[CryptoConstants.GCM_NONCE_LEN]),
            new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
            new SecureBuffer(new byte[CryptoConstants.GCM_TAG_LEN])),
        1);
  }
}
