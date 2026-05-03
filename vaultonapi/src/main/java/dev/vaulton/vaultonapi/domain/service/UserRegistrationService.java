package dev.vaulton.vaultonapi.domain.service;

import dev.vaulton.vaultonapi.domain.model.User;
import dev.vaulton.vaultonapi.domain.model.dto.RegistrationInput;

public interface UserRegistrationService {
  User createUser(RegistrationInput input);
}
