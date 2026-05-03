package dev.vaulton.vaultonapi.domain.model.dto.registration;

import dev.vaulton.vaultonapi.domain.crypto.EncryptedValue;
import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;
import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import java.util.UUID;

public record RegistrationInput(
    UUID accountId,
    SecureBuffer verifier,
    SecureBuffer adminVerifier,
    SecureBuffer rkVerifier,
    SecureBuffer sPwd,
    KdfMode kdfMode,
    EncryptedValue mkWrapPwd,
    EncryptedValue mkWrapRk,
    int cryptoSchemaVer) {}
