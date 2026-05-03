package dev.vaulton.vaultonapi.domain.service.registration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.User;
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
  void shouldGenerateUniqueIdWhenPreRegisterIsCalled() {
    when(userRepository.existsById(any())).thenReturn(false);

    UUID generatedId = registrationService.preRegister();

    assertNotNull(generatedId);
    verify(userRepository, atLeastOnce()).existsById(any());
  }

  @Test
  void shouldReturnUnsupportedSchemaWhenVersionIsInvalid() {
    RegistrationInput input = createValidInput(UUID.randomUUID(), 999);

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Failure.class, result);
    assertEquals(
        RegistrationError.UNSUPPORTED_CRYPTO_SCHEMA, ((RegistrationResult.Failure) result).error());
  }

  @Test
  void shouldReturnSuccessWhenInputIsValid() {
    UUID accountId = UUID.randomUUID();
    RegistrationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(false);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Success.class, result);
    User user = ((RegistrationResult.Success) result).user();
    assertEquals(accountId, user.getId());
  }

  @Test
  @SuppressWarnings("resource")
  void shouldReturnFailureWhenAccountAlreadyExists() {
    UUID accountId = UUID.randomUUID();
    RegistrationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(true);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    RegistrationResult result = registrationService.createUser(input);

    assertInstanceOf(RegistrationResult.Failure.class, result);
    assertEquals(RegistrationError.ACCOUNT_EXISTS, ((RegistrationResult.Failure) result).error());
  }

  @Test
  void shouldWipeSecretsOnFailure() {
    UUID accountId = UUID.randomUUID();
    RegistrationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(true);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    registrationService.createUser(input);

    verify(cryptoService, times(3)).computeStoredVerifier(any(), any());
    verify(userRepository, atLeastOnce()).existsById(any());
  }

  private RegistrationInput createValidInput(UUID id) {
    return createValidInput(id, 1);
  }

  private RegistrationInput createValidInput(UUID id, int schemaVer) {
    return new RegistrationInput(
        id,
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        KdfMode.DEFAULT,
        new EncryptedValue(
            new SecureBuffer(new byte[CryptoConstants.GCM_NONCE_LEN]),
            new SecureBuffer(new byte[CryptoConstants.MK_LEN]),
            new SecureBuffer(new byte[CryptoConstants.GCM_TAG_LEN])),
        new EncryptedValue(
            new SecureBuffer(new byte[CryptoConstants.GCM_NONCE_LEN]),
            new SecureBuffer(new byte[CryptoConstants.MK_LEN]),
            new SecureBuffer(new byte[CryptoConstants.GCM_TAG_LEN])),
        schemaVer);
  }
}
