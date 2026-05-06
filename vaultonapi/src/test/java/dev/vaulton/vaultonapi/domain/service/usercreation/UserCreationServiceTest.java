package dev.vaulton.vaultonapi.domain.service.usercreation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationError;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

  @Mock private CryptoService cryptoService;

  @Mock private UserRepository userRepository;

  private UserCreationService userCreationService;

  @BeforeEach
  void setUp() {
    userCreationService =
        new UserCreationServiceImpl(1000, new byte[32], cryptoService, userRepository);
  }

  @Test
  void shouldGenerateUniqueIdWhenPreRegisterIsCalled() {
    when(userRepository.existsById(any())).thenReturn(false);

    UUID generatedId = userCreationService.preRegister();

    assertNotNull(generatedId);
    verify(userRepository, atLeastOnce()).existsById(any());
  }

  @Test
  void shouldReturnUnsupportedSchemaWhenVersionIsInvalid() {
    UserCreationInput input = createValidInput(UUID.randomUUID(), 999);

    UserCreationResult result = userCreationService.createUser(input);

    assertInstanceOf(UserCreationResult.Failure.class, result);
    assertEquals(
        UserCreationError.UNSUPPORTED_CRYPTO_SCHEMA, ((UserCreationResult.Failure) result).error());
  }

  @Test
  void shouldReturnSuccessWhenInputIsValid() {
    UUID accountId = UUID.randomUUID();
    UserCreationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(false);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any(), anyInt(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    UserCreationResult result = userCreationService.createUser(input);

    assertInstanceOf(UserCreationResult.Success.class, result);
    User user = ((UserCreationResult.Success) result).user();
    assertEquals(accountId, user.getId());
  }

  @Test
  @SuppressWarnings("resource")
  void shouldReturnFailureWhenAccountAlreadyExists() {
    UUID accountId = UUID.randomUUID();
    UserCreationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(true);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any(), anyInt(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    UserCreationResult result = userCreationService.createUser(input);

    assertInstanceOf(UserCreationResult.Failure.class, result);
    assertEquals(UserCreationError.ACCOUNT_EXISTS, ((UserCreationResult.Failure) result).error());
  }

  @Test
  void shouldWipeSecretsOnFailure() {
    UUID accountId = UUID.randomUUID();
    UserCreationInput input = createValidInput(accountId);

    when(userRepository.existsById(accountId)).thenReturn(true);
    when(cryptoService.generateRandomBytes(anyInt())).thenReturn(new SecureBuffer(new byte[16]));
    when(cryptoService.computeStoredVerifier(any(), any(), anyInt(), any()))
        .thenReturn(new SecureBuffer(new byte[32]));

    userCreationService.createUser(input);

    verify(cryptoService, times(3)).computeStoredVerifier(any(), any(), anyInt(), any());
    verify(userRepository, atLeastOnce()).existsById(any());
  }

  private UserCreationInput createValidInput(UUID id) {
    return createValidInput(id, 1);
  }

  private UserCreationInput createValidInput(UUID id, int schemaVer) {
    return new UserCreationInput(
        id,
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.VERIFIER_LEN]),
        new SecureBuffer(new byte[CryptoConstants.SALT_LEN]),
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
