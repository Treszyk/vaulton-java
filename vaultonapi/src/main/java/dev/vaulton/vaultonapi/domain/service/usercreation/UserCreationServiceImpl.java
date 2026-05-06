package dev.vaulton.vaultonapi.domain.service.usercreation;

import dev.vaulton.vaultonapi.domain.crypto.CryptoConstants;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationError;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationInput;
import dev.vaulton.vaultonapi.domain.model.dto.usercreation.UserCreationResult;
import dev.vaulton.vaultonapi.domain.repository.UserRepository;
import dev.vaulton.vaultonapi.domain.service.shared.CryptoService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCreationServiceImpl implements UserCreationService {
  private record SaltedVerifier(SecureBuffer stored, SecureBuffer salt) {
    void wipe() {
      if (stored != null) stored.wipe();
      if (salt != null) salt.wipe();
    }
  }

  private final int pbkdf2Iterations;
  private final byte[] verifierPepper;
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
  public UserCreationResult createUser(UserCreationInput input) {
    if (input == null || !input.isValid())
      return new UserCreationResult.Failure(UserCreationError.INVALID_CRYPTO_BLOB);

    if (input.cryptoSchemaVer() != 1)
      return new UserCreationResult.Failure(UserCreationError.UNSUPPORTED_CRYPTO_SCHEMA);

    SaltedVerifier login = null;
    SaltedVerifier admin = null;
    SaltedVerifier rk = null;

    // this is not ideal probably, but it's the only thing I can think of for now
    boolean success = false;

    try {
      // We perform the computation before checking for existence to equalize the
      // response time and prevent Account ID enumeration (Timing Attack).
      login = compute(input.verifier());
      admin = compute(input.adminVerifier());
      rk = compute(input.rkVerifier());

      if (userRepository.existsById(input.accountId())) {
        return new UserCreationResult.Failure(UserCreationError.ACCOUNT_EXISTS);
      }

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

      success = true;
      // Note: The created User is returned to the Application layer for persistence orchestration.
      // this service is a Domain Factory, not a transactional workflow.
      // defensive cloning is intentionally omitted to minimize secret copies in memory
      // ownership and lifecycle management are delegated to the application orchestrator.
      return new UserCreationResult.Success(newUser);
    } finally {
      if (!success) {
        if (login != null) login.wipe();
        if (admin != null) admin.wipe();
        if (rk != null) rk.wipe();
      }
    }
  }

  private SaltedVerifier compute(SecureBuffer raw) {
    SecureBuffer salt = cryptoService.generateRandomBytes(CryptoConstants.SALT_LEN);
    try {
      SecureBuffer stored =
          cryptoService.computeStoredVerifier(raw, salt, pbkdf2Iterations, verifierPepper);
      return new SaltedVerifier(stored, salt);
    } catch (Exception e) {
      salt.wipe();
      throw e;
    }
  }
}
