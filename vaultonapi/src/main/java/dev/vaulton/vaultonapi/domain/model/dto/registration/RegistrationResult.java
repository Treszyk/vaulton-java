package dev.vaulton.vaultonapi.domain.model.dto.registration;

import dev.vaulton.vaultonapi.domain.model.User;

public sealed interface RegistrationResult {
  record Success(User user) implements RegistrationResult {}

  record Failure(RegistrationError error) implements RegistrationResult {}
}
