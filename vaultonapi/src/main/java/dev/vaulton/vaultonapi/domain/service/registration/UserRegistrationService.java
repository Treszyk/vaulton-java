package dev.vaulton.vaultonapi.domain.service.registration;

import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationInput;
import dev.vaulton.vaultonapi.domain.model.dto.registration.RegistrationResult;
import java.util.UUID;

public interface UserRegistrationService {
  UUID preRegister();

  RegistrationResult createUser(RegistrationInput input);
}
