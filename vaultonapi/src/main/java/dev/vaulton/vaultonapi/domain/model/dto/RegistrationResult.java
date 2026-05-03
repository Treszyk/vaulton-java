package dev.vaulton.vaultonapi.domain.model.dto;

import dev.vaulton.vaultonapi.domain.crypto.SecureBuffer;

public record RegistrationResult(
    SecureBuffer sVerifier,
    SecureBuffer verifier,
    SecureBuffer sAdminVerifier,
    SecureBuffer adminVerifier,
    SecureBuffer sRk,
    SecureBuffer rkVerifier) {}
