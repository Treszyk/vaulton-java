package dev.vaulton.vaultonapi.domain.service.shared;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import java.util.UUID;

public interface CryptoService {
  SecureBuffer computeStoredVerifier(SecureBuffer verifierRaw, SecureBuffer salt);

  SecureBuffer generateRandomBytes(int length);

  SecureBuffer computeFakeSalt(UUID accountId);
}
