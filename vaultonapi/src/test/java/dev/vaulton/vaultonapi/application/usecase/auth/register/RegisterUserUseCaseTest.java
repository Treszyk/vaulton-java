package dev.vaulton.vaultonapi.application.usecase.auth.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.vaulton.vaultonapi.application.dto.auth.requests.RegisterRequest;
import dev.vaulton.vaultonapi.application.dto.auth.responses.RegisterResponse;
import dev.vaulton.vaultonapi.application.dto.shared.EncryptedValueDto;
import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.exception.VaultonDomainException;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationError;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  @Mock private UserCreationService userCreationService;

  @Mock private UserRepository userRepository;

  @InjectMocks private RegisterUserUseCaseImpl useCase;

  @Test
  void shouldSuccessfullyRegisterUser() {
    UUID accountId = UUID.randomUUID();
    byte[] verifier = new byte[CryptoConstants.VERIFIER_LEN];
    byte[] salt = new byte[CryptoConstants.SALT_LEN];
    EncryptedValueDto dummyEnc =
        new EncryptedValueDto(
            new byte[CryptoConstants.GCM_NONCE_LEN],
            new byte[32],
            new byte[CryptoConstants.GCM_TAG_LEN]);

    RegisterRequest request =
        new RegisterRequest(
            accountId, verifier, verifier, verifier, salt, 1, dummyEnc, dummyEnc, 1);

    User mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn(accountId);
    when(userCreationService.createUser(any(UserCreationInput.class)))
        .thenReturn(new UserCreationResult.Success(mockUser));

    RegisterResponse response = useCase.execute(request);

    assertNotNull(response);
    assertEquals(accountId, response.accountId());
    verify(userRepository, times(1)).save(mockUser);
    verify(mockUser, times(1)).close();
  }

  @Test
  void shouldHandleRegistrationFailure() {
    UUID accountId = UUID.randomUUID();
    byte[] verifier = new byte[CryptoConstants.VERIFIER_LEN];
    byte[] salt = new byte[CryptoConstants.SALT_LEN];
    EncryptedValueDto dummyEnc =
        new EncryptedValueDto(
            new byte[CryptoConstants.GCM_NONCE_LEN],
            new byte[32],
            new byte[CryptoConstants.GCM_TAG_LEN]);

    RegisterRequest request =
        new RegisterRequest(
            accountId, verifier, verifier, verifier, salt, 1, dummyEnc, dummyEnc, 1);

    when(userCreationService.createUser(any(UserCreationInput.class)))
        .thenReturn(new UserCreationResult.Failure(UserCreationError.ACCOUNT_EXISTS));

    assertThrows(VaultonDomainException.class, () -> useCase.execute(request));
    verify(userRepository, never()).save(any());
  }
}
