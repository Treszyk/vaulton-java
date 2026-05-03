package dev.vaulton.vaultonapi.domain.service.registration;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationError;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationInput;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
  private record SaltedVerifier(SecureBuffer stored, SecureBuffer salt) {}

  private final CryptoService cryptoService;
  private final UserRepository userRepository;

  public UUID preRegister() {
    UUID id = UUID.randomUUID();

    while (userRepository.existsById(id)) {
      id = UUID.randomUUID();
    }

    return id;
  }

  @Override
  public RegistrationResult createUser(RegistrationInput input) {
    if (input.cryptoSchemaVer() != 1)
      return new RegistrationResult.Failure(RegistrationError.UNSUPPORTED_CRYPTO_SCHEMA);

    if (userRepository.existsById(input.accountId()))
      return new RegistrationResult.Failure(RegistrationError.ACCOUNT_EXISTS);

    SaltedVerifier login = compute(input.verifier());
    SaltedVerifier admin = compute(input.adminVerifier());
    SaltedVerifier rk = compute(input.rkVerifier());

    User newUser =
        User.builder()
            .id(input.accountId())
            .verifier(login.stored())
            .saltVerifier(login.salt())
            .adminVerifier(admin.stored())
            .saltAdminVerifier(admin.salt())
            .saltPwd(input.sPwd())
            .kdfMode(input.kdfMode())
            .mkWrapPwd(input.mkWrapPwd())
            .mkWrapRk(input.mkWrapRk())
            .rkVerifier(rk.stored())
            .saltRk(rk.salt())
            .cryptoSchemaVer(input.cryptoSchemaVer())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .failedLoginCount(0)
            .build();

    return new RegistrationResult.Success(newUser);
  }

  private SaltedVerifier compute(SecureBuffer raw) {
    SecureBuffer salt = cryptoService.generateRandomBytes(CryptoConstants.SALT_LEN);
    SecureBuffer stored = cryptoService.computeStoredVerifier(raw, salt);

    return new SaltedVerifier(stored, salt);
  }
}
