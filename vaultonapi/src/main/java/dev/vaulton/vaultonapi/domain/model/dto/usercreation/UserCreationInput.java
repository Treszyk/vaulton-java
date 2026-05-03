package dev.vaulton.vaultonapi.domain.model.dto.usercreation;

import dev.vaulton.vaultonapi.domain.crypto.CryptoValidators;
import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import java.util.UUID;

public record UserCreationInput(
    UUID accountId,
    SecureBuffer verifier,
    SecureBuffer adminVerifier,
    SecureBuffer rkVerifier,
    SecureBuffer sPwd,
    KdfMode kdfMode,
    EncryptedValue mkWrapPwd,
    EncryptedValue mkWrapRk,
    int cryptoSchemaVer) {
  public boolean isValid() {
    return accountId != null
        && verifier != null
        && adminVerifier != null
        && rkVerifier != null
        && sPwd != null
        && kdfMode != null
        && CryptoValidators.isValidMasterKey(mkWrapPwd)
        && CryptoValidators.isValidMasterKey(mkWrapRk);
  }
}
