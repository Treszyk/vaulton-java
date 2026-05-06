package dev.vaulton.vaultonapi.application.usecase.auth.register;

import dev.vaulton.vaultonapi.application.dto.auth.requests.RegisterRequest;
import dev.vaulton.vaultonapi.application.dto.auth.responses.RegisterResponse;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.usercreation.UserCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

  private final UserCreationService userCreationService;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public RegisterResponse execute(RegisterRequest request) {
    User createdUser = null;

    try (SecureBuffer loginVerifier = new SecureBuffer(request.verifier());
        SecureBuffer sPwd = new SecureBuffer(request.s_pwd());
        SecureBuffer adminVerifier = new SecureBuffer(request.adminVerifier());
        SecureBuffer recoveryVerifier = new SecureBuffer(request.rkVerifier());
        SecureBuffer mkNonce = new SecureBuffer(request.mkWrapPwd().nonce());
        SecureBuffer mkCipher = new SecureBuffer(request.mkWrapPwd().cipherText());
        SecureBuffer mkTag = new SecureBuffer(request.mkWrapPwd().tag());
        SecureBuffer rkNonce = new SecureBuffer(request.mkWrapRk().nonce());
        SecureBuffer rkCipher = new SecureBuffer(request.mkWrapRk().cipherText());
        SecureBuffer rkTag = new SecureBuffer(request.mkWrapRk().tag());
        EncryptedValue mkWrapPwd = new EncryptedValue(mkNonce, mkCipher, mkTag);
        EncryptedValue mkWrapRk = new EncryptedValue(rkNonce, rkCipher, rkTag); ) {
      UserCreationInput input =
          new UserCreationInput(
              request.accountId(),
              loginVerifier,
              adminVerifier,
              recoveryVerifier,
              sPwd,
              KdfMode.fromValue(request.kdfMode()),
              mkWrapPwd,
              mkWrapRk,
              request.cryptoSchemaVer());

      UserCreationResult result = userCreationService.createUser(input);

      return switch (result) {
        case UserCreationResult.Success success -> {
          createdUser = success.user();
          userRepository.save(createdUser);

          yield new RegisterResponse(createdUser.getId());
        }
        case UserCreationResult.Failure failure ->
            throw new RuntimeException("Registration failed: " + failure.error());
      };
    } finally {
      if (createdUser != null) createdUser.close();
    }
  }
}
