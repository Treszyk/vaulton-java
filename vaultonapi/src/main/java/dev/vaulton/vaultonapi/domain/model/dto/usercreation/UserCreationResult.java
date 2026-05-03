package dev.vaulton.vaultonapi.domain.model.dto.usercreation;

import dev.vaulton.vaultonapi.domain.model.User;

public sealed interface UserCreationResult {
  record Success(User user) implements UserCreationResult {}

  record Failure(UserCreationError error) implements UserCreationResult {}
}
